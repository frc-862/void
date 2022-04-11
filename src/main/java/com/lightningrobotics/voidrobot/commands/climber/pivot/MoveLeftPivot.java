// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.climber.pivot;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.ClimbArms;
import com.lightningrobotics.voidrobot.subsystems.ClimbPivots;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class MoveLeftPivot extends CommandBase {
  DoubleSupplier power;
  ClimbPivots pivots;
  public MoveLeftPivot(ClimbPivots pivots, DoubleSupplier power) {
      this.pivots = pivots;
      this.power = power;
      addRequirements(pivots);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    pivots.setLeftPower(power.getAsDouble());
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    pivots.setLeftPower(0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
