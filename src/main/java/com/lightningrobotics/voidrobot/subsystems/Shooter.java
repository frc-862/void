package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.lightningrobotics.voidrobot.Constants;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Shooter extends SubsystemBase {

	// TalonFX on flywheel
	// TalonSRX on hood

	private TalonFX flywheelMotor;//TODO: correctly set sign for motors
	private TalonSRX hoodMotor;

	public Shooter() {
		flywheelMotor = new TalonFX(Constants.FLYWHEEL_MOTOR_ID);
		hoodMotor = new TalonSRX(Constants.HOOD_MOTOR_ID);
	}


	public void runShooter(double shooterVelocity) {
		flywheelMotor.set(TalonFXControlMode.Velocity, shooterVelocity); //TODO: implement homemade velocity control
	}

	public void stopShooter() {
		flywheelMotor.set(TalonFXControlMode.PercentOutput, 0); //TODO: implement velocity control
	}

	public void hoodMove(double moveAmount) {
		hoodMotor.set(TalonSRXControlMode.PercentOutput, moveAmount);
		//TODO: add logic to actually increment
	}



	@Override
	public void periodic() {}

}
