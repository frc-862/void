package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ZeroTurretHood extends CommandBase {

	private final Hood hood;
	private final Turret turret;

	public ZeroTurretHood(Hood hood, Turret turret) {
		this.hood = hood;
		this.turret = turret;

		addRequirements(hood, turret);
	}

	@Override
	public void initialize() {}

	@Override
	public void execute() {
		turret.setAngle(0);
		hood.setAngle(0);
	}

	@Override
	public void end(boolean interrupted) {
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	


}
