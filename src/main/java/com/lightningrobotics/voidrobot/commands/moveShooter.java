// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class moveShooter extends CommandBase {
  Shooter shooter;
  private ShuffleboardTab voltageTab = Shuffleboard.getTab("shooter test");
  private NetworkTableEntry shooterVelocity;
  private NetworkTableEntry shooterPosition;
  private NetworkTableEntry shooterDist;
  /** Creates a new moveShooter. */
  public moveShooter(Shooter shooter) {
    this.shooter = shooter;
    addRequirements(shooter);

    shooterVelocity = voltageTab
    .add("velocity", 0)
    .getEntry();
    shooterPosition = voltageTab
    .add("current position", 0)
    .getEntry();
    shooterDist = voltageTab
      .add("current distance", 0)
      .getEntry();
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    shooter.runShooter(0.4);
    shooterVelocity.setDouble(shooter.getEncoderRPMs());
    shooterPosition.setDouble(shooter.currentEncoderTicks());
    shooterDist.setDouble(shooter.getEncoderDist());
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
