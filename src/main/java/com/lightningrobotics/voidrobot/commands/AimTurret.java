package com.lightningrobotics.voidrobot.commands;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.common.geometry.kinematics.DrivetrainSpeed;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AimTurret extends CommandBase {
    
    // Creates our turret and vision subsystems
    private Turret turret;
    private Vision vision;
    private double lastVisionOffset;
    private double lastVisionDistance;
    private Drivetrain drivetrain;
    boolean isUsingVision;

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
    public void initialize() {}

    @Override
    public void execute() {
        if(vision.hasVision()){
            //offsetAngle  = vision.getOffsetAngle(); // gets the vision offset angle 
            lastVisionOffset = vision.getOffsetAngle();
            turret.setVisionOffset(lastVisionOffset); // setting the target angle of the turret
            isUsingVision = true;
        } else {
            
            if (isUsingVision){
                //navXOrigin = navX.getHeading().getDegrees();
                drivetrain.resetPose();
                isUsingVision = false;
            } 
            
            double relativeX = drivetrain.getPose().getX();
            double relativeY = drivetrain.getPose().getY();

            //DrivetrainSpeed absPose = DrivetrainSpeed.fromFieldCentricSpeed(relativeX, relativeY, 0, drivetrain.getPose().getRotation());
            turret.setOffsetNoVision(relativeX, relativeY, lastVisionOffset, lastVisionDistance);
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
