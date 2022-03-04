package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.lightningrobotics.common.controller.PIDFController;
import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.common.subsystem.drivetrain.PIDFDashboardTuner;
import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import com.lightningrobotics.voidrobot.constants.Constants;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Turret extends SubsystemBase {
	
	private boolean isUsingNavX = false;

	private Rotation2d navXHeading;
	// LightningIMU navX;

	// Creating turret motor, encoder, and PID controller
	private final TalonSRX turretMotor;

	//variables needed to run the tests
	private double realX = 0d;
	private double realY = 0d;

	//private final DigitalInput centerCensor = new DigitalInput(RobotMap.CENTER_SENSOR_ID);

	private final PIDFController PID = new PIDFController(Constants.TURRET_kP, Constants.TURRET_kI, 0);

	// A PID tuner that displays to a tab on the dashboard (values dont save, rember what you typed)
	private final PIDFDashboardTuner tuner = new PIDFDashboardTuner("Turret", PID);

	private boolean isArmed = false;
	private double target;
	private static double motorOutput;

	private ShuffleboardTab turretTab = Shuffleboard.getTab("Turret");
	private NetworkTableEntry centerSensorEntry;
	private NetworkTableEntry leftLimitSwitchEntry;
	private NetworkTableEntry rightLimitSwitchEntry;
	
	/**
	 * The turret subsystem has functions for aiming the turret based on three modes - vision,
	 *  no vision, and manual control (manual should only be used in emergencies or testing)
	 */ 
	public Turret() {
		// turretMotor = new CANSparkMax(RobotMap.TURRET_MOTOR_ID, MotorType.kBrushless);
		// turretMotor.setIdleMode(CANSparkMax.IdleMode.kCoast);
		// turretMotor.setClosedLoopRampRate(0); // too low?
		// turretEncoder = turretMotor.getEncoder();

		// navX = LightningIMU.navX();

		// Motor config
		turretMotor = new TalonSRX(RobotMap.TURRET_MOTOR_ID);
		turretMotor.setNeutralMode(NeutralMode.Brake);
		turretMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);

		// Dashboard info
		centerSensorEntry = turretTab.add("Hit Center Censor", "false").getEntry();
		leftLimitSwitchEntry = turretTab.add("Hit Left Limit Switch", false).getEntry();
		rightLimitSwitchEntry = turretTab.add("Hit Right Limit Switch", false).getEntry();

		// Reset values
	    resetEncoder();
		target = 0;
	}
	
	@Override
	public void periodic() {
		/*
		// Check if ready to shoot
		isArmed = Math.abs(target - getTurretAngle().getDegrees()) < Constants.TURRET_ANGLE_TOLERANCE; 
		SmartDashboard.putBoolean("Turret Armed", isArmed);

		//target = setTargetAngleEntry.getDouble(0); //uncomment this to set it to the network table values
	
		// Constraining our angle to -180 to 180 and then to our turret limit
		double sign = Math.signum(target);
        target = sign * (((Math.abs(target) + 180) % 360) - 180);
		Rotation2d constrainedAngle = Rotation2d.fromDegrees(LightningMath.constrain(target, Constants.MIN_TURRET_ANGLE, Constants.MAX_TURRET_ANGLE));
		double currentAngle = getTurretAngle().getDegrees();

		//centerSensorEntry.setBoolean(centorCensor.get());
		//if(centorSensor.get()) {
		//	resetEncoder();
		//}

		// Get and constrain motor output
		motorOutput = PID.calculate(getTurretAngle().getDegrees(), constrainedAngle.getDegrees());
		double maxMotorOutput = Math.abs(Constants.MAX_TURRET_ANGLE - currentAngle) < 10 ?
		    Constants.TURRET_REDUCED_MAX_MOTOR_OUTPUT : Constants.TURRET_NORMAL_MAX_MOTOR_OUTPUT;
		motorOutput = LightningMath.constrain(motorOutput, -maxMotorOutput, motorOutput);

		
		SmartDashboard.putNumber("navx reading", navX.getHeading().getDegrees());
		SmartDashboard.putNumber("motor output", motorOutput);
		SmartDashboard.putNumber("constrained angle", constrainedAngle.getDegrees());
		SmartDashboard.putNumber("current angle", currentAngle);
		SmartDashboard.putNumber("current angle no limit", getTurretAngleNoLimit().getDegrees());
		leftLimitSwitchEntry.setBoolean(turretMotor.isFwdLimitSwitchClosed() == 1);
		rightLimitSwitchEntry.setBoolean(turretMotor.isRevLimitSwitchClosed() == 1);
		//SmartDashboard.putData("Gyro", navX); 
		
		turretMotor.set(TalonSRXControlMode.PercentOutput, motorOutput);*/
	}
		
	/**
	 * 
	 * @return If the turret is turned to the correct degree and ready to shoot
	 */
	public boolean getArmed() {
		return isArmed;
	}

	public void stopTurret() {
		turretMotor.set(TalonSRXControlMode.PercentOutput, 0);
	}

	/**
	 * Gets the turret angle using turret encoder
	 * @return An angle limited by min and max turret angle
	 */
	public Rotation2d getTurretAngle(){
		return  Rotation2d.fromDegrees(getEncoderRotation() / Constants.TURN_TURRET_GEAR_RATIO * 360d);
	}

	/**
	 * Gets the turret angle as if it has no limit
	 */
	public Rotation2d getTurretAngleNoLimit() {
		Rotation2d turretAngle = getTurretAngle();
		final double tolerance = 5d;
		boolean isOverLimit = turretAngle.getDegrees() >= (Constants.MAX_TURRET_ANGLE - tolerance) || turretAngle.getDegrees() <= (Constants.MIN_TURRET_ANGLE + tolerance);


		// If target angle is over the limit and we are not using the navx to calculate, then use the navx to calculate
		if (isOverLimit && !isUsingNavX) {
			isUsingNavX = true;
			// navXHeading = navX.getHeading(); // what the navx was at the second it hit the limit
		} 
		// If target angle is within the limit and we are using the navx, don't
		else if (!isOverLimit && isUsingNavX){
			isUsingNavX = false;
		}

		// If we are using the navx to calculate, add the change in navx reading from the moment we hit the limit
		if(isUsingNavX){
			// turretAngle = Rotation2d.fromDegrees(turretAngle.getDegrees() + (navX.getHeading().getDegrees() - navXHeading.getDegrees()));
		} 
		
		return turretAngle;
	}

	/**
	 * gets the encoder in rotations
	 * @return the value of encoder in rotations
	 */
	public double getEncoderRotation() {
		return turretMotor.getSelectedSensorPosition() / 4096;
}


	/**
	 * Sets the offset angle and updates to see if we are within the angle threshold to shoot
	 * @param offsetAngle relative angle to turn
	 */
	public void setVisionOffset(double offsetAngle) {
		//this.target = getTurretAngleNoLimit().getDegrees() + offsetAngle;// this is getting us the angle that we need to go to using the current angle and the needed rotation 
	}

	/**
	 * Sets an offset based on tracking with no vision
	 * @param relativeX the x of the odometer reading (relative to the robot)
	 * @param relativeY the y of the odometer reading (relative to the robot)
	 * @param realTargetHeading the angle of where the target is relative to the robot at the loss of data
	 * @param lastVisionDistance the distance that was last recorded before vision stopped giving data
	 * @param changeInRotation the change in rotation from the second the robot lost vision
	 */
	public void setOffsetNoVision(double relativeX, double relativeY, double realTargetHeading, double lastVisionDistance, double changeInRotation){
		
		realX = rotateX(relativeX, relativeY, realTargetHeading);
		realY = rotateY(relativeX, relativeY, realTargetHeading);

		this.target = realTargetHeading + (Math.toDegrees(Math.atan2(realX,(lastVisionDistance-realY)))-(changeInRotation));
	}

	/**
	 * Directly set the angle to turn to
	 * @param targetAngle the angle of the turret (degrees)
	 */
	public void setTarget(double targetAngle) {
		this.target = targetAngle;
		SmartDashboard.putNumber("reading from controller", targetAngle);
	}

	/**
	 * Rotates a point in the x y plane by an angle in degrees
	 * @return x value of rotated point
	 */
	public double rotateX (double xValue, double yValue, double angleInDegrees){
		return (xValue * Math.cos(Math.toRadians(angleInDegrees))) - (yValue * Math.sin(Math.toRadians(angleInDegrees)));
	}	

	/**
	 * Rotates a point in the x y plane by an angle in degrees
	 * @return y value of rotated point
	 */
	public double rotateY (double xValue, double yValue, double angleInDegrees){
		return (xValue * Math.sin(Math.toRadians(angleInDegrees))) + (yValue * Math.cos(Math.toRadians(angleInDegrees)));
	}

	public void resetEncoder() {
		turretMotor.setSelectedSensorPosition(0);
	}

}