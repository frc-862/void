package com.lightningrobotics.voidrobot.subsystems;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import com.lightningrobotics.common.logging.DataLogger;
import com.lightningrobotics.common.util.filter.MovingAverageFilter;
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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class HubTargeting extends SubsystemBase {

	// Network Table for Limelight & Vision Data
	private final NetworkTable limelightTab = NetworkTableInstance.getDefault().getTable("limelight");
	private final ShuffleboardTab targetingTab = Shuffleboard.getTab("Targeting Tab");

	// Entries for Angle & Distance
	private final NetworkTableEntry visionTargetAreaEntry = limelightTab.getEntry("ta");
	private final NetworkTableEntry visionTargetXOffsetEntry = limelightTab.getEntry("tx");
	private final NetworkTableEntry visionTargetYOffsetEntry = limelightTab.getEntry("ty");
	private final NetworkTableEntry visionTargetDetectedEntry = limelightTab.getEntry("tv");
	private final NetworkTableEntry visionLEDEntry = limelightTab.getEntry("ledMode");
	private final NetworkTableEntry visionSnapshotEntry = limelightTab.getEntry("snapshot");
	private final NetworkTableEntry visionDistanceEntry = targetingTab.add("Vision Distance", 0).getEntry();
	private final NetworkTableEntry visionAngleEntry = targetingTab.add("Vision Angle", 0).getEntry();
	private final NetworkTableEntry biasAngleEntry = targetingTab.add("Bias Angle", 0).getEntry();
	private final NetworkTableEntry biasDistanceEntry = targetingTab.add("Bias Distnace", 0).getEntry();
	private final NetworkTableEntry hasVisionEntry = targetingTab.add("Has Vision", false).getEntry();
	private final NetworkTableEntry onTargetEntry = targetingTab.add("On Target", false).getEntry();
	private final NetworkTableEntry velocityEntry = targetingTab.add("Change In Velocity", 0).getEntry();
	private final NetworkTableEntry motionBiasAngleEntry = targetingTab.add("Motion Bias Angle", 0).getEntry();
	private final NetworkTableEntry motionBiasDistanceEntry = targetingTab.add("Motion Bias Distnace", 0).getEntry();
	private final NetworkTableEntry deltaDistanceEntry = targetingTab.add("Delta Distnace", 0).getEntry();

	// Position Tracker
	private Pose2d prevPose = new Pose2d();

	// Moving Average Filter
	private MovingAverageFilter maf = new MovingAverageFilter(Constants.DISTANCE_MOVING_AVG_ELEMENTS);

	// Vision Input Variables
	private double hubDistance = -1d;
    private double hubAngleOffset = 0d;

	// Subsystem Input Variables
	private Supplier<Pose2d> currentPoseSupplier;
	private Supplier<Rotation2d> currentTurretAngleSupplier;
	private DoubleSupplier currentHoodAngleSupplier;
	private DoubleSupplier currentFlywheelRPMSupplier;

	// Misc. Vision Targeting Variables
	private double lastVisionSnapshot = 0d;
	private double lastKnownDistance = 7d;
	private double lastKnownHeading = 0d;
	private double initX = 0d;
    private double initY = 0d;
    private double initRot = 0d;

	// Bias Vars
	private double distanceBias = 0d;
	private double angleBias = 0d;
	
	// Motion Bias
	private double motionBiasDistance = 0d;;
	private double motionBiasAngle = 0d;
	private double deltaDistance = 0d;

	// Main Output Variables
	private double targetTurretAngle;
	private double targetFlywheelRPM;
	private double targetHoodAngle;

	// Command State
	private int state = 0;

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

	public boolean onTarget() {

		var currTurret = currentTurretAngleSupplier.get().getDegrees();
		var currHood = currentHoodAngleSupplier.getAsDouble();
		var currRPM = currentFlywheelRPMSupplier.getAsDouble();

		return 
			(Math.abs(targetFlywheelRPM - currRPM) < Constants.SHOOTER_TOLERANCE) &&
			(Math.abs(targetTurretAngle - currTurret) < Constants.TURRET_TOLERANCE) &&
			(Math.abs(targetHoodAngle - currHood) < Constants.HOOD_TOLERANCE);

	}

	public boolean onTarget(double shooterRPM, double turretAngle, double hoodAngle) {

		var currTurret = currentTurretAngleSupplier.get().getDegrees();
		var currHood = currentHoodAngleSupplier.getAsDouble();
		var currRPM = currentFlywheelRPMSupplier.getAsDouble();

		return 
			(Math.abs(shooterRPM - currRPM) < Constants.SHOOTER_TOLERANCE) &&
			(Math.abs(turretAngle - currTurret) < Constants.TURRET_TOLERANCE) &&
			(Math.abs(hoodAngle - currHood) < Constants.HOOD_TOLERANCE);

	}

	public void setState(int state) {
		this.state = state;
	}

	public int getState() {
		return state;
	}

	// Set Up Hub Targeting
  	public HubTargeting(Supplier<Pose2d> currentPoseSupplier, Supplier<Rotation2d> currentTurretAngleSupplier, DoubleSupplier currentHoodAngleSupplier, DoubleSupplier currentFlywheelRPMSupplier) {

		// Setup Value Suppliers
		this.currentPoseSupplier = currentPoseSupplier;
		this.currentTurretAngleSupplier = currentTurretAngleSupplier;
		this.currentHoodAngleSupplier = currentHoodAngleSupplier;
		this.currentFlywheelRPMSupplier = currentFlywheelRPMSupplier;

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
			resetForGyro();
			targetTurretAngle = calcTurretAngle();
		}

		filterDistance();

		// Account for robot motion
		// filterRobotMotion();

		targetFlywheelRPM = calcFlywheelRPM();
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

		// Log Biases
		DataLogger.addDataElement("distanceBias", () -> distanceBias);
		DataLogger.addDataElement("angleBias", () -> angleBias);

		// Log On Target
		DataLogger.addDataElement("onTarget", () -> onTarget() ? 1 : 0);
		
		// Log Motion Biases
		DataLogger.addDataElement("motionBiasAngle", () -> motionBiasAngle);
		DataLogger.addDataElement("motionBiasDistance", () -> motionBiasDistance);
		DataLogger.addDataElement("deltaDistance", () -> deltaDistance);

		DataLogger.addDataElement("state", this::getState);

	}

	private void updateDashboard() {

		// Vision Dashboard Data
		visionDistanceEntry.setDouble(hubDistance);
		visionAngleEntry.setDouble(hubAngleOffset);
		biasAngleEntry.setDouble(angleBias);
		biasDistanceEntry.setDouble(distanceBias);
		hasVisionEntry.setBoolean(hasVision());
		onTargetEntry.setBoolean(onTarget());
		motionBiasAngleEntry.setDouble(motionBiasAngle);
		motionBiasDistanceEntry.setDouble(motionBiasDistance);
		deltaDistanceEntry.setDouble(deltaDistance);
		
	}

	private void snapshot() {
		// Take Snapshot of Vision Target When Enabled
		if (DriverStation.isEnabled()) {
			if (Timer.getFPGATimestamp() - lastVisionSnapshot > Constants.SNAPSHOT_DELAY) {
				lastVisionSnapshot	= Timer.getFPGATimestamp();
				visionSnapshotEntry.setNumber(1);
				System.out.println("Logging Snapshot Taken");
				visionSnapshotEntry.setNumber(0);

			}
		}
	}

	// Math Util Functions

	private void filterRobotMotion() {
		
		try {

			// Calculate Change in Velocity
			var pose = currentPoseSupplier.get();
			var deltaX = pose.getX() - prevPose.getX();
			var deltaY = pose.getY() - prevPose.getY(); 
			var vel = -(Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2))); // negative b/c shooter on the back of the robot
			prevPose = pose;
			velocityEntry.setDouble(vel);

			var dist = hubDistance;
			var theta = targetTurretAngle;

			motionBiasDistance = Math.sqrt((Math.pow(dist, 2)) + (Math.pow(vel, 2)) - (2 * dist * vel * Math.cos(theta)));
			
			if(motionBiasDistance <= 0) {
				System.err.println("Bias Distance <= 0 - Will Fail");
				return;
			}
			
			motionBiasAngle = Math.acos( ( (Math.pow(dist, 2)) + (Math.pow(motionBiasDistance, 2)) - (Math.pow(vel, 2)) ) / (2 * dist * motionBiasDistance) );
			deltaDistance = hubDistance - motionBiasDistance;

			if(motionBiasDistance > 0) {
				hubDistance = motionBiasDistance;
				targetTurretAngle += motionBiasAngle;
			} else {
				System.err.println("robot motion out of bounds");
			}

		} catch (Exception e) {
			System.err.println("failed to process robot motion");
			e.printStackTrace();
		}
		

	}

	private double calcHubDistance() {
		var theta = visionTargetYOffsetEntry.getDouble(-1d); // get limelight angle degrees
		var rawDistanceInches = // calc raw distance from angle
			(Constants.HUB_HEIGHT-Constants.MOUNT_HEIGHT) / 
			Math.tan(Math.toRadians(Constants.MOUNT_ANGLE + theta)) + 
			Constants.HUB_CENTER_OFFSET; 
		var processedDistance = Units.inchesToMeters(rawDistanceInches) + distanceBias; // add biases/on-the-fly offsets, etc.
		lastKnownDistance = processedDistance;
		return processedDistance;
	}

	private double calcHubAngleOffset() {
		var offsetFromCenter = visionTargetXOffsetEntry.getDouble(hubAngleOffset); // get limelight angle degrees
		var processedAngleOffset = offsetFromCenter + angleBias; // add biases/on-the-fly offsets, etc.
		return -processedAngleOffset;
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
		// var lastKnownHeading = targetTurretAngle; // currentTurretAngleSupplier.get().getDegrees() + lastKnownOffset;
		var realX = rotateX(relativeX, relativeY, lastKnownHeading);
		var realY = rotateY(relativeX, relativeY, lastKnownHeading);
	
		// extract back to a distance and angle
		var processedDistance = Math.sqrt(Math.pow((lastKnownDistance - relativeX), 2) + Math.pow(relativeX, 2));
		var turretTarget = (lastKnownHeading) + (Math.toDegrees(Math.atan2(realX,(lastKnownDistance-realY)))-(changeInRotation));
			
		hubDistance = processedDistance;
		SmartDashboard.putNumber("Turret Angle Gyro", turretTarget);
		targetTurretAngle = turretTarget;

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

	private void filterDistance() {
		if (hubDistance > 0) {
			hubDistance = maf.filter(hubDistance);
		} else {
			hubDistance = maf.get();
		}
	}

	private void resetForGyro() {
		var rot = currentPoseSupplier.get();
		initRot = rot.getRotation().getDegrees();
        initX = rot.getX();
        initY = rot.getY();
		lastKnownHeading = targetTurretAngle;

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

	public boolean hasVision() {
		return hubDistance > 0 && visionTargetDetectedEntry.getDouble(0) == 1;
	}

	// Bias Util

	public void adjustBiasDistance(double delta) {
		distanceBias += delta;
	}
	
	public void adjustBiasAngle(double delta) {
		angleBias -= delta; // needs to subtract to add on to the delta, its werid
	}

	public void zeroBias() {
		distanceBias = 0;
		angleBias = 0;
	}

}
