// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import org.photonvision.*;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class PhotonVision extends SubsystemBase {
  /** Creates a new PhotonVision. */

  NetworkTableEntry latency;

  NetworkTableEntry targetYaw;
  NetworkTableEntry targetPitch;
  NetworkTableEntry targetArea;
  NetworkTableEntry targetSkew;

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

      // Pushes target data to network tables
      targetYaw.setDouble(yaw);
      targetPitch.setDouble(pitch);
      targetArea.setDouble(area);
      targetSkew.setDouble(skew);
      
      // Gets the distance of the target from the camera




    }
  }
}