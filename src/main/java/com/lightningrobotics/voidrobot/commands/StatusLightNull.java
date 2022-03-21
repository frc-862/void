// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands;

import java.sql.Time;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class StatusLightNull extends CommandBase {

  DigitalOutput light = new DigitalOutput(0);

  public StatusLightNull() {
  }

  @Override
  public void initialize() {}

  @Override
  public void execute() {
    double start = Timer.getFPGATimestamp();

    while (Timer.getFPGATimestamp() < (start + 0.5)){
      light.set(true);
    }
    light.set(false);
  }

  @Override
  public void end(boolean interrupted) {
    light.set(true);
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
