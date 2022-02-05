package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.lightningrobotics.voidrobot.Constants;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Shooter extends SubsystemBase {

	// TalonFX on flywheel
	// TalonSRX on hood

	private VictorSPX flywheelMotor;//TODO: correctly set sign for motors
	private TalonSRX hoodMotor;

	private Encoder shooterEncoder;

	public Shooter() {
		flywheelMotor = new VictorSPX(Constants.FLYWHEEL_MOTOR_ID);
		hoodMotor = new TalonSRX(Constants.HOOD_MOTOR_ID);
		shooterEncoder = new Encoder(0, 1);

		flywheelMotor.setInverted(true);

		shooterEncoder.setDistancePerPulse(1d/2048d);
	}


	public void runShooter(double shooterVelocity) {
		flywheelMotor.set(VictorSPXControlMode.PercentOutput, shooterVelocity); 
	}

	public void stopShooter() {
		flywheelMotor.set(VictorSPXControlMode.PercentOutput, 0);; 
	}

	public void hoodMove(double moveAmount) {
		hoodMotor.set(TalonSRXControlMode.PercentOutput, moveAmount);
		//TODO: add logic to actually increment
	}

	public double getEncoderRPMs() {
		return shooterEncoder.getRate() * 60;
	}

	public double getEncoderDist() {
		return shooterEncoder.getDistancePerPulse();
	}

	public double currentEncoderTicks() {
		return shooterEncoder.getRaw(); 
	}

	@Override
	public void periodic() {}

}
