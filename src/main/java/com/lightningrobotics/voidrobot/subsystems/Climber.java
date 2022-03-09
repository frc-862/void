package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.lightningrobotics.voidrobot.constants.RobotMap;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Climber extends SubsystemBase {

	// create some empty talonfx objects
	private TalonFX leftClimbWinch;
	private TalonFX rightClimbWinch;

	private TalonSRX leftClimbPivot;
	private TalonSRX rightClimbPivot;

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

		//TODO: correctly set the inverts of the climb motors
		leftClimbWinch.setInverted(true);
		rightClimbWinch.setInverted(false);

		leftClimbPivot.setInverted(false);
		rightClimbPivot.setInverted(true);
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

}
