// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class VoidDrivetrain extends CommandBase {
  Drivetrain drivetrain;
  DoubleSupplier rightJoy;
  DoubleSupplier leftJoy;
  /** Creates a new VoidDrivetrain. */
  public VoidDrivetrain(Drivetrain drivetrain, DoubleSupplier leftJoy, DoubleSupplier rightJoy) {
    this.drivetrain = drivetrain;
    this.leftJoy = leftJoy;
    this.rightJoy = rightJoy;

    addRequirements(drivetrain);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    drivetrain.setPower(leftJoy.getAsDouble(), rightJoy.getAsDouble());
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
