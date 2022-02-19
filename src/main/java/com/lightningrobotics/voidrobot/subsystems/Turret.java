package com.lightningrobotics.voidrobot.subsystems;

import java.time.chrono.IsoChronology;

import javax.management.ConstructorParameters;

import com.fasterxml.jackson.databind.deser.ValueInstantiator.Gettable;
import com.lightningrobotics.common.controller.PIDFController;
import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.common.subsystem.drivetrain.PIDFDashboardTuner;
import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.Constants;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.math.geometry.Rotation2d;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Turret extends SubsystemBase {

	private boolean isUsingNavX = false;

	private Rotation2d navXHeading;
	LightningIMU navX;
	private final CANSparkMax turretMotor;
	private final RelativeEncoder turretEncoder;
	private final PIDFController PID = new PIDFController(Constants.TURRET_kP, 0, 0);

	// A PID tuner that displays to a tab on the dashboard (values dont save, rember what you typed)
	private final PIDFDashboardTuner tuner = new PIDFDashboardTuner("Turret", PID);

	private double target;
	
	// TODO add java docs
	public Turret() {
		turretMotor = new CANSparkMax(Constants.TURN_TURRET_ID, MotorType.kBrushless); // TODO: change CAN ids for both motors
		turretMotor.setIdleMode(CANSparkMax.IdleMode.kCoast);
		turretMotor.setClosedLoopRampRate(0); // too low?
		turretEncoder = turretMotor.getEncoder();
		navX = LightningIMU.navX();
	}
	
	@Override
	public void periodic() {
		// To make the dgress in terms of -180 to 180
		double sign = -Math.signum(target);
        target = sign * (((Math.abs(target) + 180) % 360) - 180);
	
		Rotation2d constrainedAngle = Rotation2d.fromDegrees(LightningMath.constrain(target, Constants.MIN_TURRET_ANGLE, Constants.MAX_TURRET_ANGLE)); // Constraining our angle to compensate for our deadzone
		SmartDashboard.putNumber("constrained angle", constrainedAngle.getDegrees());
		SmartDashboard.putNumber("current angle", getTurretAngleNoLimit().getDegrees());
		SmartDashboard.putNumber("target angle", target);

		
		double output = PID.calculate(getTurretAngleNoLimit().getDegrees(), constrainedAngle.getDegrees()); // uses pid to set the turret power
		turretMotor.set(output);
		
		SmartDashboard.putNumber("motor output", output);
	}

	public void setTargetAngle(double target) {
		this.target = target;
	}

	public void stopTurret() {
		turretMotor.set(0);
	}

	/**
	 * Gets the turret angle using turret encoder
	 * @return An angle limited by min and max turret angle
	 */
	public Rotation2d getTurretAngle(){
		return  Rotation2d.fromDegrees(getEncoderValue() / Constants.TURN_TURRET_GEAR_RATIO * 360d);
	}

	/**
	 * Gets the turret angle as if it has no limit
	 */

	// to get the full explanation for what this does check the jira ticket (prog-195)
	public Rotation2d getTurretAngleNoLimit() {
		Rotation2d turretAngle = getTurretAngle();
		boolean isOverLimit = turretAngle.getDegrees() >= Constants.MAX_TURRET_ANGLE || turretAngle.getDegrees() <= Constants.MIN_TURRET_ANGLE;
		
		// If target angle is over the limit and we are not using the navx to calculate, then use the navx to calculate
		if (isOverLimit && !isUsingNavX) {
			isUsingNavX = true;
			navXHeading = navX.getHeading(); // what the navx was at the second it hit the limit
		} 
		// If target angle is within the limit and we are using the navx, don't
		else if (!isOverLimit && isUsingNavX){
			isUsingNavX = false;
		}

		// If we are using the navx to calculate, add the change in navx reading from the moment we hit the limit
		if(isUsingNavX){
			turretAngle.rotateBy(navX.getHeading().minus(navXHeading));
		} 
		
		return turretAngle;
	}

	public double getEncoderValue() {
		return turretEncoder.getPosition();
	}

}
