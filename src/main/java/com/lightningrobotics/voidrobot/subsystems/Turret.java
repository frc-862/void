// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.lightningrobotics.voidrobot.Constants;


public class Turret extends SubsystemBase {
  private CANSparkMax twistMotor; // TODO: change VictorSPX to correct motor controller

  private RelativeEncoder twistMotorEncoder;

  private double target;

  private boolean isDone = false;

  public Turret() {
    twistMotor = new CANSparkMax(Constants.TURRET_MOTOR_ID, MotorType.kBrushless); // TODO: change CAN ids for both motors

    twistMotor.setIdleMode(CANSparkMax.IdleMode.kCoast);

    twistMotor.setClosedLoopRampRate(.02);

    twistMotorEncoder = twistMotor.getEncoder();
  }

  public void setTarget(double degrees) {
    target = turretRevToDeg() + degrees;
  }

  public void twistTurret(double degrees) { // -135 -> 135     // TODO: find gear ratio and then multiple getPosition() by 360 * gear ratio
    double error = target-turretRevToDeg();

    // if (Math.abs(error) < 1) {
    //   isDone = true;
    // }

    twistMotor.set(Constants.TURRET_kP*degrees);    
  }
  
  public void stopTurret() {
    twistMotor.set(0);
  }

  public double turretRevToDeg() {
    return twistMotorEncoder.getPosition() * 360 * Constants.TURRET_GEAR_RATIO;
  }

  public boolean isDone() {
    return isDone;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
