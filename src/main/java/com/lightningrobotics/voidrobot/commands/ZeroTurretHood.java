package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.wpilibj2.command.InstantCommand;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class ZeroTurretHood extends InstantCommand {

	private final Hood hood;
	private final Turret turret;

	public ZeroTurretHood(Hood hood, Turret turret) {
		this.hood = hood;
		this.turret = turret;

		addRequirements(hood, turret);
	}

	@Override
	public void initialize() {	
		turret.setAngle(0);
		hood.setAngle(0);
	}
	
}
