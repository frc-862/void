package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.lightningrobotics.common.controller.FeedForwardController;
import com.lightningrobotics.common.controller.PIDFController;
import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.common.subsystem.drivetrain.differential.DifferentialDrivetrain;
import com.lightningrobotics.common.subsystem.drivetrain.differential.DifferentialGains;
import com.lightningrobotics.common.util.LightningMath;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;

public class Drivetrain extends DifferentialDrivetrain {

    private static final MotorController[] LEFT_MOTORS = new MotorController[]{
        new WPI_TalonFX(4),
        new WPI_TalonFX(5),
        new WPI_TalonFX(6)
    };

    private static final MotorController[] RIGHT_MOTORS = new MotorController[]{
        new WPI_TalonFX(1),
        new WPI_TalonFX(2),
        new WPI_TalonFX(3)
    };

    private static final DifferentialGains DIFFERENTIAL_GAINS = new DifferentialGains(
        1d,
        1d,
        0.5583711759,
        new boolean[]{false, false, false},
        new boolean[]{true, true ,true},
        new PIDFController(0.0038793, 0, 0),
        new FeedForwardController(0.53397, 3.2953, 0.17849)
    );

    
    public Drivetrain() {
        super(
            DIFFERENTIAL_GAINS, 
            LEFT_MOTORS, 
            RIGHT_MOTORS, 
            LightningIMU.navX(), 
            // This finction only deals wit ticks to a distance, so the *10 handles the per second operation needed
            () -> LightningMath.ticksToDistance( (((WPI_TalonFX)LEFT_MOTORS[0]).getSelectedSensorVelocity() * 10d), Units.inchesToMeters(6.16), 15d, 2048d ), 
            () -> LightningMath.ticksToDistance( (((WPI_TalonFX)RIGHT_MOTORS[0]).getSelectedSensorVelocity() * 10d), Units.inchesToMeters(6.16), 15d, 2048d ),
            () -> LightningMath.ticksToDistance( (((WPI_TalonFX)LEFT_MOTORS[0]).getSelectedSensorPosition()), Units.inchesToMeters(6.16), 15d, 2048d),
            () -> LightningMath.ticksToDistance( (((WPI_TalonFX)RIGHT_MOTORS[0]).getSelectedSensorPosition()), Units.inchesToMeters(6.16), 15d, 2048d)
        );
    }

}