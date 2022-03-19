package com.lightningrobotics.voidrobot.subsystems;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.lightningrobotics.common.logging.DataLogger;
import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.common.subsystem.drivetrain.differential.DifferentialDrivetrain;
import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import com.lightningrobotics.voidrobot.constants.Constants;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Drivetrain extends DifferentialDrivetrain {

    private LightningIMU imu;

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
		() -> LightningMath.ticksToDistance((((WPI_TalonFX)LEFT_MOTORS[1]).getSelectedSensorVelocity() * 10d), Units.inchesToMeters(Constants.WHEEL_DIAMETER), Constants.GEAR_REDUCTION, Constants.TICKS_PER_REV_FALCON);
	private static final DoubleSupplier rightVelocitySupplier = 
		() -> LightningMath.ticksToDistance((((WPI_TalonFX)RIGHT_MOTORS[1]).getSelectedSensorVelocity() * 10d), Units.inchesToMeters(Constants.WHEEL_DIAMETER), Constants.GEAR_REDUCTION, Constants.TICKS_PER_REV_FALCON);
	private static final DoubleSupplier leftPositionSupplier =  
		() -> LightningMath.ticksToDistance((((WPI_TalonFX)LEFT_MOTORS[1]).getSelectedSensorPosition()), Units.inchesToMeters(Constants.WHEEL_DIAMETER), Constants.GEAR_REDUCTION, Constants.TICKS_PER_REV_FALCON);
	private static final DoubleSupplier rightPositionSupplier = 
		() -> LightningMath.ticksToDistance((((WPI_TalonFX)RIGHT_MOTORS[1]).getSelectedSensorPosition()), Units.inchesToMeters(Constants.WHEEL_DIAMETER), Constants.GEAR_REDUCTION, Constants.TICKS_PER_REV_FALCON);
    
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
        this.imu = imu;


        this.withEachMotor((m) -> {
            WPI_TalonFX motor = (WPI_TalonFX)m;
            motor.setNeutralMode(NeutralMode.Coast);   
            motor.configOpenloopRamp(0.15); // TODO Tune this number for eric <3         
        });
    
        intitLogging();

		CommandScheduler.getInstance().registerSubsystem(this);

    }

    public boolean isMoving() {
        return ((WPI_TalonFX)LEFT_MOTORS[0]).getSelectedSensorVelocity() < 0.05 && ((WPI_TalonFX)RIGHT_MOTORS[0]).getSelectedSensorVelocity() < 0.05;
    }

    private void intitLogging() {
        DataLogger.addDataElement("leftVelocity", leftVelocitySupplier);
        DataLogger.addDataElement("rightVelocity", rightVelocitySupplier);
        DataLogger.addDataElement("leftPosition", leftPositionSupplier);
        DataLogger.addDataElement("rightPosition", rightPositionSupplier);
        DataLogger.addDataElement("heading", () -> this.getPose().getRotation().getDegrees());
        DataLogger.addDataElement("poseX", () -> this.getPose().getX());
        DataLogger.addDataElement("poseY", () -> this.getPose().getY());
    }

    @Override
    public void periodic() {
        super.periodic();
        SmartDashboard.putNumber("heading", imu.getHeading().getDegrees());
        SmartDashboard.putNumber("left motor vel", ((WPI_TalonFX)LEFT_MOTORS[1]).getSelectedSensorVelocity());
        SmartDashboard.putNumber("right motor vel", rightPositionSupplier.getAsDouble());
    }
}
