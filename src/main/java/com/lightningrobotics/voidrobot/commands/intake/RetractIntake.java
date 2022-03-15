package com.lightningrobotics.voidrobot.commands.intake;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Intake;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class RetractIntake extends CommandBase {
  Intake intake;
  double startTime;

  /** Creates a new DeployIntake. */
  public RetractIntake(Intake intake) {
    this.intake = intake;
    
    addRequirements(intake);
  }

  // Called when the command is initially scheduled.
  @Override 
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    intake.actuateIntake(-Constants.DEFAULT_WINCH_POWER);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    intake.stopDeploy();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}