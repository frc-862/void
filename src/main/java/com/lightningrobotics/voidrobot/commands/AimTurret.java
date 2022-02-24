package com.lightningrobotics.voidrobot.commands;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.common.geometry.kinematics.DrivetrainSpeed;
import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AimTurret extends CommandBase {
    
    // Creates our turret and vision subsystems
    private Turret turret;
    private Vision vision;
    private double lastVisionOffset;
    private Drivetrain drivetrain;
    private boolean isUsingVision = true;
	private double realHeadingTowardsTarget = 0d;

    private static double targetAngle = 0; // this is the angle that we are setting to the turret


    private static double offsetAngle = 0d; // The offset that vision gives us
    private static double currentAngle; // The current angle of the turret
    private DoubleSupplier controllerInput;

    public AimTurret(Turret turret, Vision vision, Drivetrain drivetrain) {
        this.turret = turret;
        this.vision = vision;
        this.drivetrain = drivetrain;
        // Not adding vision since its use is read-only
        addRequirements(turret, vision, drivetrain);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void execute() {
        if(vision.hasVision()){
            //offsetAngle  = vision.getOffsetAngle(); // gets the vision offset angle 
            lastVisionOffset = vision.getOffsetAngle();
            turret.setVisionOffset(lastVisionOffset); // setting the target angle of the turret
            isUsingVision = true;
        } else {
            
        if (isUsingVision){
            // As soon as we lose vision, we reset drivetrain pose/axis, get current angle degree
            //navXOrigin = navX.getHeading().getDegrees();
            drivetrain.resetPose();
            isUsingVision = false;
            realHeadingTowardsTarget = turret.getTurretAngle().getDegrees();// + lastVisionOffset;
        } 

        // get (x,y) relative to the robot. the X and Y axis is created when we reset the drivetrain odometer
        double relativeX = drivetrain.getPose().getX();
        double relativeY = drivetrain.getPose().getY();
        // Get the last time vision saw a distance
        double lastVisionDistance = lastVisionOffset;
        // Get the change in rotation from the time we lose vision
        double changeInRotation = drivetrain.getPose().getRotation().getDegrees();

        turret.setOffsetNoVision(relativeX, relativeY, realHeadingTowardsTarget, lastVisionDistance, changeInRotation);
        }
    }

    @Override
    public void end(boolean interrupted) {
        turret.stopTurret();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

}