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

    private double targetAngle;
    private double constrainedAngle;
    private double initialIMUHeading; 

    private ShuffleboardTab turretTab = Shuffleboard.getTab("Turret");
    private NetworkTableEntry displayOffset;
    private NetworkTableEntry displayTargetAngle;
    private NetworkTableEntry displayConstrainedAngle;
    private NetworkTableEntry displayMotorOutput;

    private static double motorOutput;
    private DoubleSupplier controllerInputX;
    private DoubleSupplier controllerInputY;
    private final Drivetrain drivetrain;

    private double targetOffset;
    private double lastKnownHeading;
    private double lastKnownDistance;
    private boolean isUsingOdometer = true;

    enum TargetingState{
        MANUAL,
        VISION,
        NO_VISION
    }
    TargetingState targetingState;

    public AimTurret(Vision vision, Turret turret, Drivetrain drivetrain, LightningIMU imu, DoubleSupplier controllerInputX, DoubleSupplier controllerInputY) {
        this.vision = vision;
        this.drivetrain = drivetrain;
        this.turret = turret;
        this.imu = imu;
        this.controllerInputX = controllerInputX;
        this.controllerInputY = controllerInputY;

        addRequirements(vision, turret);
    }

    @Override
    public void initialize() {

        targetingState = TargetingState.MANUAL;

        drivetrain.resetPose();
        lastKnownHeading = turret.getCurrentAngle().getDegrees();
        initialIMUHeading = imu.getHeading().getDegrees();

        displayOffset = turretTab.add("vision offset", 0).getEntry();
        displayTargetAngle = turretTab.add("target angle", 0).getEntry();
        displayConstrainedAngle = turretTab.add("constrained angle", 0).getEntry();
        displayMotorOutput = turretTab.add("motor output", 0).getEntry();

    }

    @Override
    public void execute() {

        if (controllerInputX.getAsDouble() == 0) { // vision.getDistance == -1
            targetingState = TargetingState.NO_VISION;
        } else {
            targetingState = TargetingState.MANUAL;
        }
            
        switch(targetingState) {
            case MANUAL: 
                //testOffset += controllerInput.getAsDouble(); <-- old manual control

                // Just sets the target to the aim of the stick
                if (controllerInputY.getAsDouble() >= 0){
                    targetAngle = (-1 * (Math.toDegrees(Math.atan(controllerInputX.getAsDouble()/controllerInputY.getAsDouble()))));
                } else {
                    targetAngle = (-180 - Math.toDegrees(Math.atan(controllerInputX.getAsDouble()/controllerInputY.getAsDouble())));
                }

                //makes it field centric based on the imu
                targetAngle = (imu.getHeading().getDegrees() - initialIMUHeading) + targetAngle;
                
                break;
            case VISION:
                isUsingOdometer = true;
                targetOffset = vision.getOffsetAngle();
                lastKnownDistance = vision.getTargetDistance();
                targetAngle = turret.getCurrentAngle().getDegrees() + targetOffset;
                break;
            case NO_VISION:
                if(isUsingOdometer){
                    isUsingOdometer = false;
                    drivetrain.resetPose();
                    lastKnownHeading = turret.getCurrentAngle().getDegrees();
                }

                double relativeX = drivetrain.getPose().getX();
                double relativeY = drivetrain.getPose().getY();

                // update rotation data 
                double changeInRotation = drivetrain.getPose().getRotation().getDegrees();
                SmartDashboard.putNumber("odometer x", relativeX);
                SmartDashboard.putNumber("odometer y", relativeY);
                SmartDashboard.putNumber("change in rotation", changeInRotation);

                targetAngle = turret.getTargetNoVision(relativeX, relativeY, lastKnownHeading, lastKnownDistance, changeInRotation);
                break;   
        }

        //offsetAngle = Rotation2d.fromDegrees(vision.getOffsetAngle());
        displayOffset.setDouble(targetOffset); // offsetAngle.getDegrees()
        //targetAngle = testOffset; // turret.getCurrentAngle().getDegrees() + offsetAngle.getDegrees()

        double sign = Math.signum(targetAngle);
        targetAngle =  sign * (((Math.abs(targetAngle) + 180) % 360) - 180);
        displayTargetAngle.setDouble(targetAngle);

        constrainedAngle = LightningMath.constrain(targetAngle, Constants.MIN_TURRET_ANGLE, Constants.MAX_TURRET_ANGLE);
        displayConstrainedAngle.setDouble(constrainedAngle);

        if(constrainedAngle - turret.getCurrentAngle().getDegrees() <= Constants.SLOW_PID_THRESHOLD) {
            motorOutput = Constants.TURRET_PID_SLOW.calculate(turret.getCurrentAngle().getDegrees(), constrainedAngle);
        } else {
            motorOutput = Constants.TURRET_PID_FAST.calculate(turret.getCurrentAngle().getDegrees(), constrainedAngle);
        }
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
