// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.subsystems.Indexer;

import org.opencv.ml.StatModel;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class IndexBalls extends CommandBase {
  /** Creates a new IndexBalls. */
  
  private Indexer indexer;
  private double indexTime = 0.5d; // in seconds
  private double startIndexTime = 0d;
  private boolean isRunning;
  private int previousBallCount;

  public IndexBalls(Indexer indexer) {
    this.indexer = indexer;
    this.startIndexTime = Timer.getFPGATimestamp();
    this.previousBallCount = indexer.getBallCount();

    addRequirements(indexer);
  }

  @Override
  public void initialize() {}

  @Override
  public void execute() {
    if(indexer.getBallCount() > previousBallCount){
      isRunning = true;
    }

    if(Timer.getFPGATimestamp() - startIndexTime <= indexTime && isRunning){
        indexer.setPower(1);
      }
    else{
        indexer.setPower(0);
        isRunning = false;
        previousBallCount = indexer.getBallCount();
    }
  }

  @Override
  public void end(boolean interrupted) {
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
