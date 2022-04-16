// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.lightningrobotics.common.logging.DataLogger;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.constants.RobotMap;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ClimbPivots extends SubsystemBase {
	private TalonSRX leftPivot;
	private TalonSRX rightPivot;

	private double pivotPower = 0;

	private enum pivotPosition {
		hold,
		reach,
		moving
	}

	private pivotPosition pivotState = pivotPosition.reach;

  public ClimbPivots() {
	// Sets the IDs of the pivot motors
	leftPivot = new TalonSRX(RobotMap.LEFT_PIVOT);
	rightPivot = new TalonSRX(RobotMap.RIGHT_PIVOT);


	//pivot motors need to be in brake mode to hold pivot in place
	leftPivot.setNeutralMode(NeutralMode.Brake);
	rightPivot.setNeutralMode(NeutralMode.Brake);

	//set pivot inverts
	leftPivot.setInverted(false);
	rightPivot.setInverted(true);

    initLogging();
  }
  private void initLogging() {
		DataLogger.addDataElement("pivot position", () -> pivotState == pivotPosition.hold ? 0 : (pivotState == pivotPosition.reach ? 2 : 3));
		DataLogger.addDataElement("right pivot power", () -> rightPivot.getMotorOutputPercent());
		DataLogger.addDataElement("left pivot power", () -> leftPivot.getMotorOutputPercent());

		DataLogger.addDataElement("left pivot reach", () -> leftPivot.isRevLimitSwitchClosed());
		DataLogger.addDataElement("right pivot reach", () -> rightPivot.isRevLimitSwitchClosed());
		DataLogger.addDataElement("left pivot hold", () -> leftPivot.isFwdLimitSwitchClosed());
		DataLogger.addDataElement("right pivot hold", () -> rightPivot.isFwdLimitSwitchClosed());
  }
  /**
	 * run the pivots towards collector until they hit the limit switch
	 */
	public void pivotToHold() {
		setPivotPower(Constants.DEFAULT_PIVOT_POWER, Constants.DEFAULT_PIVOT_POWER);
	}

	/**
	 * run the pivots away from collector until they hit the limit switch
	 */
	public void pivotToReach() {
		setPivotPower(-Constants.DEFAULT_PIVOT_POWER, -Constants.DEFAULT_PIVOT_POWER);
	}


  public void setPivotPower(double leftPower, double rightPower) {
		//only set one as the left motor is set to follow the right
		rightPivot.set(TalonSRXControlMode.PercentOutput, rightPower);
		leftPivot.set(TalonSRXControlMode.PercentOutput, leftPower);

		pivotPower = (rightPower+leftPower)/2;
	}


	public void setLeftPower(double power) {
		leftPivot.set(TalonSRXControlMode.PercentOutput, power);
	}

	public void setRightPower(double power) {
		rightPivot.set(TalonSRXControlMode.PercentOutput, power);
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


	/**
	 * @return true if the pivot is triggering the appropriate sensor
	 */
	public boolean onTarget() {
		if(pivotPower == Constants.DEFAULT_PIVOT_POWER) {
			return getLeftHoldSensor() && getRightHoldSensor();
		} else {
			return getLeftReachSensor() && getRightReachSensor();
		}
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
  public void stop() {
		setPivotPower(0, 0);
	}

  @Override
  public void periodic() {
    checkPivotState();
  }
}
