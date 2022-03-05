// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.turret;

import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ZeroTurret extends CommandBase {
  Turret turret;

  DigitalInput limitSwitch;

  private boolean stopped = false;

  /** Creates a new ZeroTurret. */
  public ZeroTurret(Turret turret) {
    this.turret = turret;
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    turret.setPower(0.2);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (turret.getLeftLimitSwitch()) {
      turret.setPower(-0.2);
    }

    if (turret.getCenterSensor() || turret.getRightLimitSwitch()) { // stop if it bypasses center sensor
      turret.resetEncoder();
      turret.stop();
      stopped = true;
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return stopped;
  }
}
