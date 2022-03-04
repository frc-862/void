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

	// Creating turret motor, encoder, and PID controller
	private final TalonSRX turretMotor;

	private final DigitalInput centerSensor = new DigitalInput(2);
	//variables needed to run the tests
	private double realX = 0d;
	private double realY = 0d;

	// A PID tuner that displays to a tab on the dashboard (values dont save, rember what you typed)
	private final PIDFDashboardTuner tuner = new PIDFDashboardTuner("Turret", Constants.TURRET_PID);

	private boolean isArmed = false;
	private double target;
	private static double motorOutput;
	private boolean turretZeroed = false;

	private ShuffleboardTab turretTab = Shuffleboard.getTab("Turret");
	private NetworkTableEntry currentAngle;
	private NetworkTableEntry centerSensorEntry;
	private NetworkTableEntry setTargetAngleEntry;
	private NetworkTableEntry leftLimitSwitchEntry;
	private NetworkTableEntry rightLimitSwitchEntry;
	
	public Turret() {

		// Motor config
		turretMotor = new TalonSRX(RobotMap.TURRET_MOTOR_ID);
		turretMotor.setNeutralMode(NeutralMode.Brake);
		turretMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);

		// Dashboard info
		centerSensorEntry = turretTab.add("Hit Center Censor", "false").getEntry();
		leftLimitSwitchEntry = turretTab.add("Hit Left Limit Switch", false).getEntry();
		rightLimitSwitchEntry = turretTab.add("Hit Right Limit Switch", false).getEntry();
		setTargetAngleEntry = turretTab.add("Set Turret Angle", 0).getEntry();
		currentAngle = turretTab.add("current angle", 0).getEntry(); 


		// Reset values
	    resetEncoder();
		target = 0;
	}
	
	@Override
	public void periodic() {


		// //TODO: put this somewhere better, and implement it with some kind of break maybe. 
		// while(findZero() && !turretZeroed) {
		// 	if(!centerSensor.get()) {
		// 		resetEncoder();
		// 	}
		// }

		// turretZeroed = true;

		// if(!centerSensor.get()) {
		// 	resetEncoder();
		// }

		// Check if ready to shoot
		isArmed = Math.abs(target - getCurrentAngle().getDegrees()) < Constants.TURRET_ANGLE_TOLERANCE; 
		SmartDashboard.putBoolean("Turret Armed", isArmed);

		currentAngle.setDouble(getCurrentAngle().getDegrees());


	}
		
	public boolean isOverLimit(){
		final double tolerance = 5d;
		return getCurrentAngle().getDegrees() >= (Constants.MAX_TURRET_ANGLE - tolerance) 
		|| getCurrentAngle().getDegrees() <= (Constants.MIN_TURRET_ANGLE + tolerance);
	}

	public boolean getArmed() {
		return isArmed;
	}

	public void stop() {
		turretMotor.set(TalonSRXControlMode.PercentOutput, 0);
	}

	public Rotation2d getCurrentAngle(){
		return  Rotation2d.fromDegrees(getEncoderRotation() / Constants.TURN_TURRET_GEAR_RATIO * 360d);
	}

	public double getEncoderRotation() {
		return turretMotor.getSelectedSensorPosition() / 4096;
	}

	public void setVisionOffset(double offsetAngle) {
		target = getCurrentAngle().getDegrees() + offsetAngle;// this is getting us the angle that we need to go to using the current angle and the needed rotation 
	}

	public double getOffsetNoVision(double relativeX, double relativeY, double realTargetHeading, double lastVisionDistance, double changeInRotation){
		
		realX = rotateX(relativeX, relativeY, realTargetHeading);
		realY = rotateY(relativeX, relativeY, realTargetHeading);

		return realTargetHeading + (Math.toDegrees(Math.atan2(realX,(lastVisionDistance-realY)))-(changeInRotation));
	}

	public void setTarget(double targetAngle) {
		this.target = targetAngle;
		SmartDashboard.putNumber("reading from controller", targetAngle);
	}

	public double rotateX (double xValue, double yValue, double angleInDegrees){
		return (xValue * Math.cos(Math.toRadians(angleInDegrees))) - (yValue * Math.sin(Math.toRadians(angleInDegrees)));
	}	

	public double rotateY (double xValue, double yValue, double angleInDegrees){
		return (xValue * Math.sin(Math.toRadians(angleInDegrees))) + (yValue * Math.cos(Math.toRadians(angleInDegrees)));
	}

	public void resetEncoder() {
		turretMotor.setSelectedSensorPosition(0);
	}

	public boolean getLeftLimitSwitch() {
		return turretMotor.isFwdLimitSwitchClosed() == 1;
	}

	public boolean getRightLimitSwitch() {
		return turretMotor.isRevLimitSwitchClosed() == 1;
	}

	public boolean getCenterSensor() {
		return !centerSensor.get(); // TODO: check if it's inverted
	}

	public void setPower(double power) {
		turretMotor.set(TalonSRXControlMode.PercentOutput, power);
	}

}