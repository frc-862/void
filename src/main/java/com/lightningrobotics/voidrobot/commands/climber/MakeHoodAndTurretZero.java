// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.climber;

import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class MakeHoodAndTurretZero extends CommandBase {
  /** Creates a new MakeHoodAndTurretZero. */
  private Turret turret;
  private Shooter shooter;

  private static ShuffleboardTab driverView = Shuffleboard.getTab("Competition");
  private static NetworkTableEntry preventFritzStoopid = driverView.add("unlock hood and turret", false).getEntry();

  public MakeHoodAndTurretZero(Turret turret, Shooter shooter) {
	this.shooter = shooter;
	this.turret = turret;  
	
	addRequirements(shooter);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
	  turret.setManualOverride(true);
	  shooter.setManualHoodOverride(true, 0);
	  turret.setTarget(0);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
	  turret.setManualOverride(false);
	  shooter.setManualHoodOverride(false, 0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return preventFritzStoopid.getBoolean(false);
  }
}
