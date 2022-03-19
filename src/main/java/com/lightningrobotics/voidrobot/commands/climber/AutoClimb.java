// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.climber;

import com.lightningrobotics.voidrobot.subsystems.Climber;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutoClimb extends CommandBase {
  Climber climber;
  Drivetrain drivetrain;

  
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
    //setpoint to almost climb
    //pivot back a bit
    //climb rest of the way
    //pivot forward 
    //climb down a bit (to engage passive)
    //pivot back all the way
    //extend winch
    //pivot forward until engaged
    //climb 
    //wait till settled

    //rinse and repeat

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
