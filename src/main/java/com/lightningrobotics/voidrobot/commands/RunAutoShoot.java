// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class RunAutoShoot extends CommandBase {
  /** Creates a new RunAutoShoot. */
  private Shooter shooter;
  private Indexer indexer;
  public RunAutoShoot(Shooter shooter, Indexer indexer) {
    this.shooter = shooter;
    this.indexer = indexer;

    addRequirements(shooter, indexer);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if(indexer.startCommandSeq()) {
      new SequentialCommandGroup(new QueueBalls(indexer), new RunShooter(shooter, 3000));
    }
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
