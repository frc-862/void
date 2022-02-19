package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import com.lightningrobotics.voidrobot.constants.Constants;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SimpleClimber extends SubsystemBase {

	// TODO: implement climber logic

	// Two TalonFX to winch down climb arms
	private TalonFX winch1Motor;
	private TalonFX winch2Motor;

  	public SimpleClimber() {
		  // Sets the IDs of our winch motors
		  winch1Motor = new TalonFX(RobotMap.WINCH1_MOTOR_ID);
		  winch2Motor = new TalonFX(RobotMap.WINCH2_MOTOR_ID);

		  winch1Motor.setInverted(false); //TODO: correctly set the inverts of the motors
		  winch2Motor.setInverted(false);
	  }

	@Override
	public void periodic() {}

	public void climb(double motorPower) {
		winch1Motor.set(TalonFXControlMode.PercentOutput, motorPower);
		winch2Motor.set(TalonFXControlMode.PercentOutput, motorPower); 
	}

	public void climbStop() {
		winch1Motor.set(TalonFXControlMode.PercentOutput, 0); //TODO: correctly set sign for motors
		winch2Motor.set(TalonFXControlMode.PercentOutput, 0); 
	}

}
