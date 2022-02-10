// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.subsystems.Indexer;

import org.opencv.ml.StatModel;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class QueueBalls extends CommandBase {
  /** Creates a new IndexBalls. */
  
  private Indexer indexer;
  private double indexTime = 0.5d; // in seconds
  private double startIndexTime = 0d;
  private boolean isRunning;
  private int previousBallCount;

  public QueueBalls(Indexer indexer) {
    this.indexer = indexer;

    addRequirements(indexer);
  }

  @Override
  public void initialize() {
    previousBallCount = indexer.getBallCount();
  }

  @Override
  public void execute() {
    if(indexer.getBallCount() > previousBallCount){
      isRunning = true;
      startIndexTime = Timer.getFPGATimestamp();
      previousBallCount = indexer.getBallCount();
    }

    if(Timer.getFPGATimestamp() - startIndexTime <= indexTime && isRunning){
        indexer.setPower(0.5);
      }
    else{
        indexer.setPower(0);
        isRunning = false;
        previousBallCount = indexer.getBallCount();
    }
    
    SmartDashboard.putNumber("previous ball count", previousBallCount);

  }

  @Override
  public void end(boolean interrupted) {
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
