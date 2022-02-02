// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.subsystems;

import com.lightningrobotics.voidrobot.Constants;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Turret extends SubsystemBase {

  private CANSparkMax twistMotor; 

  private RelativeEncoder twistMotorEncoder;

  private double target;

  private boolean isDone = false;

  public Turret() {
    twistMotor = new CANSparkMax(Constants.TURN_TURRET_ID, MotorType.kBrushless); // TODO: change CAN ids for both motors

    twistMotor.setIdleMode(CANSparkMax.IdleMode.kCoast);

    twistMotor.setClosedLoopRampRate(.02);

    twistMotorEncoder = twistMotor.getEncoder();
  }

  public void setTarget(double degrees) {
    target = turretRevToDeg() + degrees;
  }

  public void twistTurret(double turretTarget, double kP) { // -135 -> 135

    if(turretTarget > 180) {
      turretTarget -= 360;
    }
    if(turretTarget < -180) {
      turretTarget += 360;
    } 
    if(turretTarget >= 135) {
      turretTarget = 135;
    }
    if(turretTarget <= -135) {
      turretTarget = -135;
    }

    double error = turretTarget-turretRevToDeg();

    // if (Math.abs(error) < 1) {
    //   isDone = true;  //TODO: fix later
    // }

    double motorPower = kP*error; 

    if(motorPower > 1) {
      motorPower = 1;
    } else if (motorPower <-1) {
      motorPower = -1;
    } //TODO: implement lightning's version of this


    twistMotor.set(motorPower);    
  }

  
  public void stopTurret() {
    twistMotor.set(0);
  }

  public double turretRevToDeg() {
    return twistMotorEncoder.getPosition() * 360 / Constants.TURN_TURRET_GEAR_RATIO;
  }
  
  public double getEncoderValue() {
    return twistMotorEncoder.getPosition();
  }

  public boolean isDone() {
    return isDone;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}