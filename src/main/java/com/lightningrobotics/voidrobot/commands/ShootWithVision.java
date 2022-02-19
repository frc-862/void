// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ShootWithVision extends CommandBase {
  /** Creates a new shootWithVision. */

  Shooter shooter;
  Vision vision;

  double time;

  public ShootWithVision(Shooter shooter, Vision vision) {
    this.shooter = shooter;
    this.vision = vision;

    addRequirements(shooter, vision);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    time = Timer.getFPGATimestamp();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    shooter.setRPM(shooter.getRPMsFromHeight(vision.getTargetHeight()));
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    shooter.stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return ((Timer.getFPGATimestamp() - time) > 360);
  }
}
