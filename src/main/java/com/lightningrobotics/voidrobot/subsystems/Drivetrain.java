package com.lightningrobotics.voidrobot.subsystems;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.common.subsystem.drivetrain.differential.DifferentialDrivetrain;
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

	// This function only deals wit ticks to a distance, so the *10 handles the per second operation needed
	private static final DoubleSupplier leftVelocitySupplier = 
		() -> LightningMath.ticksToDistance((((WPI_TalonFX)LEFT_MOTORS[0]).getSelectedSensorVelocity() * 10d), Units.inchesToMeters(Constants.WHEEL_DIAMETER), Constants.GEAR_REDUCTION, Constants.TICKS_PER_REV_FALCON);
	private static final DoubleSupplier rightVelocitySupplier = 
		() -> LightningMath.ticksToDistance((((WPI_TalonFX)RIGHT_MOTORS[0]).getSelectedSensorVelocity() * 10d), Units.inchesToMeters(Constants.WHEEL_DIAMETER), Constants.GEAR_REDUCTION, Constants.TICKS_PER_REV_FALCON);
	private static final DoubleSupplier leftPositionSupplier =  
		() -> LightningMath.ticksToDistance((((WPI_TalonFX)LEFT_MOTORS[0]).getSelectedSensorPosition()), Units.inchesToMeters(Constants.WHEEL_DIAMETER), Constants.GEAR_REDUCTION, Constants.TICKS_PER_REV_FALCON);
	private static final DoubleSupplier rightPositionSupplier = 
		() -> LightningMath.ticksToDistance((((WPI_TalonFX)RIGHT_MOTORS[0]).getSelectedSensorPosition()), Units.inchesToMeters(Constants.WHEEL_DIAMETER), Constants.GEAR_REDUCTION, Constants.TICKS_PER_REV_FALCON);
    
    public Drivetrain(LightningIMU imu) {
        super(
            Constants.DIFFERENTIAL_GAINS, 
            LEFT_MOTORS, 
            RIGHT_MOTORS, 
            imu, 
            leftVelocitySupplier,
			rightVelocitySupplier,
			leftPositionSupplier, 
			rightPositionSupplier
        );

        for (int i = 0; i < RIGHT_MOTORS.length; i++){
            ((WPI_TalonFX)RIGHT_MOTORS[i]).setNeutralMode(NeutralMode.Brake);
            ((WPI_TalonFX)LEFT_MOTORS[i]).setNeutralMode(NeutralMode.Brake);
        }

    }

    public boolean isMoving() {
        return ((WPI_TalonFX)LEFT_MOTORS[0]).getSelectedSensorVelocity() < 0.05 && ((WPI_TalonFX)RIGHT_MOTORS[0]).getSelectedSensorVelocity() < 0.05;
    }

    @Override
    public void periodic() {
        super.periodic();
    }
}
