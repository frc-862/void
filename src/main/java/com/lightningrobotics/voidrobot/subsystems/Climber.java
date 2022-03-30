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

public class Climber extends SubsystemBase {
	private TalonFX leftArm;
	private TalonFX rightArm;

	private TalonSRX leftPivot;
	private TalonSRX rightPivot;

	LightningIMU imu;

	//initialize set point for arm height
	private double armsTarget = 0;
	private double pivotPower = 0;

	private double startTime;

	private boolean isSettled = false;
	private boolean checkIfSettled = true;
	private enum pivotPosition {
		hold,
		reach,
		moving
	}

	private pivotPosition pivotState = pivotPosition.reach;

	//pid tuning stuff, will be removed later
	private ShuffleboardTab climbTab = Shuffleboard.getTab("climber");
	private NetworkTableEntry leftArmPos = climbTab.add("left arm", 100).getEntry();
	private NetworkTableEntry rightArmPos = climbTab.add("right arm", 100).getEntry();
	private NetworkTableEntry gyroPitch = climbTab.add("pitch", 0).getEntry();
	private NetworkTableEntry isSettledEntry = climbTab.add("is settled", false).getEntry();
	
  	public Climber(LightningIMU imu) {
		// Sets the IDs of our arm motors
		leftArm = new TalonFX(RobotMap.LEFT_CLIMB);
		rightArm = new TalonFX(RobotMap.RIGHT_CLIMB);

		// Sets the IDs of the pivot motors
		leftPivot = new TalonSRX(RobotMap.LEFT_PIVOT);
		rightPivot = new TalonSRX(RobotMap.RIGHT_PIVOT);

		this.imu = imu;

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

		initLogging();

		startTime = Timer.getFPGATimestamp();

		CommandScheduler.getInstance().registerSubsystem(this);
	}

	private void initLogging() {
		DataLogger.addDataElement("leftArmPosition", () -> leftArm.getSelectedSensorPosition());
		DataLogger.addDataElement("rightArmPosition", () -> rightArm.getSelectedSensorPosition());
		DataLogger.addDataElement("armsTarget", () -> armsTarget);
		DataLogger.addDataElement("pivot position", () -> pivotState.toString());
		DataLogger.addDataElement("pivot power", () -> rightPivot.getMotorOutputPercent());
		DataLogger.addDataElement("gyro pitch", () -> imu.getPitch().getDegrees());
	}

	public void setClimbPower(double leftPower, double rightPower) {
		leftArm.set(TalonFXControlMode.PercentOutput, leftPower);
		rightArm.set(TalonFXControlMode.PercentOutput, rightPower);
	}

	public void setPivotPower(double leftPower, double rightPower) {
		//only set one as the left motor is set to follow the right
		rightPivot.set(TalonSRXControlMode.PercentOutput, rightPower);
		leftPivot.set(TalonSRXControlMode.PercentOutput, leftPower);

		pivotPower = (rightPower+leftPower)/2;
	}

	/**
	 * @param armTarget desired set point, in encoder ticks
	 * @param climbMode 0 for unloaded PID, 1 for loaded
	 */
	public void setArmsTarget(double armTarget) {
		System.out.println("setting arm target _______________________________------");
		armsTarget = LightningMath.constrain(armTarget, 0, Constants.MAX_ARM_VALUE);
		leftArm.set(TalonFXControlMode.Position, armsTarget);
		rightArm.set(TalonFXControlMode.Position, armsTarget);
	}

	/**
	 * run the pivots towards collector until they hit the limit switch
	 */
	public void pivotToHold() {
		System.out.println("running pviot ot hold_____________________");
		setPivotPower(Constants.DEFAULT_PIVOT_POWER, Constants.DEFAULT_PIVOT_POWER);
	}

	/**
	 * run the pivots away from collector until they hit the limit switch
	 */
	public void pivotToReach() {
		System.out.println("running pviot ot reach_______________________");
		setPivotPower(-Constants.DEFAULT_PIVOT_POWER, -Constants.DEFAULT_PIVOT_POWER);
	}

	public void resetArmEncoders() {
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
	 * @return true if the pivot is at its far limit from the collector
	 */
	public boolean getLeftReachSensor() {
		return leftPivot.isRevLimitSwitchClosed() == 1;
	}

	/**
	 * @return true if the pivot is at its near limit to the collector
	 */
	public boolean getLeftHoldSensor() {
		return leftPivot.isFwdLimitSwitchClosed() == 1;
	}

	/**
	 * @return true if the pivot is at its far limit from the collector
	 */
	public boolean getRightReachSensor() {
		return rightPivot.isRevLimitSwitchClosed() == 1;
	}

	/**
	 * @return true if the pivot is at its near limit to the collector
	 */
	public boolean getRightHoldSensor() {
		return rightPivot.isFwdLimitSwitchClosed() == 1;
	}

	public boolean isSettled() {
		return isSettled;
	}

	/**
	 * @return true if the pivot is triggering the appropriate sensor
	 */
	public boolean pivotOnTarget() {
		if(pivotPower == Constants.DEFAULT_PIVOT_POWER) {
			return getLeftHoldSensor() && getRightHoldSensor();
		} else {
			return getLeftReachSensor() && getRightReachSensor();
		}
	}

	/**
	 * @return true if the arms are within a given threshhold
	 */
	public boolean armsOnTarget() {
		return Math.abs(leftArm.getSelectedSensorPosition() - armsTarget) < Constants.ARM_TARGET_THRESHOLD && 
			   Math.abs(rightArm.getSelectedSensorPosition() - armsTarget) < Constants.ARM_TARGET_THRESHOLD;
	}

	/**
	 * @return true if both the arms and pivots are on target
	 */
	public boolean onTarget() {
		return pivotOnTarget() && armsOnTarget();
	}

	/**
	 * checks the pivot state based on which sensor is being triggered
	 */
	private void checkPivotState() {
		if(getLeftHoldSensor() && getRightHoldSensor()) {
			pivotState = pivotPosition.hold;
		} else if(getLeftReachSensor() && getRightReachSensor()) {
			pivotState = pivotPosition.reach;
		} else {
			pivotState = pivotPosition.moving;
		}
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

		checkPivotState();
	}
	public void stopPivot() {
		setPivotPower(0, 0);
	}

	public void stopArms() {
		setClimbPower(0, 0);
	}

	public void stop() {
		stopArms();
		stopPivot();
	}

}
