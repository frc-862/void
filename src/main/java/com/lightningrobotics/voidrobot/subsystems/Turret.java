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
  private double turretkP = Constants.TURRET_kP;

  private CANSparkMax twistMotor; 

  private RelativeEncoder twistMotorEncoder;

  private double target;

  private boolean isDone = false;

  DoubleSupplier joystickXInput;

  private double joystickGain = 100;

  private ShuffleboardTab turretTab = Shuffleboard.getTab("turret");

  private final double DEFAULT_TARGET = 0;

  private NetworkTableEntry targetDegrees = turretTab
  .add("current turret target", 0)
  .getEntry();;
  private NetworkTableEntry currentTargetAngel = turretTab
  .add("current target Angel", 0)
  .getEntry();;
  private NetworkTableEntry currentDegrees = turretTab
  .add("current turret degrees", turretRevToDeg())
  .getEntry();;
  private NetworkTableEntry targetEntry = turretTab
  .add("target", DEFAULT_TARGET)
  .withWidget(BuiltInWidgets.kNumberSlider)
  .withProperties(Map.of("min", -360, "max", 360)) // specify widget properties here
  .getEntry();
  private NetworkTableEntry kPEntry = turretTab
  .add("kP", Constants.TURRET_kP)
  .getEntry();

    private static double turretTarget = 0;


  public Turret(DoubleSupplier joystickXInput) {
    twistMotor = new CANSparkMax(Constants.TURN_TURRET_ID, MotorType.kBrushless); // TODO: change CAN ids for both motors

    twistMotor.setIdleMode(CANSparkMax.IdleMode.kCoast);

    twistMotor.setClosedLoopRampRate(.02);

    twistMotorEncoder = twistMotor.getEncoder();

    this.joystickXInput = joystickXInput;
  }

  public void setTarget(double degrees) {
    target = turretRevToDeg() + degrees;
  }

  public void twistTurret(double error, double target) { // -135 -> 135

    double motorPower = turretkP*error; 
      
    if(motorPower > 1) {
      motorPower = 1;
    } else if (motorPower <-1) {
      motorPower = -1;
    } 

    targetDegrees.setDouble(turretTarget);
    currentTargetAngel.setDouble(target);
      
    
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
    // twistTurret(targetEntry.getDouble(DEFAULT_TARGET));
    turretkP = kPEntry.getDouble(turretkP);
    currentDegrees.setDouble(turretRevToDeg());
  }
}