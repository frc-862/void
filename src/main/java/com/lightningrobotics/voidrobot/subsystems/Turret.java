// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.subsystems;

import java.util.Map;
import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.Constants;
import com.lightningrobotics.voidrobot.commands.TurnTurret;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Turret extends SubsystemBase {

  private double turretkP = 0.035;

  private CANSparkMax twistMotor; 

  private RelativeEncoder twistMotorEncoder;

  private double target;

  private boolean isDone = false;

  DoubleSupplier joystickXInput;

  private double joystickGain = 100;


  private ShuffleboardTab turretTab = Shuffleboard.getTab("turret");

  private NetworkTableEntry targetEntry;

  private NetworkTableEntry targetDegrees;
  private NetworkTableEntry targetAngel;
  private NetworkTableEntry Error;
  private NetworkTableEntry currentDegrees;

  private final double DEFAULT_TARGET = 0;

  private static double turretTarget = 0; //IDK where i should be creating this variable but I htink this is right

  public Turret(DoubleSupplier joystickXInput) {
    twistMotor = new CANSparkMax(Constants.TURN_TURRET_ID, MotorType.kBrushless); // TODO: change CAN ids for both motors

    twistMotor.setIdleMode(CANSparkMax.IdleMode.kCoast);

    twistMotor.setClosedLoopRampRate(.02);

    twistMotorEncoder = twistMotor.getEncoder();

    this.joystickXInput = joystickXInput;

  currentDegrees = turretTab
    .add("current turret degrees", turretRevToDeg())
    .getEntry();
     
    targetDegrees = turretTab
    .add("current turret target", 0)
    .getEntry();

    Error = turretTab
    .add("current Error", 0)
    .getEntry();

    targetAngel = turretTab
    .add("current turret Angel", 0)
    .getEntry();

  }

  public void setTarget(double degrees) {
    target = turretRevToDeg() + degrees;
  }

  public void twistTurret(double targetAngle) { // -135 -> 135
    if (Math.abs(targetAngle) < 0.5) {
      targetAngle = 0;
    }
    turretTarget += targetAngle;
    double error = 0;

    if(turretTarget > 180) {
      turretTarget -= 360;
    }
    if(turretTarget < -180) {
      turretTarget += 360;
    } 

    error = turretTarget - turretRevToDeg();

    if(turretTarget >= 135) {
      error = 135 - turretRevToDeg();
    }
    if(turretTarget <= -135) {
      error = -135 - turretRevToDeg();
    }

    targetDegrees.setDouble(turretTarget);
    targetAngel.setDouble(targetAngle);
    Error.setDouble(error);

    // if (Math.abs(error) < 1) {
    //   isDone = true;  //TODO: fix later
    // }

  
    double motorPower = turretkP*error; 

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
    twistTurret(joystickXInput.getAsDouble()*3);
    currentDegrees.setDouble(turretRevToDeg());
  }
}