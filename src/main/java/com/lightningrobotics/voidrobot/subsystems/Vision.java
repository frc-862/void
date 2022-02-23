package com.lightningrobotics.voidrobot.subsystems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Vision extends SubsystemBase {

	// Network Table for Vision
	private final NetworkTable visionTable = NetworkTableInstance.getDefault().getTable("Vision");

	// Entries for Angle & Distance	
	private final NetworkTableEntry targetAngleEntry = visionTable.getEntry("Angle");
	private final NetworkTableEntry targetDistanceEntry = visionTable.getEntry("Distance");
	// private final NetworkTableEntry targetAngleEntry = visionTable.getEntry("Angle");
	// private final NetworkTableEntry targetDistanceEntry = visionTable.getEntry("Distance");

	private final ShuffleboardTab vision = Shuffleboard.getTab("vision");
	private final NetworkTableEntry setTurretAngle;
	private final NetworkTableEntry setTargetHeight;
	private final double deltaAngle = 0.001;

	// Placeholder Vars for Angle & Distance
	private static double targetAngle = 0d;
	private static double targetDistance = 0d;
	private static double targetHeight = 0d;
    private static double offsetAngle = 0d;

	public Vision() {
		setTurretAngle = vision
			.add("set turrent angle", 0)
			.getEntry();
		setTargetHeight = vision
			.add("set target height", 0)
			.getEntry();
	}

	@Override
	public void periodic() {
		
		// Update Target Angle
		offsetAngle = targetAngleEntry.getDouble(targetAngle);
		targetAngle = setTurretAngle.getDouble(0);

		targetHeight = setTargetHeight.getDouble(0);

		// targetAngleEntry.getDouble(targetAngle);

		// Update Target Distance
		targetDistance = targetDistanceEntry.getDouble(targetDistance);

	}

	public double getOffsetAngle() {
		// TODO: implement math for error to get target angle
		return offsetAngle; 
	}

	public double getTargetDistance() {
		return targetDistance;
	}

	public double getTargetHeight() {
		return targetHeight;
	}

	public boolean hasVision(){
		return true;
	}
}
