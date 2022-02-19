package com.lightningrobotics.voidrobot.subsystems;

import com.lightningrobotics.common.controller.PIDFController;
import com.lightningrobotics.common.subsystem.drivetrain.PIDFDashboardTuner;
import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Turret extends SubsystemBase {

	// Creating turret motor, encoder, and PID controller
	private final CANSparkMax turretMotor;
	private final RelativeEncoder turretEncoder;
	private final PIDFController PID = new PIDFController(Constants.TURRET_kP, 0, 0);

	// A PID tuner that displays to a tab on the dashboard (values dont save, rember what you typed)
	private final PIDFDashboardTuner tuner = new PIDFDashboardTuner("Turret", PID);

	
	private double target; // Target turret angle
	private static double motorOutput; // Output thats is supplied to the motor -1 to 1
	
	// TODO add java docs
	public Turret() {
		turretMotor = new CANSparkMax(RobotMap.TURRET_MOTOR_ID, MotorType.kBrushless); // Sets our turret motor ID
		turretMotor.setIdleMode(CANSparkMax.IdleMode.kCoast); // Sets the turret motor to coast
		turretMotor.setClosedLoopRampRate(0);
		turretEncoder = turretMotor.getEncoder(); // Gets the cansparkmax motor's built in motor controller

	}
	
	@Override
	public void periodic() {
		// To make the dgress in terms of -180 to 180
		double sign = -Math.signum(target);
        target = sign * (((Math.abs(target) + 180) % 360) - 180);
	
		// Constraining our angle to compensate for our deadzone
		Rotation2d constrainedAngle = Rotation2d.fromDegrees(LightningMath.constrain(target, Constants.MIN_TURRET_ANGLE, Constants.MAX_TURRET_ANGLE));
		SmartDashboard.putNumber("constrained angle", constrainedAngle.getDegrees());
		SmartDashboard.putNumber("current angle", getTurretAngle().getDegrees());
		SmartDashboard.putNumber("target angle", target);

		// uses pid to set the turret power
		motorOutput = PID.calculate(getTurretAngle().getDegrees(), constrainedAngle.getDegrees());
		turretMotor.set(motorOutput);
		
		SmartDashboard.putNumber("motor output", motorOutput);
	}

	public void setTargetAngle(double target) {
		this.target = target;
	}

	public void stopTurret() {
		turretMotor.set(0);
	}

	public Rotation2d getTurretAngle() {
		return Rotation2d.fromDegrees(getEncoderValue() / Constants.TURN_TURRET_GEAR_RATIO * 360d); 
		
	}

	public double getEncoderValue() {
		return turretEncoder.getPosition();
	}

}
