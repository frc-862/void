package com.lightningrobotics.voidrobot.commands.auto.commands;

import com.lightningrobotics.voidrobot.commands.turret.AimTurret;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutonShootCargo extends CommandBase {

	private Shooter shooter;
	private Hood hood;
	private Indexer indexer;
	private Turret turret;

	private double rpm;
	private double hoodAngle;
	private double turretAngle;
	

	public AutonShootCargo(Shooter shooter, Hood hood, Indexer indexer, Turret turret, double rpm, double hoodAngle, double turretAngle) {
		this.shooter = shooter;
		this.indexer = indexer;
		this.turret = turret;
		this.hood = hood;
		this.rpm = rpm;
		this.hoodAngle = hoodAngle;
		this.turretAngle = turretAngle;

		addRequirements(shooter, hood, indexer, turret); // not adding vision or turret as it is read only

	}

	@Override
	public void execute() {

		shooter.setRPM(rpm);
		hood.setAngle(hoodAngle);
		turret.setAngle(turretAngle);

		if (shooter.onTarget() && turret.onTarget() && hood.onTarget()) {
			indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
		}
	}

	@Override
	public void end(boolean interrupted) {
		turret.setAngle(50);
		indexer.stop();;
		shooter.coast();
	}

	@Override
	public boolean isFinished() {
		return indexer.getEjectedBall();
	}
	
}