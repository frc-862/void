package com.lightningrobotics.voidrobot.commands.indexer;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Indexer.BallColor;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutoEjectBadBalls extends CommandBase {
  Indexer indexer;
  Shooter shooter;
  Turret turret;
  Hood hood;

  public AutoEjectBadBalls(Indexer indexer, Shooter shooter, Turret turret, Hood hood) {
    this.indexer = indexer;
    this.shooter = shooter;
    this.turret = turret;
    this.hood = hood;

    addRequirements(indexer);
  }

  
  @Override
  public void initialize() {}

  
  @Override
  public void execute() {
    if(DriverStation.getAlliance().toString() != indexer.getUpperBallColor().toString() && indexer.getUpperBallColor() != BallColor.nothing) { //check if the current ball is not the same color as the alliance
      shooter.setRPM(Constants.EJECT_BALL_RPM);
      hood.setAngle(Constants.EJECT_BALL_HOOD_ANGLE);

      if(shooter.onTarget() && hood.onTarget()) {
        indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
      } else {
        indexer.setPower(0);
      }
    }
  }

  
  @Override
  public void end(boolean interrupted) {
    indexer.setPower(0);
  }

  
  @Override
  public boolean isFinished() {
    return false;
  }
}
