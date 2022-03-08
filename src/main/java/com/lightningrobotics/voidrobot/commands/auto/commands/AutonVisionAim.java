// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.auto.commands;


import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutonVisionAim extends CommandBase {

	private final Vision vision;
	private final Turret turret;

	private double targetAngle;
	private double motorOutput;

	public AutonVisionAim(Vision vision, Turret turret) {
		this.vision = vision;
		this.turret = turret;

		addRequirements(vision, turret);
	}

	@Override
	public void initialize() {}

	@Override
	public void execute() {
		targetAngle = turret.getCurrentAngle().getDegrees() + vision.getOffsetAngle();

		turret.setTarget(targetAngle);

		motorOutput = turret.getMotorOutput(turret.getTarget());
		turret.setPower(motorOutput);
		
	}

	@Override
	public void end(boolean interrupted) {}

	@Override
	public boolean isFinished() {
		return false;
	}
}
