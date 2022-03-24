package com.lightningrobotics.voidrobot.commands.auto.commands;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutonVisionShooting extends CommandBase {

	private final Shooter shooter;
	private final Hood hood;
	private final Indexer indexer;
	private final Turret turret;
	private final HubTargeting targeting;

	private double visionOffset;
	private double distanceOffset;

	private boolean shooting = false;

	public AutonVisionShooting(Shooter shooter, Hood hood, Indexer indexer, Turret turret, HubTargeting targeting, double visionOffset, double distanceOffset) {
		this.shooter = shooter;
		this.indexer = indexer;
		this.turret = turret;
		this.hood = hood;
		this.targeting = targeting;
		this.visionOffset = visionOffset;
		this.distanceOffset = distanceOffset;

		addRequirements(shooter, hood, indexer, turret);

	}

	@Override
	public void initialize() {	
		targeting.zeroBias();
		targeting.adjustBiasDistance(distanceOffset);
		targeting.adjustBiasAngle(visionOffset);
	}
	
	@Override
	public void execute() {
		var rpm = targeting.getTargetFlywheelRPM();
		var hoodAngle = targeting.getTargetHoodAngle();
		var turretAngle = targeting.getTargetTurretAngle();

		shooter.setRPM(rpm);
		hood.setAngle(hoodAngle);
		turret.setAngle(turretAngle);

		if ((shooter.onTarget() && hood.onTarget() && turret.onTarget()) || shooting) {
			indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
			shooting = true;
		}
		else if (indexer.getColorSensor().getProximity() < 350) {
			indexer.setPower(0.5d);
		} else {
			indexer.stop();
		}

	}

	@Override
	public void end(boolean interrupted) {
		// shooter.coast();
		// indexer.stop();
		// hood.stop();
		// turret.stop();
		shooting = false;
		targeting.zeroBias();
		
	}

	@Override
	public boolean isFinished() {
		return indexer.getEjectedBall();
	}
	
}