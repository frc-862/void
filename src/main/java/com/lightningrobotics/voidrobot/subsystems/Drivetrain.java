// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

    public class Drivetrain extends SubsystemBase {
    /** Creates a new Drivetrain. */
    final TalonFX left1 = new TalonFX(0);//TODO check the iddddddsdssdss
    final TalonFX left2 = new TalonFX(0);
    final TalonFX left3 = new TalonFX(0);

    final TalonFX right1 = new TalonFX(0);
    final TalonFX right2 = new TalonFX(0);
    final TalonFX right3 = new TalonFX(0);

    public Drivetrain() {
        left2.follow(left1);
        left3.follow(left1);

        right2.follow(right1);
        right2.follow(right1);
    }

    public void setPower(double left, double right){
        left1.set(ControlMode.PercentOutput, left);
        right1.set(ControlMode.PercentOutput, right);
    }

    public void stop(){
        setPower(0, 0);
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
    }
}
