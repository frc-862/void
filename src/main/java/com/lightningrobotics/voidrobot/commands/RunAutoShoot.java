// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class RunAutoShoot extends CommandBase {
  /** Creates a new RunAutoShoot. */
  private Shooter shooter;
  private Indexer indexer;
  private Turret turret;
  private Vision vision;
  private Drivetrain drivetrain;

  private double shootingStartTime; 
  private double shootTime = 0.5d; // Time after we hit the top beam break before we end command

  public RunAutoShoot(Shooter shooter, Indexer indexer, Turret turret) {
    this.shooter = shooter;
    this.indexer = indexer;
    this.turret = turret;

    addRequirements(shooter, indexer, turret);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    // \/--- Commented this out for now to avoid errors TODO figure out what to do with this
    //(new ParallelCommandGroup(new AimTurret(turret, vision, drivetrain),  new RunShooter(shooter, 3000))).schedule();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if(indexer.getBallCount() > 0 && shooter.getArmed() && turret.getArmed()) {
      // TODO add the commands that we want here
      indexer.setPower(0.5); // Temporary
    }

    if(indexer.getBeamBreakExitStatus()){
      shootingStartTime = Timer.getFPGATimestamp();
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    shooter.stop();
    indexer.stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return Timer.getFPGATimestamp() - shootingStartTime > shootTime;
  }
}
