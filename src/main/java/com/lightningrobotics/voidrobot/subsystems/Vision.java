package com.lightningrobotics.voidrobot.subsystems;

import com.lightningrobotics.common.logging.DataLogger;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.constants.RobotMap;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Vision extends SubsystemBase {

	// Network Table for Vision
	private final NetworkTable visionTable = NetworkTableInstance.getDefault().getTable("Vision");

	// Entries for Angle & Distance	
	private final NetworkTableEntry targetAngleEntry = visionTable.getEntry("Target Angle");
	private final NetworkTableEntry targetDistanceEntry = visionTable.getEntry("Target Distance");
	private final NetworkTableEntry targetTimeEntry = visionTable.getEntry("Target Time");

	private final ShuffleboardTab visionTab = Shuffleboard.getTab("Vision Tab");
	private final NetworkTableEntry bias = visionTab.add("Bias", 0).getEntry();

	// Placeholder Vars for Angle & Distance
	private static double targetDistance = -1d;
    private static double offsetAngle = 0d;

	private double lastVisionTimestamp = 0;
	private double visionTimestamp = 0;

	private double gyroDistance = 0;

	private static boolean haveData = false;

	private double distanceOffset = 0;

	// PDH
	PowerDistribution pdh = new PowerDistribution(RobotMap.PDH_ID, ModuleType.kRev);

	public Vision() {
		turnOffVisionLight();
		
		initLogging();

		CommandScheduler.getInstance().registerSubsystem(this);
	}

	@Override
	public void periodic() {
		// Update Target Angle
		offsetAngle = targetAngleEntry.getDouble(offsetAngle);
		
		// Update Target Distance
		targetDistance = Units.inchesToMeters(targetDistanceEntry.getDouble(targetDistance)) + distanceOffset;

		visionTimestamp = targetTimeEntry.getDouble(0);

		bias.setDouble(distanceOffset);

		if(!hasVision() || lastVisionTimestamp == visionTimestamp) {
			haveData = false;
			targetDistance = gyroDistance;
		} else {
			haveData = true;
			lastVisionTimestamp = visionTimestamp;
		}

	}

	private void initLogging() {
		DataLogger.addDataElement("visionAngle", () -> offsetAngle);
		DataLogger.addDataElement("targetDistance", () -> targetDistance);
		DataLogger.addDataElement("distanceOffset", () -> distanceOffset);
	}

	public void setGyroDistance(double gyroDistance) {
		this.gyroDistance = gyroDistance;
	}

	public boolean isOnTarget() {
		return Math.abs(getOffsetAngle()) < Constants.TURRET_TOLERANCE;
	}

	public double getOffsetAngle() {
		return -offsetAngle;
	}

	public double getTargetDistance() {
		return targetDistance;
	}

	public void turnOnVisionLight(){
		pdh.setSwitchableChannel(true);
	}

	public void turnOffVisionLight(){
		pdh.setSwitchableChannel(false);
	}

	public void toggleVisionLights() {
		if(visionLightsOn()) turnOffVisionLight();
		else turnOnVisionLight();
	}

	public boolean visionLightsOn() {
		return pdh.getSwitchableChannel();
	}

	public boolean hasVision(){
		return targetDistance > 0;
	}

	public boolean isNewData() {
		return haveData;
	}

	public void adjustBias(double delta) {
		distanceOffset += delta;
	}

	public void zeroBias() {
		distanceOffset = 0;
	}
}
