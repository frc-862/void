// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.lightningrobotics.common.subsystem.drivetrain.differential.DifferentialDrivetrain;
import com.lightningrobotics.common.subsystem.drivetrain.differential.DifferentialGains;
import com.lightningrobotics.voidrobot.Constants;

    public class Drivetrain extends DifferentialDrivetrain {
    /** Creates a new Drivetrain. */

    private static final DifferentialGains DIFFERENTIAL_GAINS = new DifferentialGains( // TODO take a look at these gains for quasar later
        5d,
        5d,
        0.5583711759,
        new boolean[]{true, false, false},
        new boolean[]{false, true, true} 
    );

    
    public Drivetrain() {
        super(
            DIFFERENTIAL_GAINS, 
            new MotorController[]{
                new WPI_TalonFX(Constants.left_1_CAN_ID),
                new WPI_TalonFX(Constants.left_2_CAN_ID),
                new WPI_TalonFX(Constants.left_3_CAN_ID)
            }, 
            new MotorController[]{
                new WPI_TalonFX(Constants.right_1_CAN_ID),
                new WPI_TalonFX(Constants.right_2_CAN_ID),
                new WPI_TalonFX(Constants.right_3_CAN_ID),
            }
        );
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
    }
}
