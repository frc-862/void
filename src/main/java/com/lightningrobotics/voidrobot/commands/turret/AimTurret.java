package com.lightningrobotics.voidrobot.commands.turret;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.common.geometry.kinematics.DrivetrainSpeed;
import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AimTurret extends CommandBase {
    
    private boolean manualControl = true;
    private boolean testing = false;

    // Creates our turret and vision subsystems
    private Turret turret;
    private Vision vision;
    private Drivetrain drivetrain;

    private boolean isUsingVision = true;

	private double realHeadingTowardsTarget = 0d;
    private double lastVisionOffset;

	private NetworkTableEntry turretAngleEntry;

    DoubleSupplier stickX;
    DoubleSupplier stickY;

    enum TargetingState{
        MANUAL,
        TESTING,
        AUTO_NO_VISION,
        AUTO_VISION
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
		turretAngleEntry = NetworkTableInstance.getDefault().getTable("Turret").getEntry("Turret Angle");
		turretAngleEntry.setDouble(0d);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void execute() {
        //if((stickX.getAsDouble() > 0.5 || stickY.getAsDouble() > 0.5) && manualControl){
		if(manualControl) {
            targetingState = TargetingState.MANUAL; // TODO: how does copilot override auto turning?
        } else if (testing) {
            targetingState = TargetingState.TESTING;
        } else if(vision.hasVision()){
            targetingState = TargetingState.AUTO_VISION;
        } else{
            targetingState = TargetingState.AUTO_NO_VISION;
        }
        
        switch (targetingState) {
            case MANUAL: 
                //just sets the target to wherever the stick on the controller is pointed
                turret.setTarget(Math.toDegrees(turretAngleEntry.getDouble(0d))); //(Math.tan(stickX.getAsDouble()/stickY.getAsDouble())) + 90);
            break;

            case TESTING:
                //put test code here
            break;

            case AUTO_VISION: 
                lastVisionOffset = vision.getOffsetAngle();
                turret.setVisionOffset(lastVisionOffset); // setting the target angle of the turret
                isUsingVision = true;
            break;

            case AUTO_NO_VISION: 
                if (isUsingVision){ //runs once on vision loss
                    // As soon as we lose vision, we reset drivetrain pose/axis and get current angle degree
                    drivetrain.resetPose();
                    realHeadingTowardsTarget = turret.getTurretAngle().getDegrees();// + lastVisionOffset;
                    isUsingVision = false;
                } 

                // get (x,y) relative to the robot. the X and Y axis is created when we reset the drivetrain odometer
                double relativeX = drivetrain.getPose().getX();
                double relativeY = drivetrain.getPose().getY();

                // update rotation data 
                double changeInRotation = drivetrain.getPose().getRotation().getDegrees();

                turret.setOffsetNoVision(relativeX, relativeY, realHeadingTowardsTarget, lastVisionOffset, changeInRotation);
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