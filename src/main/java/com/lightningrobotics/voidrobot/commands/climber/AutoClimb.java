// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.climber;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Climber;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Climber.pivotTarget;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

public class AutoClimb extends CommandBase {
  Climber climber;
  Drivetrain drivetrain;

  private enum currentRung {
    mid,
    high,
    traversal,
    climbing
  }
  
  public AutoClimb(Climber climber, Drivetrain drivetrain) {
    this.climber = climber;
    this.drivetrain = drivetrain;

    addRequirements(climber);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    new ParallelCommandGroup(
      new InstantCommand(() -> climber.setPivotTarget(pivotTarget.reach)),
      new InstantCommand(() -> climber.setWinchTarget(Constants.REACH_HEIGHT))
    );

    //check if at target

    new ParallelCommandGroup(
      new InstantCommand(() -> climber.setPivotTarget(pivotTarget.hold)),
      new InstantCommand(() -> climber.setWinchTarget(Constants.HOLD_HEIGHT))
    );

    //then repeat
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
