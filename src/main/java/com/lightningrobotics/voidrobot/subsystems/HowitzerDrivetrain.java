package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderConfiguration;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.lightningrobotics.common.geometry.LightningOdometer;
import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.common.subsystem.drivetrain.swerve.SwerveDrivetrain;
import com.lightningrobotics.common.subsystem.drivetrain.swerve.SwerveGains;
import com.lightningrobotics.common.subsystem.drivetrain.swerve.SwerveModule;
import com.lightningrobotics.voidrobot.constants.HowitzerConstants.RobotMap;
import com.lightningrobotics.voidrobot.constants.HowitzerConstants.DrivetrainConstants;
import com.lightningrobotics.voidrobot.constants.HowitzerConstants.ModuleConstants;
import com.lightningrobotics.voidrobot.constants.HowitzerConstants.Wheelbase;

import edu.wpi.first.math.geometry.Rotation2d;

public class HowitzerDrivetrain extends SwerveDrivetrain {

    private LightningOdometer odometer;
    private LightningIMU IMU;
    private static final SwerveGains SWERVE_GAINS = new SwerveGains(
            Wheelbase.W, // width
            Wheelbase.L, // length
            DrivetrainConstants.MAX_SPEED, // maxSpeed
            DrivetrainConstants.REAL_MAX_SPEED, // maxRealSpeed
            DrivetrainConstants.MAX_ACCEL, // maxAcceleration
            DrivetrainConstants.MAX_ANGULAR_SPEED, // maxAngularSpeed
            new boolean[] { true, true, true, true }, // turnMotorInverts
            new boolean[] { false, false, false, false } // driveMotorInverts
    );

    public HowitzerDrivetrain() {
        super(SWERVE_GAINS, new SwerveModule[] {
                makeModule(Modules.FRONT_LEFT, 
                        RobotMap.FRONT_LEFT_DRIVE_MOTOR, 
                        RobotMap.FRONT_LEFT_ANGLE_MOTOR,
                        RobotMap.FRONT_LEFT_CANCODER, 
                        ModuleConstants.FRONT_LEFT_OFFSET),
                makeModule(Modules.FRONT_RIGHT, 
                        RobotMap.FRONT_RIGHT_DRIVE_MOTOR, 
                        RobotMap.FRONT_RIGHT_ANGLE_MOTOR,
                        RobotMap.FRONT_RIGHT_CANCODER, 
                        ModuleConstants.FRONT_RIGHT_OFFSET),
                makeModule(Modules.REAR_LEFT, 
                        RobotMap.BACK_LEFT_DRIVE_MOTOR, 
                        RobotMap.BACK_LEFT_ANGLE_MOTOR,
                        RobotMap.BACK_LEFT_CANCODER, 
                        ModuleConstants.BACK_LEFT_OFFSET),
                makeModule(Modules.REAR_RIGHT, 
                        RobotMap.BACK_RIGHT_DRIVE_MOTOR, 
                        RobotMap.BACK_RIGHT_ANGLE_MOTOR,
                        RobotMap.BACK_RIGHT_CANCODER, 
                        ModuleConstants.BACK_RIGHT_OFFSET)
        });
        IMU = LightningIMU.navX();
        odometer = new LightningOdometer(IMU.getHeading());
    }

    private static SwerveModule makeModule(Modules module, int driveID, int angleID, int encoderID, Rotation2d offset) {

        // Set Up Drive Motor
        WPI_TalonFX driveMotor = new WPI_TalonFX(driveID);
        driveMotor.configFactoryDefault();
        driveMotor.setNeutralMode(NeutralMode.Brake);

        // Set Up Azimuth Motor
        WPI_TalonFX azimuthMotor = new WPI_TalonFX(angleID);
        azimuthMotor.configFactoryDefault();
        azimuthMotor.setNeutralMode(NeutralMode.Brake);
        azimuthMotor.setInverted(true);

        // Set Up Encoder
        CANCoder canCoder = new CANCoder(encoderID);
        CANCoderConfiguration canCoderConfiguration = new CANCoderConfiguration();
        canCoderConfiguration.initializationStrategy = SensorInitializationStrategy.BootToAbsolutePosition;
        canCoderConfiguration.absoluteSensorRange = AbsoluteSensorRange.Signed_PlusMinus180;
        canCoderConfiguration.magnetOffsetDegrees = offset.getDegrees();
        canCoderConfiguration.sensorDirection = true;
        canCoder.configAllSettings(canCoderConfiguration);

        // Build Module
        return new SwerveModule(
                SWERVE_GAINS,
                driveMotor,
                azimuthMotor,
                () -> Rotation2d.fromDegrees(canCoder.getAbsolutePosition()),
                () -> (driveMotor.getSelectedSensorVelocity() * 10d) * (Wheelbase.WHEEL_CIRCUMFERENCE / (Wheelbase.TICKS_PER_REV_TALON_FX * Wheelbase.GEARING)),
                null,
                ModuleConstants.AZIMUTH_CONTROLLER);

    }

}