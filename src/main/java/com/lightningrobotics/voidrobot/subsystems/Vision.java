package com.lightningrobotics.voidrobot.subsystems;

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
	private final NetworkTableEntry targetAngleEntry = visionTable.getEntry("Angle");
	private final NetworkTableEntry targetDistanceEntry = visionTable.getEntry("Distance");

	// Placeholder Vars for Angle & Distance
	private static double targetAngle = 0d;
	private static double targetDistance = 0d;
    private static double offsetAngle = 0d;

	//PDH
	PowerDistribution pdh = new PowerDistribution(RobotMap.PDH_ID, ModuleType.kRev);

	public Vision() {}

	@Override
	public void periodic() {
		
		// Update Target Angle
		offsetAngle = targetAngleEntry.getDouble(targetAngle);

		// Update Target Distance
		targetDistance = targetDistanceEntry.getDouble(targetDistance);

	}

	public boolean isOnTarget() {
		if(Math.abs(getOffsetAngle()) < 3) {
			return true;
		} else {
			return false;
		}
	}

	public double getOffsetAngle() {
		// TODO: implement math for error to get target angle
		return offsetAngle; 
	}

	public double getTargetDistance() {
		return targetDistance;
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

}
