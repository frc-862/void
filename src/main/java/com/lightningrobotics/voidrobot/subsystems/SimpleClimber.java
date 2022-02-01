package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.lightningrobotics.voidrobot.Constants;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SimpleClimber extends SubsystemBase {

	// Two TalonFX to winch down climb arms

	private TalonFX winch1Motor;
	private TalonFX winch2Motor;

  	public SimpleClimber() {
		  winch1Motor = new TalonFX(Constants.WINCH1_MOTOR_ID);
		  winch2Motor = new TalonFX(Constants.WINCH2_MOTOR_ID);

		  winch2Motor.setInverted(true); //TODO: correctly set sign for motors
	  }

	@Override
	public void periodic() {}

	public void climb(double motorPower) {
		winch1Motor.set(TalonFXControlMode.PercentOutput, motorPower);
		winch2Motor.set(TalonFXControlMode.PercentOutput, motorPower); 
	}

	public void climbStop() {
		winch1Motor.set(TalonFXControlMode.PercentOutput, 0);
		winch2Motor.set(TalonFXControlMode.PercentOutput, 0); //TODO: correctly set sign for motors
	}

}
