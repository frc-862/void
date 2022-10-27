// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import com.lightningrobotics.voidrobot.subsystems.Intake;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;

public class AutoIntake extends CommandBase {

  private final Intake intake;
  private final Drivetrain drivetrain;

  /** Creates a new autoIntake. */
  public AutoIntake(Intake intake, Drivetrain drivetrain) {
    this.intake = intake;
    this.drivetrain = drivetrain;

    addRequirements(drivetrain, intake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false; // command doesn't return true, idk what built in return statement is for
  }
}
