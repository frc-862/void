// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.climber;

import com.lightningrobotics.voidrobot.commands.climber.pivot.PivotToReach;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.ClimbArms;
import com.lightningrobotics.voidrobot.subsystems.ClimbPivots;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;

public class BackHooks extends SequentialCommandGroup {
  ClimbArms arms;
  ClimbPivots pivots;
  public BackHooks(ClimbArms arms, ClimbPivots pivots) {
    super(
      new InstantCommand(() -> arms.setTarget(Constants.REACH_HEIGHT)),
      new WaitUntilCommand(() -> (arms.getRightEncoder() + arms.getleftEncoder()) / 2 >= Constants.REACH_HEIGHT * 0.75),
      new PivotToReach(pivots).withTimeout(1.25)
    );
    this.arms = arms;
    this.pivots = pivots; 

    addRequirements(arms, pivots);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    arms.stop();
    pivots.stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
