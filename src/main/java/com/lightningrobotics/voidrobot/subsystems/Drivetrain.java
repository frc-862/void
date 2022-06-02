package com.lightningrobotics.voidrobot.subsystems;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.lightningrobotics.common.logging.DataLogger;
import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.common.subsystem.drivetrain.differential.DifferentialDrivetrain;
import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import com.lightningrobotics.voidrobot.constants.Constants;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.simulation.AnalogGyroSim;
import edu.wpi.first.wpilibj.simulation.DifferentialDrivetrainSim;
import edu.wpi.first.wpilibj.simulation.EncoderSim;
import edu.wpi.first.wpilibj.simulation.RoboRioSim;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
// import lightningrobotics.sim.SimulationConstants;
import com.lightningrobotics.voidrobot.simulation.FieldController;
import com.lightningrobotics.voidrobot.simulation.Battery;
// import lightningrobotics.sim.util.PIDFController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Drivetrain extends DifferentialDrivetrain {

    private LightningIMU imu;

    private double currentVelocity;

    private Pose2d pose = new Pose2d();
    private Pose2d poseContinious = new Pose2d();
    private Pose2d prevPose = new Pose2d();

    private Rotation2d heading = Rotation2d.fromDegrees(0);
    private Rotation2d prevHeading = Rotation2d.fromDegrees(0);

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
    
    private DifferentialDrivetrainSim drivetrain;
    private DifferentialDriveOdometry odometry;
    
    private Encoder leftEncoder = new Encoder(5, 6);
    private Encoder rightEncoder = new Encoder(7, 8);
    private AnalogGyro gyro = new AnalogGyro(1);
    
    private EncoderSim leftEncoderSim = new EncoderSim(leftEncoder);
    private EncoderSim rightEncoderSim = new EncoderSim(rightEncoder);
    private AnalogGyroSim gyroSim = new AnalogGyroSim(gyro);

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

        leftEncoderSim.setPeriod(0.02);
        rightEncoderSim.setPeriod(0.02);

		setCanBusFrameRate(StatusFrameEnhanced.Status_1_General, 200);
		setCanBusFrameRate(StatusFrameEnhanced.Status_2_Feedback0, 500);
    
        intitLogging();

        this.withEachMotor((m) -> {
            WPI_TalonFX motor = (WPI_TalonFX)m;
            motor.config_kP(0, Constants.DRIVETRAIN_BRAKE_KP);
            motor.config_kF(0, 0.005);
        });

        drivetrain = new DifferentialDrivetrainSim(
            DCMotor.getNEO(3),     // 2 NEO motors on each side of the drivetrain.
            7.29,                    // 7.29:1 gearing reduction.
            Constants.MOI,          
            Constants.ROBOT_MASS,     
            Constants.WHEEL_DIAMETER,
            Constants.TRACK_WIDTH,       
            Constants.MEASUREMENT_NOISE);
      
            odometry = 
            new DifferentialDriveOdometry(new Rotation2d(), new Pose2d(0,0,new Rotation2d()));

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
        DataLogger.addDataElement("accelX", () -> imu.getNavxAccelerationX());
        DataLogger.addDataElement("accelY", () -> imu.getNavxAccelerationY());
        DataLogger.addDataElement("accelZ", () -> imu.getNavxAccelerationZ());
        // Moveing while shooting stuff
    }

	private void setCanBusFrameRate(StatusFrameEnhanced frame, int freq) {
		((WPI_TalonFX)RIGHT_MOTORS[1]).setStatusFramePeriod(frame, freq, 200);
		((WPI_TalonFX)RIGHT_MOTORS[2]).setStatusFramePeriod(frame, freq, 200);

		((WPI_TalonFX)LEFT_MOTORS[1]).setStatusFramePeriod(frame, freq, 200);
		((WPI_TalonFX)LEFT_MOTORS[2]).setStatusFramePeriod(frame, freq, 200);

	}

	@Override
    public void periodic() {
        super.periodic();
		
        pose = this.getPose();
        poseContinious = this.getPose();
        heading = this.getPose().getRotation();
        
        var deltaX = pose.getX() - prevPose.getX();
        var deltaY = pose.getY() - prevPose.getY(); 

        currentVelocity = (Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)));
        var rot = Math.atan2(deltaY, deltaX);  
        
        prevPose = pose;
        prevHeading = heading;

        SmartDashboard.putNumber("currentVelocity", currentVelocity);
        SmartDashboard.putNumber("rot thigy", rot);

        setMotorCoastMode();

        SmartDashboard.putNumber("heading", imu.getHeading().getDegrees());
        // SmartDashboard.putNumber("left motor vel", ((WPI_TalonFX)LEFT_MOTORS[1]).getSelectedSensorVelocity());
        // SmartDashboard.putNumber("right motor vel", rightPositionSupplier.getAsDouble());

        SmartDashboard.putNumber("velocity", getCurrentVelocity()); // TODO want to test this
    }

    public void setMotorBreakMode() {
        this.withEachMotor((m) -> {
            WPI_TalonFX motor = (WPI_TalonFX)m;
            motor.setNeutralMode(NeutralMode.Brake);   
            motor.configOpenloopRamp(0.15);
        });
    } 

    public void setMotorCoastMode() {
        this.withEachMotor((m) -> {
            WPI_TalonFX motor = (WPI_TalonFX)m;
            motor.setNeutralMode(NeutralMode.Coast);   
            motor.configOpenloopRamp(0.15);
        });
    }

    public double getCurrentVelocity() {
        return (leftVelocitySupplier.getAsDouble() + rightVelocitySupplier.getAsDouble()) / 2; // TODO want to test this
        // return -currentVelocity; // this is negative b/c we want it shooter-forward
    }

    public void setMotorBrakeMode(){
        this.withEachMotor((m) -> {
            WPI_TalonFX motor = (WPI_TalonFX)m;
            motor.setNeutralMode(NeutralMode.Brake);
        });
    }
    
    private void updateOdometry(){
    
        leftEncoderSim.setDistance(drivetrain.getLeftPositionMeters());
        leftEncoderSim.setRate(drivetrain.getLeftVelocityMetersPerSecond());
        rightEncoderSim.setDistance(drivetrain.getRightPositionMeters());
        rightEncoderSim.setRate(drivetrain.getRightVelocityMetersPerSecond());

         gyroSim.setAngle(drivetrain.getPose().getRotation().getDegrees());

         odometry.update(
            Rotation2d.fromDegrees(gyroSim.getAngle()), 
           drivetrain.getLeftPositionMeters(), drivetrain.getRightPositionMeters());

        SmartDashboard.putNumber("pos x", odometry.getPoseMeters().getX());
        SmartDashboard.putNumber("pos y", odometry.getPoseMeters().getY());
        FieldController.SetRobotPosition(odometry.getPoseMeters());
        Battery.UseBattery(drivetrain);
    }

    @Override
    public void tankDrive(double leftPWR, double rightPWR) {
        super.tankDrive(leftPWR, rightPWR);
        var leftVoltageInput = leftPWR * Constants.MAX_INPUT_VOLTAGE;
        var rightVoltageInput = rightPWR * Constants.MAX_INPUT_VOLTAGE;
        drivetrain.setInputs(leftVoltageInput, rightVoltageInput);

   SmartDashboard.putNumber("leftDriveVolt", leftVoltageInput);
   SmartDashboard.putNumber("rightDriveVolt", rightVoltageInput);

        drivetrain.update(Constants.SIM_UPDATE_TIME);
        updateOdometry();
    }

    public void pidStop() { 
        this.withEachMotor((m) -> {
            WPI_TalonFX motor = (WPI_TalonFX)m;
            motor.set(TalonFXControlMode.Velocity, 0);
        });
    }

}
