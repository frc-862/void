package com.lightningrobotics.voidrobot.subsystems;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.constants.RobotMap;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Vision extends SubsystemBase {

	// Network Table for Vision
	private final NetworkTable visionTable = NetworkTableInstance.getDefault().getTable("Vision");

	// Entries for Angle & Distance	
	private final NetworkTableEntry targetAngleEntry = visionTable.getEntry("Target Angle");
	private final NetworkTableEntry targetDistanceEntry = visionTable.getEntry("Target Distance");
	private final NetworkTableEntry targetTimeEntry = visionTable.getEntry("Target Time");

	private final ShuffleboardTab biasTab = Shuffleboard.getTab("Biases");

	private final NetworkTableEntry distanceBiasEntry = biasTab.add("distance bias", 0).getEntry();

	// Placeholder Vars for Angle & Distance
	private static double targetDistance = -1d;
	private static double visionMode = 0;
    private static double offsetAngle = 0d;

	private boolean findMode = false; 
	
	private double startVisionTimer = 0;

	private static double lastTargetDistance = 0;

	private double lastVisionTimestamp = 0;
	private double visionTimestamp = 0;

	private static boolean haveData = false;

	private ArrayList<Double> visionArray = new ArrayList<Double>();
	
	// Var for if green LEDs are on
	private static boolean lightsOn = false;

	private double lastGoodDistance = 0;

	private double distanceOffset = 0;

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
		targetDistance = targetDistanceEntry.getDouble(targetDistance) / 12;

		visionTimestamp = targetTimeEntry.getDouble(0);

		if(Timer.getFPGATimestamp() - startVisionTimer <= Constants.READ_VISION_TIME) {
			visionArray.add(targetDistance);
		} else if(findMode){
			visionMode = getMode(visionArray);
			findMode = false;
		}

		if(!hasVision() || lastVisionTimestamp == visionTimestamp) {
			haveData = false;
		} else {
			haveData = true;
			lastVisionTimestamp = visionTimestamp;
		}

		SmartDashboard.putNumber("inputted target distance from vision", targetDistance);
		SmartDashboard.putNumber("inputted target angle from vision", offsetAngle);

		SmartDashboard.putNumber("vision size", visionArray.size());
		SmartDashboard.putNumber("vision mode", visionMode);

		SmartDashboard.putBoolean("has data", haveData);

		SmartDashboard.putNumber("rpm from map", Constants.DISTANCE_RPM_MAP.get(targetDistance));

		SmartDashboard.putNumber("last good distance", lastGoodDistance);

		distanceOffset = distanceBiasEntry.getDouble(0);

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
		return targetDistance + distanceOffset; 
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
		return targetDistance != -0;
	}

	public double getMode(ArrayList<Double> array){
	double mode = 0;
	int count = 0;
		if(array.size() > 1) {
			for (int i = 0; i < array.size() ; i++) {
				double x = array.get(i);
				int tempCount = 1;

				for(int e = 0; e < array.size() ; e++){
					double x2 = array.get(e);

					if( x == x2)
						tempCount++;

					if( tempCount > count){
						count = tempCount;
						mode = x;
					}
				}
			}

			return mode;

		} else  {
			return 0d;
		}
	  }

	  public void startTimer() {
		  startVisionTimer = Timer.getFPGATimestamp();
		  visionArray.clear();
		  visionArray.add(0d);

		  findMode = true;
	  }

	  public boolean isNewData() {
		  
		
		return haveData;
	  }

	  public void setGoodDistance() {
		  lastGoodDistance = targetDistance;
	  }

	  public double getGoodDistance() {
		  return lastGoodDistance;
	  }

	  public void setDistanceOffset(double offset) {
		distanceOffset = offset;
	  }

}
