package com.lightningrobotics.voidrobot.subsystems;

import java.util.function.Supplier;

import com.lightningrobotics.common.logging.DataLogger;
import com.lightningrobotics.voidrobot.constants.Constants;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class HubTargeting extends SubsystemBase {

	// Network Table for Limelight & Vision Data
	private final NetworkTable limelightTab = NetworkTableInstance.getDefault().getTable("limelight");
	private final ShuffleboardTab visionTab = Shuffleboard.getTab("Vision Tab");

	// Entries for Angle & Distance
	private final NetworkTableEntry visionTargetAreaEntry = limelightTab.getEntry("ta");
	private final NetworkTableEntry visionTargetXOffsetEntry = limelightTab.getEntry("tx");
	private final NetworkTableEntry visionTargetYOffsetEntry = limelightTab.getEntry("ty");
	private final NetworkTableEntry visionTargetDetectedEntry = limelightTab.getEntry("tv");
	private final NetworkTableEntry visionLEDEntry = limelightTab.getEntry("ledMode");
	private final NetworkTableEntry visionSnapshotEntry = limelightTab.getEntry("snapshot");
	private final NetworkTableEntry visionDistanceEntry = visionTab.add("Vision Distance", 0).getEntry();
	private final NetworkTableEntry visionAngleEntry = visionTab.add("Vision Angle", 0).getEntry();

	// Vision Input Variables
	private double hubDistance = -1d;
    private double hubAngleOffset = 0d;

	// Subsystem Input Variables
	private Supplier<Pose2d> currentPoseSupplier;
	private Supplier<Rotation2d> currentTurretAngleSupplier;

	// Misc. Vision Targeting Variables
	private double lastVisionSnapshot = 0d;
	private double lastKnownDistance = 0d;
	private double lastKnownOffset = 0d;
	private double initX = 0d;
    private double initY = 0d;
    private double initRot = 0d;

	// Main Output Variables
	private double targetTurretAngle;
	private double targetFlywheelRPM;
	private double targetHoodAngle;

	// Main Output External Access Functions
	
	public double getTargetTurretAngle() {
		return targetTurretAngle;
	}

	public double getTargetFlywheelRPM() {
		return targetFlywheelRPM;
	}

	public double getTargetHoodAngle() {
		return targetHoodAngle;
	}

	// Set Up Hub Targeting
  	public HubTargeting(Supplier<Pose2d> currentPoseSupplier, Supplier<Rotation2d> currentTurretAngleSupplier) {

		// Setup Value Suppliers
		this.currentPoseSupplier = currentPoseSupplier;
		this.currentTurretAngleSupplier = currentTurretAngleSupplier;

		// Setup Subsystem
		initLogging();
		CommandScheduler.getInstance().registerSubsystem(this);

	}

	// Periodic Defines Steps To Targeting
	@Override
	public void periodic() {

		// Calculate Target Distance
		hubDistance = calcHubDistance(); 
		// Calculate Target Angle
		hubAngleOffset = calcHubAngleOffset(); 

		if(!hasVision()) {
			calcHubGyro();
		} else {
			resetOdometer();
		}

		// Calculate Nominal Turret Angle
		targetTurretAngle = calcTurretAngle();
		// Calculate Nominal Flywheel RPM
		targetFlywheelRPM = calcFlywheelRPM();
		// Calculate Nominal Hood Angle
		targetHoodAngle = calcHoodAngle();

		updateDashboard();
		snapshot();
		
	}

	// Logging/Debug Functions

	private void initLogging() {

		// Vision Logging
		DataLogger.addDataElement("hubAngleOffset", () -> hubAngleOffset);
		DataLogger.addDataElement("hubDistance", () -> hubDistance);
		DataLogger.addDataElement("targetArea", () -> visionTargetAreaEntry.getDouble(-1));
		DataLogger.addDataElement("targetX", () -> visionTargetXOffsetEntry.getDouble(-1));
		DataLogger.addDataElement("targetY", () -> visionTargetYOffsetEntry.getDouble(-1));

		// Target Output Logging
		DataLogger.addDataElement("targetTurretAngle", () -> targetTurretAngle);
		DataLogger.addDataElement("targetFlywheelRPM", () -> targetFlywheelRPM);
		DataLogger.addDataElement("targetHoodAngle", () -> targetHoodAngle);

	}

	private void updateDashboard() {

		// Vision Dashboard Data
		visionDistanceEntry.setDouble(hubDistance);
		visionAngleEntry.setDouble(hubAngleOffset);

	}

	private void snapshot() {
		// Take Snapshot of Vision Target When Enabled
		if (DriverStation.isEnabled()) {
			if (Timer.getFPGATimestamp() - lastVisionSnapshot > Constants.SNAPSHOT_DELAY) {
				lastVisionSnapshot	= Timer.getFPGATimestamp();
				visionSnapshotEntry.setNumber(1);
				System.out.println("Logging Snapshot Taken");
			}
		}
	}

	// Math Util Functions

	private double calcHubDistance() {
		var theta = visionTargetYOffsetEntry.getDouble(-1d); // get limelight angle degrees
		var rawDistanceInches = // calc raw distance from angle
			(Constants.HUB_HEIGHT-Constants.MOUNT_HEIGHT) / 
			Math.tan(Math.toRadians(Constants.MOUNT_ANGLE + theta)) + 
			Constants.HUB_CENTER_OFFSET; 
		var processedDistance = Units.inchesToMeters(rawDistanceInches); // TODO add biases/on-the-fly offsets, etc.
		lastKnownDistance = processedDistance;
		return processedDistance;
	}

	private double calcHubAngleOffset() {
		var offsetFromCenter = visionTargetXOffsetEntry.getDouble(hubAngleOffset); // get limelight angle degrees
		var processedAngleOffset = offsetFromCenter; // TODO add biases/on-the-fly offsets, etc.
		lastKnownOffset = processedAngleOffset;
		return processedAngleOffset;
	}

	private void calcHubGyro() {

		var relativeX = currentPoseSupplier.get().getX() - initX;
		var relativeY = currentPoseSupplier.get().getY() - initY;

		// rotate from odometer-center to robot-center
		relativeX = rotateX(relativeX, relativeY, initRot);
		relativeY = rotateY(relativeX, relativeY, initRot);

		// update angle data 
		var changeInRotation = currentPoseSupplier.get().getRotation().getDegrees() - initRot;	
		
		// calc new pos
		var lastKnownHeading = targetTurretAngle;
		var realX = rotateX(relativeX, relativeY, lastKnownHeading);
		var realY = rotateY(relativeX, relativeY, lastKnownHeading);
	
		// extract back to a distance and angle
		var processedDistance = lastKnownDistance - realY;
		var processedOffsetAngle = ((lastKnownHeading) - (lastKnownHeading - lastKnownOffset)) + (Math.toDegrees(Math.atan2(realX,(lastKnownDistance-realY)))-(changeInRotation));
			
		hubDistance = processedDistance;
		hubAngleOffset = processedOffsetAngle;

	}

	private double calcTurretAngle() {
		return currentTurretAngleSupplier.get().getDegrees() + hubAngleOffset;
	}

	private double calcFlywheelRPM() {
		var rpm = Constants.DISTANCE_RPM_MAP.get(hubDistance) + Constants.ANGLE_POWER_MAP.get(currentTurretAngleSupplier.get().getDegrees());
		return rpm;
	}

	private double calcHoodAngle() {
		var hoodAngle = Constants.HOOD_ANGLE_MAP.get(hubDistance);
		return hoodAngle;
	}

	private double rotateX (double xValue, double yValue, double angleInDegrees){
		return (xValue * Math.cos(Math.toRadians(angleInDegrees))) - (yValue * Math.sin(Math.toRadians(angleInDegrees)));
	}	

	private double rotateY (double xValue, double yValue, double angleInDegrees){
		return (xValue * Math.sin(Math.toRadians(angleInDegrees))) + (yValue * Math.cos(Math.toRadians(angleInDegrees)));
	}

	private void resetOdometer() {

	}

	// Limelight Util Functions

	public void turnOnVisionLight(){
		visionLEDEntry.setNumber(3);
	}

	public void turnOffVisionLight(){
		visionLEDEntry.setNumber(1);
	}

	public void toggleVisionLights() {
		if(visionLightsOn()) turnOffVisionLight();
		else turnOnVisionLight();
	}

	public boolean visionLightsOn() {
		return visionLEDEntry.getDouble(0) == 3 || visionLEDEntry.getDouble(0) == 0;
	}

	private boolean hasVision() {
		return hubDistance > 0 && visionTargetDetectedEntry.getDouble(0) == 1;
	}

}
