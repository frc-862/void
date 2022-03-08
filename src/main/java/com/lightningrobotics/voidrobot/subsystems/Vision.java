package com.lightningrobotics.voidrobot.subsystems;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.constants.RobotMap;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Vision extends SubsystemBase {

	// Network Table for Vision
	private final NetworkTable visionTable = NetworkTableInstance.getDefault().getTable("Vision");

	// Entries for Angle & Distance	
	private final NetworkTableEntry targetAngleEntry = visionTable.getEntry("Target Angle");
	private final NetworkTableEntry targetDistanceEntry = visionTable.getEntry("Target Distance");

	// Placeholder Vars for Angle & Distance
	private static double targetDistance = -1d;
    private static double offsetAngle = 0d;
	
	// Var for if green LEDs are on
	private static boolean lightsOn = false;

	// PDH
	PowerDistribution pdh = new PowerDistribution(RobotMap.PDH_ID, ModuleType.kRev);

	public Vision() {
		turnOffVisionLight();
	}

	@Override
	public void periodic() {
		
		// Update Target Angle
		offsetAngle = targetAngleEntry.getDouble(offsetAngle);
		
		// Update Target Distance
		// targetDistance = -1;
		targetDistance = targetDistanceEntry.getDouble(targetDistance);


		SmartDashboard.putNumber("inputted target distance from vision", targetDistanceEntry.getDouble(0));
		SmartDashboard.putNumber("inputted target angle from vision", targetAngleEntry.getDouble(0));

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
		return -offsetAngle; 
	}

	/**
	 * Retrieves distance from camera to detected contour
	 * @return Number from NetworkTable outputted by vision pipeline
	 */
	public double getTargetDistance() {
		return targetDistanceEntry.getDouble(0); 
	}

	/**
	 * Set the switchable port on the REV PDH to true
	 */
	public void turnOnVisionLight(){
		pdh.setSwitchableChannel(true);
		lightsOn = true;
	}

	/**
	 * Set the switchable port on the REV PDH to false
	 */
	public void turnOffVisionLight(){
		pdh.setSwitchableChannel(false);
		lightsOn = false;
	}

	public void toggleVisionLights() {
		if(lightsOn) turnOffVisionLight();
		else turnOnVisionLight();
	}

	public boolean visionLightsOn() {
		return lightsOn;
	}

	public boolean hasVision(){
		return targetDistance != -1;
	}

}
