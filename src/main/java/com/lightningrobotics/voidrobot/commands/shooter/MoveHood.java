// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.shooter;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class MoveHood extends CommandBase {
  Shooter shooter;
  DoubleSupplier power;

  public MoveHood(Shooter shooter, DoubleSupplier power) {
    this.shooter = shooter;
    this.power = power;

    addRequirements(shooter);
  }

  @Override
  public void initialize() {}

  @Override
  public void execute() {
    shooter.setHoodPower(power.getAsDouble());
  }

  @Override
  public void end(boolean interrupted) {
    shooter.setHoodPower(0);
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
