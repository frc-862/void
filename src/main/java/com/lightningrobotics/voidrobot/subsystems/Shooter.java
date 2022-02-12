package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.lightningrobotics.common.controller.PIDFController;
import com.lightningrobotics.common.subsystem.drivetrain.PIDFDashboardTuner;
import com.lightningrobotics.voidrobot.Constants;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Shooter extends SubsystemBase {
	private VictorSPX flywheelMotor;//TODO: correctly set invert for motors
	private TalonSRX hoodMotor;
	private Encoder shooterEncoder;

	private PIDFController pid = new PIDFController(Constants.SHOOTER_KP, Constants.SHOOTER_KI, Constants.SHOOTER_KD);
	private PIDFDashboardTuner pidTuner = new PIDFDashboardTuner("shooter", pid);

	private double powerSetPoint;

	public Shooter() {
		flywheelMotor = new VictorSPX(Constants.FLYWHEEL_MOTOR_ID);
		hoodMotor = new TalonSRX(Constants.HOOD_MOTOR_ID);
		shooterEncoder = new Encoder(9, 8);

		flywheelMotor.setInverted(true);

		shooterEncoder.setDistancePerPulse(1d/2048d); //encoder ticks per rev (or, the other way around)
	}

	
	public void setPower(double power) {
		//TODO: use falcon built-in functions
		flywheelMotor.set(VictorSPXControlMode.PercentOutput, power); 
	}

	public void setVelocity(double shooterVelocity) {
		//TODO: use falcon built-in functions
		flywheelMotor.set(VictorSPXControlMode.Velocity, shooterVelocity); 
	}

	public void stop() {
		flywheelMotor.set(VictorSPXControlMode.PercentOutput, 0);; 
	}

	public void hoodMove(double moveAmount) {
		hoodMotor.set(TalonSRXControlMode.PercentOutput, moveAmount);
		//TODO: add logic to actually increment
	}

	public double getEncoderRPMs() {
		return shooterEncoder.getRate() * 60; //converts from revs per second to revs per minute
	}

	public double getEncoderDist() {
		return shooterEncoder.getDistancePerPulse();
	}

	public double currentEncoderTicks() {
		return shooterEncoder.getRaw(); 
	}

	public void setRPM(double targetRPMs) {
		powerSetPoint = pid.calculate(getEncoderRPMs(), targetRPMs);
		setPower(powerSetPoint);
	}

	public double getPowerSetpoint() {
		return powerSetPoint;
	}

	public void setPIDGains(double kP, double kD) {
		pid.setP(kP);
		pid.setD(kD);
	}

	@Override
	public void periodic() {}

}
