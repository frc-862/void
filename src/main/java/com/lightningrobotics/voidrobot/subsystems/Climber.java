package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.lightningrobotics.voidrobot.constants.RobotMap;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Climber extends SubsystemBase {

	// create some empty talonfx objects
	private TalonFX leftClimbWinch;
	private TalonFX rightClimbWinch;

  	public Climber() {
		// Sets the IDs of our winch motors
		leftClimbWinch = new TalonFX(RobotMap.LEFT_CLIMB);
		rightClimbWinch = new TalonFX(RobotMap.RIGHT_CLIMB);

		//climb motors need to be in brake mode to hold climb up
		leftClimbWinch.setNeutralMode(NeutralMode.Brake);
		rightClimbWinch.setNeutralMode(NeutralMode.Brake);

		//TODO: correctly set the inverts of the motors
		leftClimbWinch.setInverted(false);
		rightClimbWinch.setInverted(false);
	}

	public void setPower(double leftPower, double rightPower) {
		leftClimbWinch.set(TalonFXControlMode.PercentOutput, leftPower);
		rightClimbWinch.set(TalonFXControlMode.PercentOutput, rightPower);
	}

	public void stop() {
		setPower(0, 0);
	}

}
