package com.lightningrobotics.voidrobot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;



public class CloseWallCannedShot extends CommandBase {

  private final Shooter shooter;
	private final Hood hood;
	private final Indexer indexer;
  private final Turret turret;
  private final HubTargeting targeting;

  public CloseWallCannedShot(Shooter shooter, Hood hood, Indexer indexer, HubTargeting targeting, Turret turret) {
    this.shooter = shooter;
		this.hood = hood;
		this.indexer = indexer;
    this.turret = turret;
    this.targeting = targeting;

		addRequirements(shooter, hood, indexer);
  }

  @Override
  public void initialize() {}

  @Override
  public void execute() {
      turret.setAngle(Constants.CLOSE_CANNED_SHOT_TURRET_POSITION); // set Turret to 0
      hood.setAngle(Constants.CLOSE_CANNED_SHOT_HOOD_ANGLE); // set Hood to 0
      shooter.setRPM(Constants.CLOSE_CANNED_SHOT_FLYWHEEL_SPEED); // set RPM to 3650.0

      if(targeting.onTarget(Constants.CLOSE_CANNED_SHOT_FLYWHEEL_SPEED, Constants.CLOSE_CANNED_SHOT_TURRET_POSITION, Constants.CLOSE_CANNED_SHOT_HOOD_ANGLE)){
        indexer.setPower(Constants.DEFAULT_INDEXER_POWER); // Turn on Indexer to shoot   
      }
  }

  @Override
  public void end(boolean interrupted) {
    shooter.coast();
    indexer.setPower(0);
    hood.setAngle(0);
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
