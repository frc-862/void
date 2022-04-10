// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.climber.arms;

import com.lightningrobotics.voidrobot.subsystems.ClimbArms;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class MoveArmsManual extends CommandBase {
  double power;

  ClimbArms arms;
  public MoveArmsManual(ClimbArms arms, double power) {
    this.power = power;
    this.arms = arms;

    addRequirements(arms);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    arms.setPower(power, power);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    arms.stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
