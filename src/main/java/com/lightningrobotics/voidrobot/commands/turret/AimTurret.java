// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.turret;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.common.controller.PIDFController;
import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AimTurret extends CommandBase {

  private final Vision vision;
  private final Turret turret;

  private LightningIMU imu;

  private Rotation2d offsetAngle;
  private Rotation2d targetAngle;
  private Rotation2d constrainedAngle;

	private ShuffleboardTab turretTab = Shuffleboard.getTab("Turret");
  private NetworkTableEntry displayOffset;
	private NetworkTableEntry displayTargetAngle;
	private NetworkTableEntry displayConstrainedAngle;
	private NetworkTableEntry displayMotorOutput;

  private static double motorOutput;
  private DoubleSupplier controlerInput;
  private final Drivetrain drivetrain;

  private double testOffset;
  private double lastKnownHeading;

  enum TargetingState{
    MANUAL,
    VISION,
    NO_VISION
  }
  TargetingState targetingState;

  public AimTurret(Vision vision, Turret turret, Drivetrain drivetrain, LightningIMU imu, DoubleSupplier controllerInput) {
    this.vision = vision;
    this.drivetrain = drivetrain;
    this.turret = turret;
    this.imu = imu;
    this.controlerInput = controllerInput;

   

    addRequirements(vision, turret);

  }

  @Override
  public void initialize() {

    targetingState = TargetingState.MANUAL;

    drivetrain.resetPose();
    lastKnownHeading = turret.getCurrentAngle().getDegrees();

    displayOffset = turretTab.add("test offset", 0).getEntry();
    displayTargetAngle = turretTab.add("target angle", 0).getEntry();
    displayConstrainedAngle = turretTab.add("constrained angle", 0).getEntry();
    displayMotorOutput = turretTab.add("motor output", 0).getEntry();

  }

  @Override
  public void execute() {

   if (controlerInput.getAsDouble() == 0) { // vision.getDistance == -1
      targetingState = TargetingState.NO_VISION;
    } else {
     targetingState = TargetingState.MANUAL;
    }
    
    switch(targetingState) {
      case MANUAL: 
        testOffset += controlerInput.getAsDouble();
        break;
      case VISION:
        testOffset = vision.getOffsetAngle();
        break;
      case NO_VISION:
     
      // drivetrain.resetPose();
      // lastKnownHeading = turret.getCurrentAngle().getDegrees();

        double relativeX = drivetrain.getPose().getX();
        double relativeY = drivetrain.getPose().getY();
                // update rotation data 
                double changeInRotation = drivetrain.getPose().getRotation().getDegrees();
                SmartDashboard.putNumber("odometer x", relativeX);
                SmartDashboard.putNumber("odometer y", relativeY);
                SmartDashboard.putNumber("change in rotation", changeInRotation);

                 testOffset = turret.setOffsetNoVision(relativeX, relativeY, lastKnownHeading, 10, changeInRotation);
            break;
        
    }

    //offsetAngle = Rotation2d.fromDegrees(vision.getOffsetAngle());
    displayOffset.setDouble(testOffset); // offsetAngle.getDegrees()
    targetAngle = Rotation2d.fromDegrees(testOffset); // turret.getCurrentAngle().getDegrees() + offsetAngle.getDegrees()

    double sign = Math.signum(targetAngle.getDegrees());
    targetAngle =  Rotation2d.fromDegrees(sign * (((Math.abs(targetAngle.getDegrees()) + 180) % 360) - 180));
    displayTargetAngle.setDouble(targetAngle.getDegrees());

		constrainedAngle = Rotation2d.fromDegrees(LightningMath.constrain(targetAngle.getDegrees(), Constants.MIN_TURRET_ANGLE, Constants.MAX_TURRET_ANGLE));
    displayConstrainedAngle.setDouble(constrainedAngle.getDegrees());

    motorOutput = Constants.TURRET_PID.calculate(turret.getCurrentAngle().getDegrees(), constrainedAngle.getDegrees());
    displayMotorOutput.setDouble(motorOutput);
    turret.setPower(motorOutput);

  }

  @Override
  public void end(boolean interrupted) {
    turret.stop();

  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
