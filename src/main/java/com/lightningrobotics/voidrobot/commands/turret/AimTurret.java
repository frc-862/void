// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.turret;

import java.util.function.DoubleSupplier;


import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AimTurret extends CommandBase {

    private final Vision vision;
    private final Turret turret;

    private LightningIMU imu;

    private double targetAngle;
    private double constrainedAngle;
    private double initialIMUHeading; 

    private static ShuffleboardTab turretTab = Shuffleboard.getTab("Turret");
    private static NetworkTableEntry displayOffset  = turretTab.add("vision offset", 0).getEntry();
    private static NetworkTableEntry displayTargetAngle = turretTab.add("target angle", 0).getEntry();
    private static NetworkTableEntry displayConstrainedAngle = turretTab.add("constrained angle", 0).getEntry();
    private static NetworkTableEntry displayMotorOutput = turretTab.add("motor output", 0).getEntry();
    private static NetworkTableEntry manualOverrideEntry = turretTab.add("Manual Turret", false).getEntry();

    private static double motorOutput;
    private DoubleSupplier controllerInputX;
    private DoubleSupplier POV;
    private boolean usingManual = false;
    private final Drivetrain drivetrain;

    private double targetOffset;
    private double turretTrim = 0d; 
    private double lastKnownHeading = 0;
    private double lastKnownDistance = 2.7432;
    private boolean isUsingOdometer = true;
    private double initialOdometerGyroReading = 0d;
    private double initialX = 0d;
    private double initialY = 0d;

    enum TargetingState{
        MANUAL,
        VISION,
        NO_VISION,
        MANUAL_OVERRIDE
    }
    
	private TargetingState targetingState;

    public AimTurret(Vision vision, Turret turret, Drivetrain drivetrain, LightningIMU imu, DoubleSupplier controllerInputX, DoubleSupplier POV) {
        this.vision = vision;
        this.drivetrain = drivetrain;
        this.turret = turret;
        this.imu = imu;
        this.controllerInputX = controllerInputX;
        this.POV = POV;

        addRequirements(vision, turret);

		// displayOffset = turretTab.add("test offset", 0).getEntry();
        // displayTargetAngle = turretTab.add("target angle", 0).getEntry();
        // displayConstrainedAngle = turretTab.add("constrained angle", 0).getEntry();
        // displayMotorOutput = turretTab.add("motor output", 0).getEntry();

    }

    @Override
    public void initialize() {

        targetingState = TargetingState.NO_VISION;

        lastKnownHeading = turret.getCurrentAngle().getDegrees();
        initialIMUHeading = imu.getHeading().getDegrees();

    }

    @Override
    public void execute() {

        System.out.println("turret state ------------------------------------------------------------------------------------------------" + targetingState);

        if (turret.getManualOverride()){
            targetingState = TargetingState.MANUAL_OVERRIDE;
        } else {
            if (manualOverrideEntry.getBoolean(false)){
                if (usingManual){
                    usingManual = false;
                    targetingState = TargetingState.VISION;
                } else {
                    usingManual = true;
                    targetingState = TargetingState.MANUAL;
                }
            }
            
            if (!usingManual && vision.hasVision()){
                targetingState = TargetingState.VISION;
            } else if (!usingManual && !vision.hasVision()){
                targetingState = TargetingState.NO_VISION;
            }
        }
   
        switch(targetingState) {
            case MANUAL: 
                motorOutput = -1 * (controllerInputX.getAsDouble() / 4);
                break;
            case VISION:
                isUsingOdometer = true;
                targetOffset = vision.getOffsetAngle();
                lastKnownDistance = vision.getTargetDistance();
                targetAngle = turret.getCurrentAngle().getDegrees() + targetOffset;

                turret.setTarget(targetAngle);
                break;
            case NO_VISION:
                if(isUsingOdometer){
                    isUsingOdometer = false;
                    resetPose();
                    turretTrim = 0;
                }
                
                //turretTrim += POVToStandard(POV); <-- TODO: Test this

                double relativeX = drivetrain.getPose().getX() - initialX;
                double relativeY = drivetrain.getPose().getY() - initialY;

                // rotate from odometer-center to robot-center
                relativeX = turret.rotateX(relativeX, relativeY, initialOdometerGyroReading);
                relativeY = turret.rotateY(relativeX, relativeY, initialOdometerGyroReading);

                // update rotation data 
                double changeInRotation = drivetrain.getPose().getRotation().getDegrees() - initialOdometerGyroReading;
                // SmartDashboard.putNumber("odometer x", relativeX);
                // SmartDashboard.putNumber("odometer y", relativeY);
                // SmartDashboard.putNumber("change in rotation", changeInRotation);

                targetAngle = turret.getTargetNoVision(relativeX, relativeY, lastKnownHeading, lastKnownDistance, changeInRotation);

                turret.setTarget(targetAngle);
                break;   
            case MANUAL_OVERRIDE:
                targetAngle = turret.getTarget();
                turret.setTarget(targetAngle);
                break;
        }

        displayOffset.setDouble(targetOffset); // offsetAngle.getDegrees()
        displayTargetAngle.setDouble(targetAngle);
        displayMotorOutput.setDouble(motorOutput);
        
        motorOutput = turret.getMotorOutput(turret.getTarget());
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

    public void resetPose(){
        initialOdometerGyroReading = drivetrain.getPose().getRotation().getDegrees();
        initialX = drivetrain.getPose().getX();
        initialY = drivetrain.getPose().getY();
        lastKnownHeading = turret.getCurrentAngle().getDegrees();
    }

    public double POVToStandard(DoubleSupplier POV){
        if (POV.getAsDouble() == 90){
            return 1;
        } else if (POV.getAsDouble() == 270){
            return -1;
        } else {
            return 0;
        }
    }

}
