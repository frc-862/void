package com.lightningrobotics.voidrobot.subsystems;

import javax.management.ConstructorParameters;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.lightningrobotics.common.controller.PIDFController;
import com.lightningrobotics.common.subsystem.drivetrain.PIDFDashboardTuner;
import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import com.lightningrobotics.voidrobot.Robot;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Turret extends SubsystemBase {

	// Creating turret motor, encoder, and PID controller
	// private final CANSparkMax turretMotor;
	// private final RelativeEncoder turretEncoder;

	private final TalonSRX turretMotor;

	private final PIDFController PID = new PIDFController(Constants.TURRET_kP, 0, 0);

	// A PID tuner that displays to a tab on the dashboard (values dont save, rember what you typed)
	private final PIDFDashboardTuner tuner = new PIDFDashboardTuner("Turret", PID);

	private double target;

	private boolean isOnTarget = false;
	
	// TODO add java docs
	public Turret() {
		// turretMotor = new CANSparkMax(RobotMap.TURRET_MOTOR_ID, MotorType.kBrushless); // TODO: change CAN ids for both motors
		// turretMotor.setIdleMode(CANSparkMax.IdleMode.kCoast);
		// turretMotor.setClosedLoopRampRate(0); // too low?
		// turretEncoder = turretMotor.getEncoder();

		turretMotor = new TalonSRX(RobotMap.TURRET_MOTOR_ID);

		turretMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);

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

		double output = PID.calculate(getTurretAngle().getDegrees(), constrainedAngle.getDegrees()); // uses pid to set the turret power

		turretMotor.set(TalonSRXControlMode.PercentOutput, output);
		
		SmartDashboard.putNumber("motor output", output);
	}

	public void setTargetAngle(double target) {
		this.target = target;
	}

	public void stopTurret() {
		turretMotor.set(TalonSRXControlMode.PercentOutput, 0);
	}

	public Rotation2d getTurretAngle() {
		return Rotation2d.fromDegrees(getEncoderValue() / Constants.TURN_TURRET_GEAR_RATIO * 360d); 
		
	}

	/**
	 * gets the encoder in rotations
	 * @return the encoder value in rotations
	 */
	public double getEncoderValue() {
		// return turretEncoder.getPosition();

		return turretMotor.getSelectedSensorPosition() * 360 / 4096;
	}

}
