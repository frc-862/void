package com.lightningrobotics.voidrobot.subsystems;

import javax.swing.plaf.basic.BasicTreeUI.TreeCancelEditingAction;

import com.lightningrobotics.common.controller.PIDFController;
import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.common.subsystem.drivetrain.PIDFDashboardTuner;
import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Turret extends SubsystemBase {
	// Creating turret motor, encoder, and PID controller
	private boolean isUsingNavX = false;
	private boolean hasVision = true;

	private Rotation2d navXHeading;
	LightningIMU navX;
	// Creating turret motor, encoder, and PID controller
	// private final CANSparkMax turretMotor;
	// private final RelativeEncoder turretEncoder;

	private final CANSparkMax turretMotor;
	//variables I need to run the tests
	private boolean testOneHasInit = false;
	private boolean testTwoHasInit = false;
	private boolean testThreeHasInit = false;
	private boolean testFourHasInit = false;
	private double navXOrigin = 0d;
	private double navXCurrent = 0d;
	private double realX = 0d;
	private double realY = 0d;
	private double knownDistanceFromTarget = 6.5d;
	private double realHeadingTowardsTarget = 0;

	private final PIDFController PID = new PIDFController(Constants.TURRET_kP, 0, 0);

	// A PID tuner that displays to a tab on the dashboard (values dont save, rember what you typed)
	private final PIDFDashboardTuner tuner = new PIDFDashboardTuner("Turret", PID);

	private boolean armed = false;
	private double target;
	private static double motorOutput;

	private ShuffleboardTab turretTab = Shuffleboard.getTab("Turret");
	
	// TODO add java docs
	public Turret() {
		// turretMotor = new CANSparkMax(RobotMap.TURRET_MOTOR_ID, MotorType.kBrushless); // TODO: change CAN ids for both motors
		// turretMotor.setIdleMode(CANSparkMax.IdleMode.kCoast);
		// turretMotor.setClosedLoopRampRate(0); // too low?
		// turretEncoder = turretMotor.getEncoder();

		turretMotor = new CANSparkMax(RobotMap.TURRET_MOTOR_ID, MotorType.kBrushless);

		//turretMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);

		navX = LightningIMU.navX();
	}
	
	@Override
	public void periodic() {

		// To make the dgress in terms of -180 to 180
		double sign = Math.signum(target);
        target = sign * (((Math.abs(target) + 180) % 360) - 180);
	
		// Constraining our angle to compensate for our deadzone
		Rotation2d constrainedAngle = Rotation2d.fromDegrees(LightningMath.constrain(target, Constants.MIN_TURRET_ANGLE, Constants.MAX_TURRET_ANGLE));
		SmartDashboard.putNumber("constrained angle", constrainedAngle.getDegrees());
		SmartDashboard.putNumber("current angle", getTurretAngle().getDegrees());
		SmartDashboard.putNumber("target angle", target);

		// uses pid to set the turret power
		motorOutput = PID.calculate(getTurretAngle().getDegrees(), constrainedAngle.getDegrees());
		turretMotor.set(motorOutput);
		SmartDashboard.putNumber("turret angle with navX added", getTurretAngleNoLimit().getDegrees());
		SmartDashboard.putNumber("navx reading", navX.getHeading().getDegrees());
		SmartDashboard.putNumber("motor output", motorOutput);

	}

	public void setTarget(double target) {
		this.target = target;
	}
		
	/**
	 * 
	 * @return If the turret is turned to the correct degree and ready to shoot
	 */
	public boolean getArmed() {
		return armed;
	}

	public void stopTurret() {
		turretMotor.set(0);
	}

	/**
	 * Gets the turret angle using turret encoder
	 * @return An angle limited by min and max turret angle
	 */
	public Rotation2d getTurretAngle(){
		//return  Rotation2d.fromDegrees(getEncoderValue() / Constants.TURN_TURRET_GEAR_RATIO * 360d);
		return  Rotation2d.fromDegrees(getEncoderValue() / Constants.TURN_TURRET_GEAR_RATIO * 360d);
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
			navXHeading = navX.getHeading(); // what the navx was at the second it hit the limit
		} 
		// If target angle is within the limit and we are using the navx, don't
		else if (!isOverLimit && isUsingNavX){
			isUsingNavX = false;
		}

		// If we are using the navx to calculate, add the change in navx reading from the moment we hit the limit
		if(isUsingNavX){
			turretAngle = Rotation2d.fromDegrees(turretAngle.getDegrees() + (navX.getHeading().getDegrees() - navXHeading.getDegrees()));
		} 
		
		return turretAngle;
	}

	/**
	 * gets the encoder in rotations
	 * @return the encoder value in rotations
	 */
	public double getEncoderValue() {
		// return turretEncoder.getPosition();
		//return turretMotor.getEncoder().getPosition() * 360 / 4096;
		return turretMotor.getEncoder().getPosition();
}


	/**
	 * Sets the offset angle and updates to see if we are within the angle threshold to shoot
	 * @param offsetAngle relative angle to turn
	 */
	public void setVisionOffset(double offsetAngle) {
		this.target = getTurretAngle().getDegrees() + offsetAngle;// this is getting us the angle that we need to go to using the current angle and the needed rotation 
		this.armed = Math.abs(offsetAngle) < 5; // Checks to see if our turret is within our vision threashold
		hasVision = true;
	}

	/**
	 * Sets an offset based on tracking with no vision
	 */
	public void setOffsetNoVision(double relativeX, double relativeY, double lastOffset, double lastVisionDistance){
		if (hasVision){
			//currentX = 0;
			//currentY = 0;
			navXOrigin = navX.getHeading().getDegrees();
			realHeadingTowardsTarget = getTurretAngle().getDegrees() + lastOffset;
			knownDistanceFromTarget = lastVisionDistance;
			hasVision = false;
		}

		realX = rotateX(relativeX, 0);
		realY = rotateY(relativeY, 0);

		navXCurrent = navX.getHeading().getDegrees();

		this.target = Math.toDegrees(Math.atan2(realX,(knownDistanceFromTarget-realY)))+(navXOrigin-navXCurrent);
	}

	public double rotateX (double xValue, double angleInDegrees){return xValue;} // <--- TODO actually program this function
	public double rotateY (double yValue, double angleInDegrees){return yValue;} // <--- TODO actually program this function

}