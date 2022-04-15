package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.lightningrobotics.common.logging.DataLogger;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.constants.RobotMap;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ClimbArms extends SubsystemBase {
	private TalonFX leftArm;
	private TalonFX rightArm;

	//initialize set point for arm height
	private double armsTarget = 0;

	private ShuffleboardTab climbTab = Shuffleboard.getTab("climber");
	private NetworkTableEntry leftArmPos =  climbTab.add("left arm",  -1000).getEntry();
	private NetworkTableEntry rightArmPos = climbTab.add("right arm", -1000).getEntry();

  	public ClimbArms() {
		// Sets the IDs of our arm motors
		leftArm = new TalonFX(RobotMap.LEFT_CLIMB);
		rightArm = new TalonFX(RobotMap.RIGHT_CLIMB);

		//climb motors need to be in brake mode to hold climb up
		leftArm.setNeutralMode(NeutralMode.Brake);
		rightArm.setNeutralMode(NeutralMode.Brake);

		//set arm inverts
		leftArm.setInverted(false);
		rightArm.setInverted(true);

		resetEncoders();
		setGains();
		initLogging();
		CommandScheduler.getInstance().registerSubsystem(this);
	}

	private void initLogging() {
		DataLogger.addDataElement("leftArmPosition", () -> leftArm.getSelectedSensorPosition());
		DataLogger.addDataElement("rightArmPosition", () -> rightArm.getSelectedSensorPosition());
		DataLogger.addDataElement("armsTarget", () -> armsTarget);
	}

	private void setGains() {
		leftArm.config_kF(1, 0d);
		leftArm.config_kP(1, Constants.ARM_KP);
		leftArm.config_kI(1, 0);
		leftArm.config_kD(1, 0);
		leftArm.configAllowableClosedloopError(1, Constants.ARM_TARGET_THRESHOLD);

		rightArm.config_kF(1, 0d);
		rightArm.config_kP(1, Constants.ARM_KP);
		rightArm.config_kI(1, 0);
		rightArm.config_kD(1, 0);
		rightArm.configAllowableClosedloopError(1, Constants.ARM_TARGET_THRESHOLD);

	}

	public void setPower(double leftPower, double rightPower) {
		leftArm.set(TalonFXControlMode.PercentOutput, leftPower);
		rightArm.set(TalonFXControlMode.PercentOutput, rightPower);
	}

	public boolean getUpperLimitSwitches() {
		return leftArm.isFwdLimitSwitchClosed() == 1 && rightArm.isFwdLimitSwitchClosed() == 1;
	}

	public boolean getLowerLimitSwitches() {
		return leftArm.isRevLimitSwitchClosed() == 1 && rightArm.isRevLimitSwitchClosed() == 1;
	}

	/**
	 * @param armTarget desired set point, in encoder ticks
	 */
	public void setTarget(double armTarget) {
		leftArm.selectProfileSlot(1, 0);
		rightArm.selectProfileSlot(1, 0);
		leftArm.set(TalonFXControlMode.Position, armsTarget);
		rightArm.set(TalonFXControlMode.Position, armsTarget);
	}

	
	public void resetEncoders() {
		leftArm.setSelectedSensorPosition(0);
		rightArm.setSelectedSensorPosition(0);
	}

	public double getleftEncoder() {
		return leftArm.getSelectedSensorPosition();
	}

	public double getRightEncoder() {
		return rightArm.getSelectedSensorPosition();
	}


	/**
	 * @return true if the arms are within a given threshhold
	 */
	public boolean onTarget() {
		return Math.abs(leftArm.getSelectedSensorPosition()  - armsTarget) < Constants.ARM_TARGET_THRESHOLD && 
			   Math.abs(rightArm.getSelectedSensorPosition() - armsTarget) < Constants.ARM_TARGET_THRESHOLD;
	}
	//here lies jusnoor's gyro code
	//shouldnt have been merged lol

	@Override
	public void periodic() {
		leftArmPos.setNumber(leftArm.getSelectedSensorPosition());
		rightArmPos.setNumber(rightArm.getSelectedSensorPosition());	
		SmartDashboard.putBoolean("upper limits", getUpperLimitSwitches());
		SmartDashboard.putBoolean("lower limits", getLowerLimitSwitches());
		if (getLowerLimitSwitches()) {
			resetEncoders();
		}
	}
	
	public void stop() {
		setPower(0, 0);
	}

}
