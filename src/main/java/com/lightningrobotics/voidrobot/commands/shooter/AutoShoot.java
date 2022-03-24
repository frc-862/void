// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;
import com.lightningrobotics.voidrobot.subsystems.Indexer.BallColor;
import com.lightningrobotics.common.geometry.kinematics.differential.DifferentialDrivetrainState;
import com.lightningrobotics.common.util.filter.MovingAverageFilter;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutoShoot extends CommandBase {
  Drivetrain drivetrain;
  Vision vision;
  Turret turret;
  Indexer indexer;
  Shooter shooter;
  Hood hood;

  private double rpm;
	private double distance;
	private double hoodAngle;
  private double startTime;

  boolean hasShot = false;

  private MovingAverageFilter maf = new MovingAverageFilter(10);

  public AutoShoot(Drivetrain drivetrain, Vision vision, Turret turret, Indexer indexer, Shooter shooter, Hood hood) {
    this.drivetrain = drivetrain;
    this.vision = vision;
    this.turret = turret;
    this.indexer = indexer;
    this.shooter = shooter;
    this.hood = hood;

    this.addRequirements(shooter, hood);
  }

  @Override
  public void initialize() {
    // toggles boolean on Shuffleboard whether autoshooting is on
    shooter.toggleAutoShootingDisplay();
  }

  @Override
  public void execute() {
    //check if the current ball is not the same color as the alliance
    //TODO: currently always our balls
    boolean isEnenmyBall = false; //!DriverStation.getAlliance().toString().equals(indexer.getUpperBallColor().toString()) && indexer.getUpperBallColor() != BallColor.nothing;
    
    // check if drive speed is slow enough
    DifferentialDrivetrainState drivetrainState = ((DifferentialDrivetrainState)drivetrain.getDriveState());
    boolean isDrivingSlow = 
      drivetrainState.getLeftSpeed() <= Constants.MAXIMUM_LINEAR_SPEED_TO_SHOOT 
      && drivetrainState.getRightSpeed() <= Constants.MAXIMUM_LINEAR_SPEED_TO_SHOOT
      && Math.abs(drivetrainState.getLeftSpeed() - drivetrainState.getRightSpeed()) <= Constants.MAXIMUM_ANGULAR_SPEED_TO_SHOOT;
    
    // if we have our own ball and vision sees things, move flywheel and hood
    if(!isEnenmyBall && vision.hasVision()){
      distance = vision.getTargetDistance();
      if (distance > 0) {
        distance = maf.filter(distance);
      } else {
        distance = maf.get();
      }

      // If too close then shoot low
      if(distance > 2.0d){
        rpm = Constants.DISTANCE_RPM_MAP.get(distance) + Constants.ANGLE_POWER_MAP.get(distance);
        hoodAngle = Constants.HOOD_ANGLE_MAP.get(distance);
      } else{
        // rpm = Constants.SHOOT_LOW_RPM;
        // hoodAngle = Constants.SHOOT_LOW_ANGLE;
      }

      shooter.setRPM(rpm);
      hood.setAngle(hoodAngle);
    }

    //checks if drivetrain, vision, and turret are OK and sets the RPM and hood angle if they are.
    if(indexer.getBallCount() != 0 
    && isDrivingSlow
    && vision.hasVision() 
    && turret.onTarget()
    && !isEnenmyBall) {

      indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
    }
    // If we have enemy ball, shoot out
    else if(indexer.getBallCount() != 0 && isEnenmyBall){ 
      shooter.setRPM(Constants.EJECT_BALL_RPM);
      hood.setAngle(Constants.EJECT_BALL_HOOD_ANGLE);
      indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
    }

    // Start time when ball hit top sensor
    
    if(indexer.getExitStatusNoDebounce()){
      hasShot = true;
      startTime = Timer.getFPGATimestamp();
    }
    // Stop indexer after a while
    if(Timer.getFPGATimestamp() - startTime > 0.5 && indexer.getBallCount() == 0 && hasShot){
      indexer.setPower(0);
      hood.setAngle(0);
      hasShot = false;
    }

    SmartDashboard.putBoolean("AS Turret Armed", turret.onTarget());
    SmartDashboard.putBoolean("AS Shooter Armed", shooter.onTarget());
    SmartDashboard.putBoolean("AS Hood Armed", hood.onTarget());
    SmartDashboard.putBoolean("AS Vision", vision.hasVision());
    SmartDashboard.putBoolean("AS IS Dirving Slow", isDrivingSlow);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
     // toggles boolean on Shuffleboard whether autoshooting is on
     shooter.toggleAutoShootingDisplay();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
