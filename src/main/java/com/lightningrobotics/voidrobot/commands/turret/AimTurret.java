package com.lightningrobotics.voidrobot.commands.turret;

import com.lightningrobotics.common.util.filter.MovingAverageFilter;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.math.filter.MedianFilter;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AimTurret extends CommandBase {

    private final Vision vision;
    private final Turret turret;
    private final Drivetrain drivetrain;

    private double targetAngle; 
    private double targetOffset;
    private double lastKnownHeading;
    private double lastKnownDistance = 1.75; // TODO 
    private double odometerDistance;
    private double initialOdometerGyroReading = 0d;
    private double initialX = 0d;
    private double initialY = 0d;

	// private MovingAverageFilter maf = new MovingAverageFilter(3);
    private MedianFilter mf = new MedianFilter(3);

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

            targetAngle = mf.calculate(targetAngle);
            lastKnownHeading = targetAngle;

            turret.setAngle(targetAngle);
            resetPose();

        } else {
            double relativeX = drivetrain.getPose().getX() - initialX;
            double relativeY = drivetrain.getPose().getY() - initialY;

            // rotate from odometer-center to robot-center
            relativeX = rotateX(relativeX, relativeY, initialOdometerGyroReading);
            relativeY = rotateY(relativeX, relativeY, initialOdometerGyroReading);

            // update rotation data 
            double changeInRotation = drivetrain.getPose().getRotation().getDegrees() - initialOdometerGyroReading;

            targetAngle = getTargetNoVision(relativeX, relativeY, lastKnownHeading, lastKnownDistance, changeInRotation); // + targetOffset;

            vision.setGyroDistance(odometerDistance);

            turret.setAngle(targetAngle);
        
        }

        SmartDashboard.putNumber("set target angle", targetAngle);
    }

    public void resetPose(){
        initialOdometerGyroReading = drivetrain.getPose().getRotation().getDegrees();
        initialX = drivetrain.getPose().getX();
        initialY = drivetrain.getPose().getY();
        lastKnownHeading = turret.getCurrentAngle().getDegrees();
    }

    public double getTargetNoVision(double relativeX, double relativeY, double realTargetHeading, double lastVisionDistance, double changeInRotation){
		
		double realX = rotateX(relativeX, relativeY, realTargetHeading);
		double realY = rotateY(relativeX, relativeY, realTargetHeading);

        odometerDistance = lastVisionDistance-realY;
		return realTargetHeading + (Math.toDegrees(Math.atan2(realX,(lastVisionDistance-realY)))-(changeInRotation));
        
	}

	public double rotateX (double xValue, double yValue, double angleInDegrees){
		return (xValue * Math.cos(Math.toRadians(angleInDegrees))) - (yValue * Math.sin(Math.toRadians(angleInDegrees)));
	}	

	public double rotateY (double xValue, double yValue, double angleInDegrees){
		return (xValue * Math.sin(Math.toRadians(angleInDegrees))) + (yValue * Math.cos(Math.toRadians(angleInDegrees)));
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