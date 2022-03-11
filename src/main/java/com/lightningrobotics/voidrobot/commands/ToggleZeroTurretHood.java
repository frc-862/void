// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.wpilibj2.command.InstantCommand;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class ToggleZeroTurretHood extends InstantCommand {

	private Shooter shooter;
	private Turret turret;

	public ToggleZeroTurretHood(Shooter shooter, Turret turret) {
		this.shooter = shooter;
		this.turret = turret;
		addRequirements(shooter, turret);
		// Use addRequirements() here to declare subsystem dependencies.
	}

	// Called when the command is initially scheduled.
	@Override
	public void initialize() {
		if (shooter.getToggleZero()) {
			turret.setManualOverride(false);
			// shooter.setManualHoodOverride(false, 0);
			shooter.setToggleZero();
		} else {
			turret.setManualOverride(true);
			// shooter.setManualHoodOverride(true, 0);
			turret.setTarget(0);
			shooter.setHoodAngle(0);
			shooter.setToggleZero();
		}
	}
}
