// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.climber.arms;

import com.lightningrobotics.voidrobot.subsystems.ClimbArms;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ArmsDownLimit extends CommandBase {
  ClimbArms arms;
  public ArmsDownLimit(ClimbArms arms) {
    this.arms = arms;

    addRequirements(arms);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    arms.setPower(-1, -1);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    arms.setPower(-1, -1);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    arms.setPower(0, 0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return arms.getLowerLimitSwitches();
  }
}
