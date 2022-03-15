package com.lightningrobotics.voidrobot.commands.turret;

import com.lightningrobotics.common.util.filter.MovingAverageFilter;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AimTurret extends CommandBase {

    private final Vision vision;
    private final Turret turret;
    private final Drivetrain drivetrain;

    private double targetAngle; 
    private double targetOffset;
    private double lastKnownHeading = 0;
    private double lastKnownDistance = 2; // TODO 
    private double initialOdometerGyroReading = 0d;
    private double initialX = 0d;
    private double initialY = 0d;

	private MovingAverageFilter maf = new MovingAverageFilter(3);

    public AimTurret(Vision vision, Turret turret, Drivetrain drivetrain) {
        this.vision = vision;
        this.drivetrain = drivetrain;
        this.turret = turret;

        addRequirements(turret);

    }

    @Override
    public void initialize() {
        resetPose();
    }

    @Override
    public void execute() {
   
        if (vision.hasVision()) {
            targetOffset = vision.getOffsetAngle();
            lastKnownDistance = vision.getTargetDistance();
            targetAngle = turret.getCurrentAngle().getDegrees() + targetOffset;

            targetAngle = maf.filter(targetAngle);
            lastKnownHeading = targetAngle;

            turret.setAngle(targetAngle);
            resetPose();

        } else {
            double relativeX = drivetrain.getPose().getX() - initialX;
            double relativeY = drivetrain.getPose().getY() - initialY;

            // rotate from odometer-center to robot-center
            relativeX = turret.rotateX(relativeX, relativeY, initialOdometerGyroReading);
            relativeY = turret.rotateY(relativeX, relativeY, initialOdometerGyroReading);

            // update rotation data 
            double changeInRotation = drivetrain.getPose().getRotation().getDegrees() - initialOdometerGyroReading;

            targetAngle = turret.getTargetNoVision(relativeX, relativeY, lastKnownHeading, lastKnownDistance, changeInRotation) + targetOffset;

            turret.setAngle(targetAngle);
            // TODO: set the distance somewhere so we can maybe shoot without vision
        
        }
    }

    public void resetPose(){
        initialOdometerGyroReading = drivetrain.getPose().getRotation().getDegrees();
        initialX = drivetrain.getPose().getX();
        initialY = drivetrain.getPose().getY();
        lastKnownHeading = turret.getCurrentAngle().getDegrees();
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