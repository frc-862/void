package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ShootCargoManual extends CommandBase {

	private final Shooter shooter;
	private final Indexer indexer;
	private final Hood hood;

	public ShootCargoManual(Shooter shooter, Hood hood, Indexer indexer, Turret turret, Vision vision) {
		this.shooter = shooter;
		this.indexer = indexer;
		this.hood = hood;

		addRequirements(shooter, indexer); // not adding vision or turret as it is read only

	}

	@Override
	public void execute() {
		shooter.setRPM(Constants.SHOOT_TARMAC_RPM);	
		hood.setAngle(Constants.SHOOT_TARMAC_ANGLE);
		
		if(shooter.onTarget() && hood.onTarget()){
			indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
		}
	}

	@Override
	public void end(boolean interrupted) {
		shooter.coast();
		indexer.stop();

		if (indexer.getBallCount() == 0) {
			hood.setAngle(0);
		} else {
			hood.setPower(0);
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
}
