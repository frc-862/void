package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.wpilibj2.command.InstantCommand;

public class ZeroTurretHood extends InstantCommand {

	private Shooter shooter;
	private Turret turret;

	public ZeroTurretHood(Shooter shooter, Turret turret) {

		this.shooter = shooter;
		this.turret = turret;

		addRequirements(shooter, turret); // not adding vision or turret as it is read onl
	}
	@Override
	public void initialize() {
		turret.setManualOverride(true);
		turret.setTarget(0d);
		shooter.setHoodAngle(0d);
		shooter.stop();
		turret.setManualOverride(false);
	}

}
