package com.lightningrobotics.voidrobot.subsystems;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.constants.RobotMap;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Vision extends SubsystemBase {

	// Network Table for Vision
	private final NetworkTable visionTable = NetworkTableInstance.getDefault().getTable("Vision");

	// Entries for Angle & Distance	
	private final NetworkTableEntry offsetAngleEntry = visionTable.getEntry("Target Angle");
	private final NetworkTableEntry targetDistanceEntry = visionTable.getEntry("Target Distance");

	// Placeholder Vars for Angle & Distance
	private static double targetDistance = 0d;
	private static double targetHeight = 0d;
    private static double offsetAngle = 0d;

	//PDH
	PowerDistribution pdh = new PowerDistribution(RobotMap.PDH_ID, ModuleType.kRev);

	public Vision() {}

	@Override
	public void periodic() {
		
		// Update Target Angle
		offsetAngle = offsetAngleEntry.getDouble(0);

		// Update Target Distance
		targetDistance = targetDistanceEntry.getDouble(targetDistance);

	}

	/**
	 * Check if turret angle is within tolerance
	 * @return If turret is ready for shooting
	 */
	public boolean isOnTarget() {
		return Math.abs(getOffsetAngle()) < Constants.TURRET_ANGLE_TOLERANCE;
	}

	/**
	 * Retreives offset angle from current turret angle
	 * @return Number from NetworkTable outputted by vision pipeline [0, 360]
	 */
	public double getOffsetAngle() {
		return offsetAngleEntry.getDouble(0);
	}

	/**
	 * Retrieves distance from camera to detected contour
	 * @return Number from NetworkTable outputted by vision pipeline
	 */
	public double getTargetDistance() {
		return targetDistanceEntry.getDouble(0); // TODO: units??
	}

	/**
	 * Set the switchable port on the REV PDH to true
	 */
	public void turnOnVisionLight(){
		pdh.setSwitchableChannel(true);
	}

	/**
	 * Set the switchable port on the REV PDH to false
	 */
	public void turnOffVisionLight(){
		pdh.setSwitchableChannel(false);
	}

	public double getTargetHeight() {
		return targetHeight;
	}

	public boolean hasVision(){
		return true;
	}
}
