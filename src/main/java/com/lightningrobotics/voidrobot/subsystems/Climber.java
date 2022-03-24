package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.lightningrobotics.common.logging.DataLogger;
import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.constants.RobotMap;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Climber extends SubsystemBase {
	private TalonFX leftArm;
	private TalonFX rightArm;

	private TalonSRX leftPivot;
	private TalonSRX rightPivot;

	//initialize set point for arm height
	private double armsTarget = 0;

	//pid tuning stuff, will be removed later
	private ShuffleboardTab climbTab = Shuffleboard.getTab("climber");
	private NetworkTableEntry resetClimb = climbTab.add("reset climb", false).getEntry();
	private NetworkTableEntry disableClimb = climbTab.add("disable climb", false).getEntry();
	private NetworkTableEntry leftArmPos = climbTab.add("left arm", 100).getEntry();
	private NetworkTableEntry rightArmPos = climbTab.add("right arm", 100).getEntry();
	private NetworkTableEntry targetClimb = climbTab.add("target climb", 0).getEntry();
	private NetworkTableEntry loaded = climbTab.add("has load", 0).getEntry();

	private NetworkTableEntry kP_load = climbTab.add("kP with load", 0).getEntry();
	private NetworkTableEntry kI_load = climbTab.add("kI with load", 0).getEntry();
	private NetworkTableEntry kD_load = climbTab.add("kD with load", 0).getEntry();
	private NetworkTableEntry kF_load = climbTab.add("kF with load", 0).getEntry();
	
	private NetworkTableEntry kP_noLoad = climbTab.add("kP without load", 0.07).getEntry();
	private NetworkTableEntry kI_noLoad = climbTab.add("kI without load", 0).getEntry();
	private NetworkTableEntry kD_noLoad = climbTab.add("kD without load", 0).getEntry();

  	public Climber() {
		// Sets the IDs of our arm motors
		leftArm = new TalonFX(RobotMap.LEFT_CLIMB);
		rightArm = new TalonFX(RobotMap.RIGHT_CLIMB);

		// Sets the IDs of the pivot motors
		leftPivot = new TalonSRX(RobotMap.LEFT_PIVOT);
		rightPivot = new TalonSRX(RobotMap.RIGHT_PIVOT);

		//climb motors need to be in brake mode to hold climb up
		leftArm.setNeutralMode(NeutralMode.Brake);
		rightArm.setNeutralMode(NeutralMode.Brake);

		//pivot motors need to be in brake mode to hold pivot in place
		leftPivot.setNeutralMode(NeutralMode.Brake);
		rightPivot.setNeutralMode(NeutralMode.Brake);

		//set arm inverts
		leftArm.setInverted(false);
		rightArm.setInverted(true);

		//set pivot inverts
		leftPivot.setInverted(false);
		rightPivot.setInverted(true);

		//set pivot motors to follow each other
		leftPivot.follow(rightPivot);

		initLogging();

		CommandScheduler.getInstance().registerSubsystem(this);
	}

	private void initLogging() {
		DataLogger.addDataElement("leftClimbPosition", () -> leftArm.getSelectedSensorPosition());
		DataLogger.addDataElement("rightClimbPosition", () -> rightArm.getSelectedSensorPosition());
	}

	public void setClimbPower(double leftPower, double rightPower) {
		leftArm.set(TalonFXControlMode.PercentOutput, leftPower);
		rightArm.set(TalonFXControlMode.PercentOutput, rightPower);
	}

	public void setPivotPower(double power) {
		//only set one as the left motor is set to follow the right
		rightPivot.set(TalonSRXControlMode.PercentOutput, power);
	}

	public void pivotToHold() {
		setPivotPower(-Constants.DEFAULT_PIVOT_POWER); //limit switch will stop it
	}

	public void pivotToReach() {
		setPivotPower(Constants.DEFAULT_PIVOT_POWER); //limit switch will stop it
	}

	public void resetArmEncoders() {
		leftArm.setSelectedSensorPosition(0);
		rightArm.setSelectedSensorPosition(0);
	}

	public boolean getReachSensor() {
		return leftPivot.isRevLimitSwitchClosed() == 1;
	}

	public boolean getHoldSensor() {
		return leftPivot.isFwdLimitSwitchClosed() == 1;
	}

	public boolean isSettled() {
		return true; //TODO: implement gyro
	}

	public boolean pivotOnTarget() {
		if(rightPivot.getMotorOutputPercent() == -1) {
			return getHoldSensor();
		} else {
			return getReachSensor();
		}
	}

	public boolean armsOnTarget() {
		return Math.abs(leftArm.getSelectedSensorPosition() - armsTarget) < Constants.ARM_TARGET_THRESHOLD && 
			   Math.abs(rightArm.getSelectedSensorPosition() - armsTarget) < Constants.ARM_TARGET_THRESHOLD;
	}

	public boolean onTarget() {
		return pivotOnTarget() && armsOnTarget();
	}

											//0 for without load, 1 for with
	public void setArmsTarget(double armTarget, int climbMode) {
		this.armsTarget = LightningMath.constrain(armTarget, 0, Constants.MAX_ARM_VALUE);

		rightArm.selectProfileSlot(climbMode, climbMode);
	}

	private void setArmPIDGains(double kP_load, double kI_load, double kD_load, double kF_load, double kP_noLoad, double kI_noLoad, double kD_noLoad) {
		leftArm.config_kP(0, kP_noLoad);
		leftArm.config_kI(0, kI_noLoad);
		leftArm.config_kD(0, kD_noLoad);

		rightArm.config_kP(0, kP_noLoad);
		rightArm.config_kI(0, kI_noLoad);
		rightArm.config_kD(0, kD_noLoad);

		leftArm.config_kP(1, kP_load);
		leftArm.config_kI(1, kI_load);
		leftArm.config_kD(1, kD_load);
		rightArm.config_kF(1, kF_load);

		rightArm.config_kP(1, kP_load);
		rightArm.config_kI(1, kI_load);
		rightArm.config_kD(1, kD_load);
		rightArm.config_kF(1, kF_load);
	}

	@Override
	public void periodic() {
		setArmsTarget(targetClimb.getDouble(100), (int)loaded.getDouble(0));

		leftArmPos.setNumber(leftArm.getSelectedSensorPosition());
		rightArmPos.setNumber(rightArm.getSelectedSensorPosition());

		if(resetClimb.getBoolean(false)) {
			resetArmEncoders();
		}

		if(disableClimb.getBoolean(false)) {
			leftArm.set(TalonFXControlMode.PercentOutput, 0);
			rightArm.set(TalonFXControlMode.PercentOutput, 0);
		} else {
			leftArm.set(TalonFXControlMode.Position, armsTarget);
			rightArm.set(TalonFXControlMode.Position, armsTarget);
		}

		setArmPIDGains(kP_load.getDouble(0), kI_load.getDouble(0), kD_load.getDouble(0), kF_load.getDouble(0), kP_noLoad.getDouble(0.07), kI_noLoad.getDouble(0), kD_noLoad.getDouble(0));

	}

	public void stopPivot() {
		setPivotPower(0);
	}

	public void stopArms() {
		setClimbPower(0, 0);
	}

	public void stop() {
		stopArms();
		stopPivot();
	}

}
