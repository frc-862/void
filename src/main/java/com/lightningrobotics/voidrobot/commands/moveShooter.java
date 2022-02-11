// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.Constants;
import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class MoveShooter extends CommandBase {
  Shooter shooter;
  private ShuffleboardTab shooterTab = Shuffleboard.getTab("shooter test");
  private NetworkTableEntry shooterVelocity;
  private NetworkTableEntry shooterTarget;
  private NetworkTableEntry shooterPower;
  private NetworkTableEntry shooterkP;
  private NetworkTableEntry shooterkD;
  /** Creates a new moveShooter. */
  public MoveShooter(Shooter shooter) {
    this.shooter = shooter;
    addRequirements(shooter);

    shooterVelocity = shooterTab
    .add("RPMs", 0)
    .getEntry();
    shooterTarget = shooterTab
    .add("current target", 0)
    .getEntry();
    shooterPower = shooterTab
    .add("commanded power", 0)
    .getEntry();
    shooterkP = shooterTab
      .add("kP", Constants.SHOOTER_KP)
      .getEntry();
    shooterkD = shooterTab
      .add("kD", Constants.SHOOTER_KD)
      .getEntry();

  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // shooter.shooterPID(shooterkP.getDouble(0), 2000);

    shooterVelocity.setDouble(shooter.getEncoderRPMs());

    shooter.setPIDGains(shooterkP.getDouble(Constants.SHOOTER_KP), shooterkD.getDouble(Constants.SHOOTER_KD));
    
    //TODO: make this better
    shooterPower.setDouble(shooter.setShooterRPMs(shooterTarget.getDouble(0)));


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
