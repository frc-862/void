package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.lightningrobotics.common.logging.DataLogger;
import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.constants.RobotMap;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ClimbArms extends SubsystemBase {
	private TalonFX leftArm;
	private TalonFX rightArm;


	LightningIMU imu;

	//initialize set point for arm height
	private double armsTarget = 0;

	private double startTime;

	private boolean isSettled = false;
	private boolean checkIfSettled = true;

	//pid tuning stuff, will be removed later
	private ShuffleboardTab climbTab = Shuffleboard.getTab("climber");
	private NetworkTableEntry leftArmPos = climbTab.add("left arm", 100).getEntry();
	private NetworkTableEntry rightArmPos = climbTab.add("right arm", 100).getEntry();
	private NetworkTableEntry gyroPitch = climbTab.add("pitch", 0).getEntry();
	private NetworkTableEntry isSettledEntry = climbTab.add("is settled", false).getEntry();
	
  	public ClimbArms(LightningIMU imu) {
		// Sets the IDs of our arm motors
		leftArm = new TalonFX(RobotMap.LEFT_CLIMB);
		rightArm = new TalonFX(RobotMap.RIGHT_CLIMB);

		this.imu = imu;

		//climb motors need to be in brake mode to hold climb up
		leftArm.setNeutralMode(NeutralMode.Brake);
		rightArm.setNeutralMode(NeutralMode.Brake);


		//set arm inverts
		leftArm.setInverted(false);
		rightArm.setInverted(true);


		resetEncoders();
		setGains();

		initLogging();

		startTime = Timer.getFPGATimestamp();

		CommandScheduler.getInstance().registerSubsystem(this);
	}

	private void initLogging() {
		DataLogger.addDataElement("leftArmPosition", () -> leftArm.getSelectedSensorPosition());
		DataLogger.addDataElement("rightArmPosition", () -> rightArm.getSelectedSensorPosition());
		DataLogger.addDataElement("armsTarget", () -> armsTarget);
		DataLogger.addDataElement("gyro pitch", () -> imu.getPitch().getDegrees());
	}

	private void setGains() {
		leftArm.config_kF(1, 0d);
		leftArm.config_kP(1, 1d);
		leftArm.config_kI(1, 0);
		leftArm.config_kD(1, 0);
		leftArm.configAllowableClosedloopError(1, 750);

		rightArm.config_kF(1, 0d);
		rightArm.config_kP(1, 1d);
		rightArm.config_kI(1, 0);
		rightArm.config_kD(1, 0);
		rightArm.configAllowableClosedloopError(1, 1000);

		// leftArm.config_kP(0, 0.07);
		// leftArm.config_kP(0, 0.07);

	}

	public void setPower(double leftPower, double rightPower) {
		leftArm.set(TalonFXControlMode.PercentOutput, leftPower);
		rightArm.set(TalonFXControlMode.PercentOutput, rightPower);
	}

	
	/**
	 * @param armTarget desired set point, in encoder ticks
	 * @param climbMode 0 for unloaded PID, 1 for loaded
	 */
	public void setTarget(double armTarget) {
		System.out.println("setting arm target _______________________________------");
		armsTarget = LightningMath.constrain(armTarget, 0, Constants.MAX_ARM_VALUE);

		leftArm.selectProfileSlot(1, 0);
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

	public boolean isSettled() {
		return isSettled;
	}


	/**
	 * @return true if the arms are within a given threshhold
	 */
	public boolean onTarget() {
		return Math.abs(leftArm.getSelectedSensorPosition() - armsTarget) < Constants.ARM_TARGET_THRESHOLD && 
			   Math.abs(rightArm.getSelectedSensorPosition() - armsTarget) < Constants.ARM_TARGET_THRESHOLD;
	}
	
	/**
	 * checks if the gyro is settled
	 */
	private void checkIfSettled() {
		//if our gyro angle is within a threshold, start a timer
		if(Math.abs(imu.getPitch().getDegrees() - Constants.ON_RUNG_ANGLE) < Constants.GYRO_SETTLE_THRESHOLD && checkIfSettled) {
			startTime = Timer.getFPGATimestamp();

			checkIfSettled = false;
		} else if(Math.abs(imu.getPitch().getDegrees() - Constants.ON_RUNG_ANGLE) > Constants.GYRO_SETTLE_THRESHOLD) {
			startTime = Timer.getFPGATimestamp();

			checkIfSettled = true;
		}

		//if the gyro is within the threshold for a certain amount of time, we have settled
		if(startTime - Timer.getFPGATimestamp() > Constants.GYRO_SETTLE_TIME) {
			isSettled = true;
		} else {
			isSettled = false;
		}

		isSettledEntry.setBoolean(isSettled);

	}

	// private void setArmPIDGains(double kP_load, double kI_load, double kD_load, double kF_load, double kP_noLoad) {//, double kI_noLoad, double kD_noLoad) {
	// 	leftArm.config_kP(0, kP_noLoad);
	// 	// leftArm.config_kI(0, kI_noLoad);
	// 	// leftArm.config_kD(0, kD_noLoad);

	// 	rightArm.config_kP(0, kP_noLoad);
	// 	// rightArm.config_kI(0, kI_noLoad);
	// 	// rightArm.config_kD(0, kD_noLoad);

	// 	leftArm.config_kP(1, kP_load);
	// 	leftArm.config_kI(1, kI_load);
	// 	leftArm.config_kD(1, kD_load);
	// 	leftArm.config_kF(1, kF_load);

	// 	rightArm.config_kP(1, kP_load);
	// 	rightArm.config_kI(1, kI_load);
	// 	rightArm.config_kD(1, kD_load);
	// 	rightArm.config_kF(1, kF_load);
	// }

	@Override
	public void periodic() {
		leftArmPos.setNumber(leftArm.getSelectedSensorPosition());
		rightArmPos.setNumber(rightArm.getSelectedSensorPosition());
		gyroPitch.setNumber(imu.getPitch().getDegrees());
	}
	

	public void stop() {
		setPower(0, 0);
	}

}
