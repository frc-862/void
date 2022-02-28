package com.lightningrobotics.voidrobot.commands.turret;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.common.geometry.kinematics.DrivetrainSpeed;
import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AimTurret extends CommandBase {
    
    private boolean manualControl = false;
    private boolean testing = false;

    // Creates our turret and vision subsystems
    private Turret turret;
    private Vision vision;
    private Drivetrain drivetrain;

    private boolean isUsingVision = true;

	private double realHeadingTowardsTarget = 0d;
    private double lastVisionOffset;

    DoubleSupplier stickX;
    DoubleSupplier stickY;

    enum TargetingState{
        MANUAL,
        AUTO_NO_VISION,
        AUTO_VISION,
        TESTING
    }
    TargetingState targetingState;

    /**
	 * Command to aim the turret
	 */ 
    public AimTurret(Turret turret, Vision vision, Drivetrain drivetrain, DoubleSupplier stickX, DoubleSupplier stickY) {
        this.turret = turret;
        this.vision = vision;
        this.drivetrain = drivetrain;
        this.stickX = stickX;
        this.stickY = stickY;
        // Not adding vision since its use is read-only
        addRequirements(turret, drivetrain);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void execute() {
        if((stickX.getAsDouble() > 0.15 || stickY.getAsDouble() > 0.15) && manualControl){
            targetingState = TargetingState.MANUAL; // TODO: how does copilot override auto turning?
        } else if(vision.hasVision()){
            targetingState = TargetingState.AUTO_VISION;
        } else if (testing) {
            //put testing stuff here
        } else{
            targetingState = TargetingState.AUTO_NO_VISION;
        }
        
        switch (targetingState) {
            case MANUAL: 
                //only work if its not in the controller stick deadzone
                turret.setTarget(Math.toDegrees(Math.tan(stickX.getAsDouble()/stickY.getAsDouble())) + 90);
            break;

            case AUTO_NO_VISION: 
                if (isUsingVision){
                    // As soon as we lose vision, we reset drivetrain pose/axis and get current angle degree
                    drivetrain.resetPose();
                    isUsingVision = false;
                    realHeadingTowardsTarget = turret.getTurretAngle().getDegrees();// + lastVisionOffset;
                } 

                // get (x,y) relative to the robot. the X and Y axis is created when we reset the drivetrain odometer
                double relativeX = drivetrain.getPose().getX();
                double relativeY = drivetrain.getPose().getY();

                // Get data from right before vision loss
                double lastVisionDistance = lastVisionOffset;
                double changeInRotation = drivetrain.getPose().getRotation().getDegrees();

                turret.setOffsetNoVision(relativeX, relativeY, realHeadingTowardsTarget, lastVisionDistance, changeInRotation);
            break;

            case AUTO_VISION: 
                lastVisionOffset = vision.getOffsetAngle();
                turret.setVisionOffset(lastVisionOffset); // setting the target angle of the turret
                isUsingVision = true;
            break;
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