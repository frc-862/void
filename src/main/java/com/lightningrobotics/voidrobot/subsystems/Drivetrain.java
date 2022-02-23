package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.lightningrobotics.common.controller.FeedForwardController;
import com.lightningrobotics.common.controller.PIDFController;
import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.common.subsystem.drivetrain.differential.DifferentialDrivetrain;
import com.lightningrobotics.common.subsystem.drivetrain.differential.DifferentialGains;
import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import com.lightningrobotics.voidrobot.constants.Constants;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;

public class Drivetrain extends DifferentialDrivetrain {

    private static final MotorController[] LEFT_MOTORS = new MotorController[]{
        new WPI_TalonFX(RobotMap.LEFT_MOTOR_1),
        new WPI_TalonFX(RobotMap.LEFT_MOTOR_2),
        new WPI_TalonFX(RobotMap.LEFT_MOTOR_3)
    };

    private static final MotorController[] RIGHT_MOTORS = new MotorController[]{
        new WPI_TalonFX(RobotMap.RIGHT_MOTOR_1),
        new WPI_TalonFX(RobotMap.RIGHT_MOTOR_2),
        new WPI_TalonFX(RobotMap.RIGHT_MOTOR_3)
    };

    private static final DifferentialGains DIFFERENTIAL_GAINS = new DifferentialGains(
        Constants.MAX_SPEED,
        Constants.MAX_ACCELERATION,
        Constants.TRACK_WIDTH,
        new boolean[]{Constants.LEFT_1_INVERT, Constants.LEFT_2_INVERT, Constants.LEFT_3_INVERT},
        new boolean[]{Constants.RIGHT_1_INVERT, Constants.RIGHT_2_INVERT, Constants.RIGHT_3_INVERT},
        new PIDFController(Constants.KP, Constants.KI, Constants.KD), // TODO: to be tuned 
        new FeedForwardController(Constants.KS, Constants.KV, Constants.KA) // TODO: to be tuned 
    );

    
    public Drivetrain() {
        super(
            DIFFERENTIAL_GAINS, 
            LEFT_MOTORS, 
            RIGHT_MOTORS, 
            LightningIMU.navX(), 
            // This finction only deals wit ticks to a distance, so the *10 handles the per second operation needed
            () -> LightningMath.ticksToDistance( (((WPI_TalonFX)LEFT_MOTORS[0]).getSelectedSensorVelocity() * 10d), Units.inchesToMeters(4.0725), 6.7d, 2048d ), 
            () -> LightningMath.ticksToDistance( (((WPI_TalonFX)RIGHT_MOTORS[0]).getSelectedSensorVelocity() * 10d), Units.inchesToMeters(4.0725), 6.7d, 2048d ),
            () -> LightningMath.ticksToDistance( (((WPI_TalonFX)LEFT_MOTORS[0]).getSelectedSensorPosition()), Units.inchesToMeters(4.0725), 6.7d, 2048d),
            () -> LightningMath.ticksToDistance( (((WPI_TalonFX)RIGHT_MOTORS[0]).getSelectedSensorPosition()), Units.inchesToMeters(4.0725), 6.7d, 2048d)
        );

        for (int i = 0; i < RIGHT_MOTORS.length; i++){
            ((WPI_TalonFX)RIGHT_MOTORS[i]).setNeutralMode(NeutralMode.Brake);
            ((WPI_TalonFX)LEFT_MOTORS[i]).setNeutralMode(NeutralMode.Brake);
        }

    }

}