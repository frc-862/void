// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class ToggleZeroTurretHood extends CommandBase {

	private Shooter shooter;
	private Turret turret;

	public ToggleZeroTurretHood(Shooter shooter, Turret turret) {
		this.shooter = shooter;
		this.turret = turret;
		addRequirements(shooter, turret);
	}

	@Override
	public void initialize() {}

	@Override
	public void execute() {

		var targetAngle = 0;
		turret.setTarget(targetAngle);
		var motorOutput = turret.getMotorOutput(turret.getTarget());

		turret.setPower(motorOutput);
		shooter.setHoodAngle(0);
	}

	@Override
	public void end(boolean interrupted) {
		turret.setPower(0);
		shooter.setHoodPower(0);
	}
}
