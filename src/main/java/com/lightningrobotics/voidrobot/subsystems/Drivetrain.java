package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.lightningrobotics.common.controller.PIDFController;
import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.common.subsystem.drivetrain.differential.DifferentialDrivetrain;
import com.lightningrobotics.common.subsystem.drivetrain.differential.DifferentialGains;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;

public class Drivetrain extends DifferentialDrivetrain {

    private static final LightningIMU IMU = LightningIMU.navX();

    private static final MotorController[] leftMotors = new MotorController[]{
        new WPI_TalonFX(1),
        new WPI_TalonFX(2),
        new WPI_TalonFX(3)
    };

    private static final MotorController[] rightMotors = new MotorController[]{
        new WPI_TalonFX(4),
        new WPI_TalonFX(5),
        new WPI_TalonFX(6)
    };

    private static final DifferentialGains DIFFERENTIAL_GAINS = new DifferentialGains(
        5d,
        5d,
        0.5583711759,
        new boolean[]{false, true, true},
        new boolean[]{true, false, false} 
    );

    
    public Drivetrain() {
        super(
            DIFFERENTIAL_GAINS, 
            leftMotors,
            rightMotors,
            IMU,
            () -> (((WPI_TalonFX)leftMotors[0]).getSelectedSensorVelocity() * 10d) * (6.16 * Math.PI / (2048d * 15d)),
            () -> (((WPI_TalonFX)rightMotors[0]).getSelectedSensorVelocity() * 10d) * (6.16 * Math.PI / (2048d * 15d)),
            // Temporary
            new PIDFController(2, 0, 0, 0),
            new PIDFController(2, 0, 0, 0),
            new SimpleMotorFeedforward(0.53606, 1.0146, 0.03217)
        );
    }

}