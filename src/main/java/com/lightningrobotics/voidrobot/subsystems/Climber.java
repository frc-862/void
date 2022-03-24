package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.lightningrobotics.common.controller.PIDFController;
import com.lightningrobotics.common.logging.DataLogger;
import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.constants.RobotMap;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Climber extends SubsystemBase {

	// create some empty talonfx objects
	private TalonFX leftElevator;
	private TalonFX rightElevator;

	private TalonSRX leftClimbPivot;
	private TalonSRX rightClimbPivot;

	private DigitalInput pivotVerticalSensor;
	private DigitalInput pivotReachSensor;

	private PIDFController pivotPID = new PIDFController(1, 1, 1);

	private int climbMode = 0;
	private double winchTarget = 0;

	private double pivotMotorOutput = 0;

	private ShuffleboardTab climbTab = Shuffleboard.getTab("climber");
	private NetworkTableEntry resetClimb = climbTab.add("reset climb", false).getEntry();
	private NetworkTableEntry disableClimb = climbTab.add("disable climb", false).getEntry();
	private NetworkTableEntry leftWinchPos = climbTab.add("left winch", 100).getEntry();
	private NetworkTableEntry rightWinchPos = climbTab.add("right winch", 100).getEntry();
	private NetworkTableEntry targetClimb = climbTab.add("target climb", 0).getEntry();
	private NetworkTableEntry loaded = climbTab.add("has load", 0).getEntry();

	private NetworkTableEntry kP_load = climbTab.add("kP with load", 0).getEntry();
	private NetworkTableEntry kI_load = climbTab.add("kI with load", 0).getEntry();
	private NetworkTableEntry kD_load = climbTab.add("kD with load", 0).getEntry();
	private NetworkTableEntry kF_load = climbTab.add("kF with load", 0).getEntry();
	
	private NetworkTableEntry kP_noLoad = climbTab.add("kP without load", 0.07).getEntry();
	private NetworkTableEntry kI_noLoad = climbTab.add("kI without load", 0).getEntry();
	private NetworkTableEntry kD_noLoad = climbTab.add("kD without load", 0).getEntry();


	public enum pivotTarget {
		reach,
		hold,
		stowed
	}

	private enum state {
		hold, // either traversal or main climber engaged
		reach, // reaching for a rung
		settling
	  }

	private pivotTarget currentPivotTarget = pivotTarget.stowed;
	private pivotTarget previousPivotTarget = pivotTarget.stowed;

  	public Climber() {
		// Sets the IDs of our winch motors
		leftElevator = new TalonFX(RobotMap.LEFT_CLIMB);
		rightElevator = new TalonFX(RobotMap.RIGHT_CLIMB);

		// Sets the IDs of the pivot motors
		leftClimbPivot = new TalonSRX(RobotMap.LEFT_PIVOT);
		rightClimbPivot = new TalonSRX(RobotMap.RIGHT_PIVOT);

		//climb motors need to be in brake mode to hold climb up
		leftElevator.setNeutralMode(NeutralMode.Brake);
		rightElevator.setNeutralMode(NeutralMode.Brake);

		//pivot motors need to be in brake mode to hold pivot in place
		leftClimbPivot.setNeutralMode(NeutralMode.Brake);
		rightClimbPivot.setNeutralMode(NeutralMode.Brake);

		leftElevator.setInverted(false);
		rightElevator.setInverted(true);

		leftClimbPivot.setInverted(false);
		rightClimbPivot.setInverted(true);

		pivotVerticalSensor = new DigitalInput(RobotMap.VERTICAL_PIVOT_SENSOR_ID);
		pivotReachSensor = new DigitalInput(RobotMap.REACHING_PIVOT_SENSOR_ID); //TODO: make limit switch

		initLogging();

		CommandScheduler.getInstance().registerSubsystem(this);
	}

	private void initLogging() {
		DataLogger.addDataElement("leftClimbPosition", () -> leftElevator.getSelectedSensorPosition());
		DataLogger.addDataElement("rightClimbPosition", () -> rightElevator.getSelectedSensorPosition());
	}

	public void setClimbPower(double leftPower, double rightPower) {
		leftElevator.set(TalonFXControlMode.PercentOutput, leftPower);
		rightElevator.set(TalonFXControlMode.PercentOutput, rightPower);
	}

	public void setPivotPower(double leftPower, double rightPower) {
		leftClimbPivot.set(TalonSRXControlMode.PercentOutput, leftPower);
		rightClimbPivot.set(TalonSRXControlMode.PercentOutput, rightPower);
	}

	public void stop() {
		setClimbPower(0, 0);
		setPivotPower(0, 0);
	}
											//0 for without load, 1 for with
	public void climbSetPoint(double winchTarget, int climbMode) {
		this.winchTarget = LightningMath.constrain(winchTarget, 0, Constants.MAX_ELEVATOR_VALUE);
		this.climbMode = climbMode;

		rightElevator.selectProfileSlot(climbMode, climbMode);
	}
	public void setWinchTarget(double target) {
		winchTarget = target;
	}
	public void resetWinchEncoders() {
		leftElevator.setSelectedSensorPosition(0);
		rightElevator.setSelectedSensorPosition(0);
	}
	private void setWinchPIDGains(double kP_load, double kI_load, double kD_load, double kF_load, double kP_noLoad, double kI_noLoad, double kD_noLoad) {
		leftElevator.config_kP(0, kP_noLoad);
		leftElevator.config_kI(0, kI_noLoad);
		leftElevator.config_kD(0, kD_noLoad);

		rightElevator.config_kP(0, kP_noLoad);
		rightElevator.config_kI(0, kI_noLoad);
		rightElevator.config_kD(0, kD_noLoad);

		leftElevator.config_kP(1, kP_load);
		leftElevator.config_kI(1, kI_load);
		leftElevator.config_kD(1, kD_load);
		rightElevator.config_kF(1, kF_load);

		rightElevator.config_kP(1, kP_load);
		rightElevator.config_kI(1, kI_load);
		rightElevator.config_kD(1, kD_load);
		rightElevator.config_kF(1, kF_load);
	}

	public boolean isWinchOnTarget() {
		return leftElevator.getSelectedSensorPosition() - winchTarget < Constants.WINCH_TARGET_THRESHOLD && rightElevator.getSelectedSensorPosition() - winchTarget < Constants.WINCH_TARGET_THRESHOLD;
	}

	public boolean isSettled() {
		return true; //TODO: implement gyro
	}

	public void setPivotTarget(pivotTarget target) {
		currentPivotTarget = target;
	}

	@Override
	public void periodic() {
		switch(currentPivotTarget) {
			case stowed:
				pivotMotorOutput = 0;
				previousPivotTarget = pivotTarget.stowed;
			break;
			case hold:
				if(previousPivotTarget == pivotTarget.stowed) {
					pivotMotorOutput = 1;
				} else if(previousPivotTarget == pivotTarget.reach) {
					pivotMotorOutput = -1;
				}

				if(pivotVerticalSensor.get()) {
					pivotMotorOutput = 0;
				}
			break;
			case reach:
				pivotMotorOutput = 1;
				previousPivotTarget = pivotTarget.reach;
				//limit switch will take care of this
			break;
		}

		// setPivotPower(pivotMotorOutput, pivotMotorOutput); //TODO: implement pivot

		climbSetPoint(targetClimb.getDouble(100), (int)loaded.getDouble(0));

		leftWinchPos.setNumber(leftElevator.getSelectedSensorPosition());
		rightWinchPos.setNumber(rightElevator.getSelectedSensorPosition());

		if(resetClimb.getBoolean(false)) {
			resetWinchEncoders();
		}

		if(!disableClimb.getBoolean(false)) {
			leftElevator.set(TalonFXControlMode.Position, winchTarget);
			rightElevator.set(TalonFXControlMode.Position, winchTarget);
		} else {
			leftElevator.set(TalonFXControlMode.PercentOutput, 0);
			rightElevator.set(TalonFXControlMode.PercentOutput, 0);
		}

		setWinchPIDGains(kP_load.getDouble(0), kI_load.getDouble(0), kD_load.getDouble(0), kF_load.getDouble(0), kP_noLoad.getDouble(0.07), kI_noLoad.getDouble(0), kD_noLoad.getDouble(0));

	}

}
