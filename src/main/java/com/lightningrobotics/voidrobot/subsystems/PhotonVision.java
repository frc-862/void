// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import org.photonvision.*;
import org.photonvision.targeting.PhotonTrackedTarget;

import com.lightningrobotics.voidrobot.constants.Constants;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.math.util.Units;

public class PhotonVision extends SubsystemBase {
  /** Creates a new PhotonVision. */

  NetworkTableEntry latency;

  NetworkTableEntry targetYaw;
  NetworkTableEntry targetPitch;
  NetworkTableEntry targetArea;
  NetworkTableEntry targetSkew;
  NetworkTableEntry targetDistance;

  private double lastKnownDistance = 0d;

  PhotonCamera camera = new PhotonCamera("pv camera");

  public PhotonVision() {

    // Instantiate network table as photonvision
    NetworkTable photonTable = NetworkTableInstance.getDefault().getTable("photonvision");

    // NetworkTable photonTable = inst.getTable("photonvision");
    latency = photonTable.getEntry("Photonvision Latency");
    targetYaw = photonTable.getEntry("Photonvision Target Yaw");
    targetPitch = photonTable.getEntry("Photonvision Target Pitch");
    targetArea = photonTable.getEntry("Photonvision Target Area");
    targetSkew = photonTable.getEntry("Photonvision Target Skew");
    targetDistance = photonTable.getEntry("Photonvision Target Distance");

    targetYaw.setDouble(0d);
    targetPitch.setDouble(0d);
    targetArea.setDouble(0d);
    targetSkew.setDouble(0d);
    targetDistance.setDouble(0d);

  }

  private double getHubDistance(double pitch) {
    double distanceBias = 0d;
	  var rawDistanceInches = // calc raw distance from angle
	  	(Constants.HUB_HEIGHT-Constants.MOUNT_HEIGHT) / 
	  	Math.tan(Math.toRadians(Constants.MOUNT_ANGLE + pitch)) + 
	  	Constants.HUB_CENTER_OFFSET; 
	  double processedDistance = Units.inchesToMeters(rawDistanceInches) + distanceBias; // add biases/on-the-fly offsets, etc. CURRENTLY BROKEN.
	  lastKnownDistance = processedDistance;
	  return processedDistance;
	}   
    

  @Override
  public void periodic() {
    // This method will be called once per scheduler run (20ms)

    // Gets processed result from camera
    var result = this.camera.getLatestResult();

    // Calculates latency of pipeline
    double latencySeconds = result.getLatencyMillis() / 1000.0;

    // Pushes latency in seconds to network tables 
    latency.setDouble(latencySeconds);

    boolean hasTargets = result.hasTargets();

    if (hasTargets) {
      PhotonTrackedTarget target = result.getBestTarget();

      // Gets the angle of the target from the center of the camera
      double yaw = target.getYaw();
      double pitch = target.getPitch();
      double area = target.getArea();
      double skew = target.getSkew();

      double hubDistance = getHubDistance(pitch);

      // Pushes target data to network tables
      targetYaw.setDouble(yaw);
      targetPitch.setDouble(pitch);
      targetArea.setDouble(area);
      targetSkew.setDouble(skew);
      targetDistance.setDouble(hubDistance);

    }
  }
}