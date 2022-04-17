package com.lightningrobotics.voidrobot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;



public class LaunchPadCannedShot extends CommandBase {

  private final Shooter shooter;
	private final Hood hood;
	private final Indexer indexer;
  private final HubTargeting targeting;

  public LaunchPadCannedShot(Shooter shooter, Hood hood, Indexer indexer, HubTargeting targeting) {
    this.shooter = shooter;
		this.hood = hood;
		this.indexer = indexer;
    this.targeting = targeting;

		addRequirements(shooter, hood, indexer);
  }

  @Override
  public void initialize() {}

  @Override
  public void execute() {
      hood.setAngle(Constants.FAR_CANNED_SHOT_HOOD_ANGLE); // set Hood to 0
      shooter.setRPM(Constants.FAR_CANNED_SHOT_FLYWHEEL_SPEED); // set RPM to 3650.0

      if(targeting.onTarget(Constants.FAR_CANNED_SHOT_FLYWHEEL_SPEED, Constants.FAR_CANNED_SHOT_HOOD_ANGLE)){
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
