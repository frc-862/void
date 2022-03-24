package com.lightningrobotics.voidrobot.subsystems;

import com.lightningrobotics.common.logging.DataLogger;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.constants.RobotMap;

import edu.wpi.first.math.filter.MedianFilter;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Vision extends SubsystemBase {

	// Network Table for Vision
	private final NetworkTable visionTable = NetworkTableInstance.getDefault().getTable("limelight");

	// Entries for Angle & Distance
	private final NetworkTableEntry targetArea = visionTable.getEntry("ta");
	private final NetworkTableEntry targetOffsetX = visionTable.getEntry("tx");
	private final NetworkTableEntry targetOffsetY = visionTable.getEntry("ty");
	private final NetworkTableEntry targetTimeEntry = visionTable.getEntry("tl");
	private final NetworkTableEntry targetDetected = visionTable.getEntry("tv");
	private final NetworkTableEntry ledMode = visionTable.getEntry("ledMode");
	private final NetworkTableEntry snapShot = visionTable.getEntry("snapshot");

	private final ShuffleboardTab visionTab = Shuffleboard.getTab("Vision Tab");
	private final NetworkTableEntry visionDistanceEntry = visionTab.add("Vision Distance", 0).getEntry();
	private final NetworkTableEntry visionAngleEntry = visionTab.add("Vision Angle", 0).getEntry();
	private final NetworkTableEntry bias = visionTab.add("Bias", 0).getEntry();

	
	private ShuffleboardTab tuneTab = Shuffleboard.getTab("tune tab");
	private final NetworkTableEntry visionDistanceTuneEntry = tuneTab.add("Vision Distance Tune", 0).getEntry();
	private final NetworkTableEntry visionAngleTuneEntry = tuneTab.add("Vision Angle Tune", 0).getEntry();

	// Placeholder Vars for Angle & Distance
	private static double targetDistance = -1d;
    private static double offsetAngle = 0d;

	private double lastVisionSnapshot = 0;
	private boolean snapshotEnabled = false;
	private MedianFilter mf = new MedianFilter(5);
	private double odometerDistance = 0;

	private double distanceOffset = 0;

	// PDH
	PowerDistribution pdh = new PowerDistribution(RobotMap.PDH_ID, ModuleType.kRev);

	public Vision() {
		initLogging();
		snapShot.setNumber(0);

		CommandScheduler.getInstance().registerSubsystem(this);
	}

	@Override
	public void periodic() {
		// Update Target Angle
		offsetAngle = mf.calculate(targetOffsetX.getDouble(offsetAngle));
		
		// Update Target Distance
		targetDistance = Units.inchesToMeters(limelightOffsetToDistance(targetOffsetY.getDouble(-1)) + distanceOffset);

		bias.setDouble(distanceOffset);

		if(!hasVision()) {
			targetDistance = odometerDistance;
		}

		visionAngleEntry.setNumber(offsetAngle);
		visionDistanceEntry.setNumber(limelightOffsetToDistance(targetOffsetY.getDouble(-1)) + distanceOffset);

		visionAngleTuneEntry.setNumber(offsetAngle);
		visionDistanceTuneEntry.setNumber(limelightOffsetToDistance(targetOffsetY.getDouble(-1)) + distanceOffset);

		if (DriverStation.isEnabled()) {
			if(!snapshotEnabled){
				snapShot.setNumber(0);
				snapshotEnabled = true;
			}
			else if (Timer.getFPGATimestamp() - lastVisionSnapshot > 0.5) {
				lastVisionSnapshot	= Timer.getFPGATimestamp();
				snapShot.setNumber(1);
				System.out.println("Snapshot?");
			}
		}
	}

	public double limelightOffsetToDistance(double offset) {
		double mountHeight = 37.5;
		double hubHeight = 104;
		double mountAngle = 32;
		double hubCenterOffset = 24;

		return (hubHeight-mountHeight)/Math.tan(Math.toRadians(mountAngle+offset)) + hubCenterOffset;
	}

	private void initLogging() {
		DataLogger.addDataElement("visionAngle", () -> offsetAngle);
		DataLogger.addDataElement("targetDistance", () -> targetDistance);
		DataLogger.addDataElement("distanceOffset", () -> distanceOffset);
		DataLogger.addDataElement("targetArea", () -> targetArea.getDouble(-1));
		DataLogger.addDataElement("targetX", () -> targetOffsetX.getDouble(-1));
		DataLogger.addDataElement("targetY", () -> targetOffsetY.getDouble(-1));
	}

	public void setGyroDistance(double odometerDistance) {
		this.odometerDistance = odometerDistance;
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
		ledMode.setNumber(3);
	}

	public void turnOffVisionLight(){
		ledMode.setNumber(1);
	}

	public void toggleVisionLights() {
		if(visionLightsOn()) turnOffVisionLight();
		else turnOnVisionLight();
	}

	public boolean visionLightsOn() {
		return ledMode.getDouble(0) == 3 || ledMode.getDouble(0) == 0;
	}

	public boolean hasVision() {
		return targetDistance > 0 && targetDetected.getDouble(0) == 1;
	}

	public void adjustBias(double delta) {
		distanceOffset += delta;
	}

	public void zeroBias() {
		distanceOffset = 0;
	}
}
