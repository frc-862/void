package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ShootCargo extends CommandBase {

	private final Shooter shooter;
	private final Hood hood;
	private final Indexer indexer;
	private final Turret turret;
	private final HubTargeting targeting;

	public ShootCargo(Shooter shooter, Hood hood, Indexer indexer, Turret turret, HubTargeting targeting) {
		this.shooter = shooter;
		this.hood = hood;
		this.indexer = indexer;
		this.turret = turret;
		this.targeting = targeting;

		addRequirements(shooter, hood, indexer);
	}

	@Override
	public void execute() {
		
		var rpm = targeting.getTargetFlywheelRPM();
		var hoodAngle = targeting.getTargetHoodAngle();

		shooter.setRPM(rpm);
		hood.setAngle(hoodAngle);
			
		if(shooter.onTarget() && turret.onTarget() && hood.onTarget()) {
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
