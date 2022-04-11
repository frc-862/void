package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ShootCargoTarmac extends CommandBase {

	private final Shooter shooter;
	private final Indexer indexer;
	private final Hood hood;
	private final HubTargeting targeting;

	public ShootCargoTarmac(Shooter shooter, Hood hood, Indexer indexer, Turret turret, HubTargeting targeting) {
		this.shooter = shooter;
		this.indexer = indexer;
		this.hood = hood;
		this.targeting = targeting;

		addRequirements(shooter, indexer);

	}

	@Override
	public void execute() {
		shooter.setRPM(Constants.SHOOT_TARMAC_RPM);	
		hood.setAngle(Constants.SHOOT_TARMAC_ANGLE);
		
		if(targeting.onTarget()){
			indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
		}
	}

	@Override
	public void end(boolean interrupted) {
		shooter.coast();
		indexer.stop();
		hood.setAngle(0d);
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
}
