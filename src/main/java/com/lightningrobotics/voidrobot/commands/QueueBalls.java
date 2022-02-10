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

  private int ball1Color;
  private int ball2Color;
  private boolean ball1Gone;
  private boolean ball2Gone;

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
      indexer.setPower(0.5);
    if(indexer.getBallCount() > previousBallCount){
      isRunning = true;
      startIndexTime = Timer.getFPGATimestamp();
      previousBallCount = indexer.getBallCount();
    }

    if(Timer.getFPGATimestamp() - startIndexTime <= indexTime && isRunning){
        //indexer.setPower(0.5);
      }
    else{
        //indexer.setPower(0);
        isRunning = false;
        previousBallCount = indexer.getBallCount();
    }
    
    // if(indexer.getBallCount() == 1 && indexer.getColorSensorOutputs() == 1) {
    //     SmartDashboard.putString("ball 1", "red");
    //     ball1Color = 1;
    // } else if(indexer.getBallCount() == 1 && indexer.getColorSensorOutputs() == 2){
    //     SmartDashboard.putString("ball 1", "blue");
    //     ball1Color = 2;
    // } 
    // if(indexer.getBallCount() == 2 && indexer.getColorSensorOutputs() == 1) {
    //     SmartDashboard.putString("ball 2", "red");
    //     ball2Color = 1;
    // } else if(indexer.getBallCount() == 2 && indexer.getColorSensorOutputs() == 2){
    //     SmartDashboard.putString("ball 2", "blue");
    //     ball2Color = 2;
    // }

    // returns 1 if red, 2 if blue.
    if(indexer.getBallCount() == 1 && indexer.getColorSensorOutputs() == 1) {
        ball1Color = 1;
    } else if(indexer.getBallCount() == 1 && indexer.getColorSensorOutputs() == 2){
        ball1Color = 2;
    } 

    // if there isn't any ball, reset ball1
    if(indexer.getBallCount() == 0) {
        ball1Color = 0;
    } 

    if(indexer.getBallCount() == 2 && indexer.getColorSensorOutputs() == 1) {
        ball2Color = 1;
    } else if(indexer.getBallCount() == 2 && indexer.getColorSensorOutputs() == 2){
        ball2Color = 2;
    }
    
    // if there aren't 2 balls, reset ball2
    if(indexer.getBallCount() == 1 || indexer.getBallCount() == 0) {
        ball2Color = 0;
    }

    // if one ball is ejected, make the 2nd ball 1st
    if(previousBallCount - indexer.getBallCount() >= 1) {
        ball2Color = ball1Color;
    }

    switch(ball1Color) {
        case 0: SmartDashboard.putString("ball 1", "nonexistant");
        break;

        case 1: SmartDashboard.putString("ball 1", "red");
        break;

        case 2: SmartDashboard.putString("ball 1", "blue");
        break;        
    }

    switch(ball2Color) {
        case 0: SmartDashboard.putString("ball 2", "nonexistant");;
        break;
        
        case 1: SmartDashboard.putString("ball 2", "red");
        break;

        case 2: SmartDashboard.putString("ball 2", "blue");
        break;        
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
