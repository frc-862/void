// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.Constants;
import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ManualShooting extends CommandBase {
  Shooter shooter;

  ShuffleboardTab shooterTab = Shuffleboard.getTab("shooterTab");
  NetworkTableEntry targetRPMs;

  private double shootingStartTime; 
  private double shootTime = 0.5d; // Time after we hit the top beam break before we end command

  /** Creates a new ManualShooting. */
  public ManualShooting(Shooter shooter) {
    this.shooter = shooter;

    addRequirements(shooter);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    targetRPMs = shooterTab
    .add("target RPMs", 0)
    .getEntry();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    shooter.setRPM(targetRPMs.getDouble(0));
    // shooter.setRPM(Constants.SHOOTER_CLOSE_RPMS);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return Timer.getFPGATimestamp() - shootingStartTime > shootTime;
  }
}
