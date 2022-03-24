package com.lightningrobotics.voidrobot.commands.auto.commands;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutonShootCargo extends CommandBase {

	private Shooter shooter;
	private Hood hood;
	private Indexer indexer;
	private Turret turret;
	private HubTargeting targeting;

	private double rpm;
	private double hoodAngle;
	private double turretAngle;

	public AutonShootCargo(Shooter shooter, Hood hood, Indexer indexer, Turret turret, HubTargeting targeting, double rpm, double hoodAngle, double turretAngle) {
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

		if (targeting.onTarget()) {
			indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
			System.out.println("on target -------------------------------------------");
			
		} else {
			System.out.println("not on target -------------------------------------------");
			System.out.println("target RPM: " + rpm);
			System.out.println("current RPM: " + shooter.getCurrentRPM());
			System.out.println("target Hood: " + hoodAngle);
			System.out.println("Current hood: " + hood.getAngle());
			System.out.println("target Turret: " + turretAngle);
			System.out.println("curent turret: " + turret.getCurrentAngle().getDegrees());
			System.out.println("hood limit switch" + hood.getLimitSwitch());

		}
	}

	@Override
	public void end(boolean interrupted) {
		// indexer.stop();
		// shooter.coast();
	}

	@Override
	public boolean isFinished() {
		return indexer.getEjectedBall();
	}
	
}