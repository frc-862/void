package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.lightningrobotics.common.controller.PIDFController;
import com.lightningrobotics.common.logging.DataLogger;
import com.lightningrobotics.voidrobot.constants.RobotMap;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Climber extends SubsystemBase {

	// create some empty talonfx objects
	private TalonFX leftClimbWinch;
	private TalonFX rightClimbWinch;

	private TalonSRX leftClimbPivot;
	private TalonSRX rightClimbPivot;

	private double pivotTarget;

	private PIDFController pivotPID = new PIDFController(1, 1, 1);

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
	
	private NetworkTableEntry kP_noLoad = climbTab.add("kP without load", 0).getEntry();
	private NetworkTableEntry kI_noLoad = climbTab.add("kI without load", 0).getEntry();
	private NetworkTableEntry kD_noLoad = climbTab.add("kD without load", 0).getEntry();


  	public Climber() {
		// Sets the IDs of our winch motors
		leftClimbWinch = new TalonFX(RobotMap.LEFT_CLIMB);
		rightClimbWinch = new TalonFX(RobotMap.RIGHT_CLIMB);

		// Sets the IDs of the pivot motors
		leftClimbPivot = new TalonSRX(RobotMap.LEFT_PIVOT);
		rightClimbPivot = new TalonSRX(RobotMap.RIGHT_PIVOT);

		//climb motors need to be in brake mode to hold climb up
		leftClimbWinch.setNeutralMode(NeutralMode.Brake);
		rightClimbWinch.setNeutralMode(NeutralMode.Brake);

		//pivot motors need to be in brake mode to hold pivot in place
		leftClimbPivot.setNeutralMode(NeutralMode.Brake);
		rightClimbPivot.setNeutralMode(NeutralMode.Brake);

		leftClimbWinch.setInverted(false);
		rightClimbWinch.setInverted(true);

		leftClimbPivot.setInverted(false);
		rightClimbPivot.setInverted(true);

		initLogging();

		CommandScheduler.getInstance().registerSubsystem(this);
	}

	private void initLogging() {
		DataLogger.addDataElement("leftClimbPosition", () -> leftClimbWinch.getSelectedSensorPosition());
		DataLogger.addDataElement("rightClimbPosition", () -> rightClimbWinch.getSelectedSensorPosition());
	}

	public void setClimbPower(double leftPower, double rightPower) {
		leftClimbWinch.set(TalonFXControlMode.PercentOutput, leftPower);
		rightClimbWinch.set(TalonFXControlMode.PercentOutput, rightPower);
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
	public void climbSetPoint(double input, int climbMode) {
		//Top constraint for left is 273,553
		//Top constraint for right is 272,576

		rightClimbWinch.selectProfileSlot(climbMode, climbMode);

		if(!disableClimb.getBoolean(false)) {
			leftClimbWinch.set(TalonFXControlMode.Position, input);
			rightClimbWinch.set(TalonFXControlMode.Position, input);
		} else {
			leftClimbWinch.set(TalonFXControlMode.PercentOutput, 0);
			rightClimbWinch.set(TalonFXControlMode.PercentOutput, 0);
		}
	}
	public void setWinchTarget(double target) {
		pivotTarget = target;
	}
	public void resetWinchEncoders() {
		leftClimbWinch.setSelectedSensorPosition(0);
		rightClimbWinch.setSelectedSensorPosition(0);
	}
	private void setWinchPIDGains(double kP_load, double kI_load, double kD_load, double kF_load, double kP_noLoad, double kI_noLoad, double kD_noLoad) {
		leftClimbWinch.config_kP(0, kP_noLoad);
		leftClimbWinch.config_kI(0, kI_noLoad);
		leftClimbWinch.config_kD(0, kD_noLoad);

		rightClimbWinch.config_kP(0, kP_noLoad);
		rightClimbWinch.config_kI(0, kI_noLoad);
		rightClimbWinch.config_kD(0, kD_noLoad);

		leftClimbWinch.config_kP(1, kP_load);
		leftClimbWinch.config_kI(1, kI_load);
		leftClimbWinch.config_kD(1, kD_load);
		rightClimbWinch.config_kF(1, kF_load);

		rightClimbWinch.config_kP(1, kP_load);
		rightClimbWinch.config_kI(1, kI_load);
		rightClimbWinch.config_kD(1, kD_load);
		rightClimbWinch.config_kF(1, kF_load);
	}

	@Override
	public void periodic() {
		double motorOutput = pivotPID.calculate(0, pivotTarget); //TODO: somehow get gyro input from this subsystem

		// setPivotPower(motorOutput, motorOutput);

		climbSetPoint(targetClimb.getDouble(100), (int)loaded.getDouble(0));

		leftWinchPos.setNumber(leftClimbWinch.getSelectedSensorPosition());
		rightWinchPos.setNumber(rightClimbWinch.getSelectedSensorPosition());

		if(resetClimb.getBoolean(false)) {
			resetWinchEncoders();
		}

		setWinchPIDGains(kP_load.getDouble(0), kI_load.getDouble(0), kD_load.getDouble(0), kF_load.getDouble(0), kP_noLoad.getDouble(0), kI_noLoad.getDouble(0), kD_noLoad.getDouble(0));

	}

}
