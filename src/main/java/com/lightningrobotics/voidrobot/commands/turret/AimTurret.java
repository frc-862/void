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

    private static ShuffleboardTab turretTab = Shuffleboard.getTab("Turret");
    private static NetworkTableEntry displayOffset = turretTab.add("test offset2", 0).getEntry();
    private static NetworkTableEntry displayTargetAngle = turretTab.add("target angle", 0).getEntry();
    private static NetworkTableEntry displayConstrainedAngle = turretTab.add("constrained angle", 0).getEntry();
    private static NetworkTableEntry displayMotorOutput = turretTab.add("motor output", 0).getEntry();

    private double motorOutput;
    private DoubleSupplier controlerInput;
    private final Drivetrain drivetrain;

    private double testOffset;
    private double lastKnownHeading;
    private double lastKnownDistance;
    private boolean isUsingVision = true;

    enum TargetingState{
        MANUAL,
        VISION,
        NO_VISION
    }
    
	private TargetingState targetingState;

    public AimTurret(Vision vision, Turret turret, Drivetrain drivetrain, LightningIMU imu, DoubleSupplier controllerInput) {
        
		this.vision = vision;
        this.drivetrain = drivetrain;
        this.turret = turret;
        this.imu = imu;
        this.controlerInput = controllerInput; // supplied offset for now

        addRequirements(vision, turret);

		// displayOffset = turretTab.add("test offset", 0).getEntry();
        // displayTargetAngle = turretTab.add("target angle", 0).getEntry();
        // displayConstrainedAngle = turretTab.add("constrained angle", 0).getEntry();
        // displayMotorOutput = turretTab.add("motor output", 0).getEntry();

    }

    @Override
    public void initialize() {

        targetingState = TargetingState.MANUAL;

        drivetrain.resetPose();
        lastKnownHeading = turret.getCurrentAngle().getDegrees();

    }

    @Override
    public void execute() {

        // if (controlerInput.getAsDouble() == 0) { // vision.getDistance == -1
        //     targetingState = TargetingState.NO_VISION;
        // } else {
        //     targetingState = TargetingState.MANUAL;
        // }
            
        switch(targetingState) {
            case MANUAL: 
                testOffset = controlerInput.getAsDouble(); // target angle for testing 
                break;
            case VISION:
                isUsingVision = true;
                testOffset = vision.getOffsetAngle();
                lastKnownDistance = vision.getTargetDistance();
                break;
            case NO_VISION:
                if(isUsingVision){
                    isUsingVision = false;
                    drivetrain.resetPose(); // TODO instead of resetting the pose, we dhould just measure a delta. dont want to accidentally reset in auto...
                    lastKnownHeading = turret.getCurrentAngle().getDegrees();
                }

                double relativeX = drivetrain.getPose().getX();
                double relativeY = drivetrain.getPose().getY();

                // update rotation data 
                double changeInRotation = drivetrain.getPose().getRotation().getDegrees();
                SmartDashboard.putNumber("odometer x", relativeX);
                SmartDashboard.putNumber("odometer y", relativeY);
                SmartDashboard.putNumber("change in rotation", changeInRotation);

                testOffset = turret.getOffsetNoVision(relativeX, relativeY, lastKnownHeading, lastKnownDistance, changeInRotation);
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

        if(constrainedAngle.getDegrees() - turret.getCurrentAngle().getDegrees() <= Constants.SLOW_PID_THRESHOLD) {
            motorOutput = Constants.TURRET_PID_SLOW.calculate(turret.getCurrentAngle().getDegrees(), constrainedAngle.getDegrees());
        } else {
            motorOutput = Constants.TURRET_PID_FAST.calculate(turret.getCurrentAngle().getDegrees(), constrainedAngle.getDegrees());
        }

		// Set Motor Output
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
