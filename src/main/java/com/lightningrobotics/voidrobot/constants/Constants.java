package com.lightningrobotics.voidrobot.constants;

import com.lightningrobotics.common.controller.FeedForwardController;
import com.lightningrobotics.common.controller.PIDFController;
import com.lightningrobotics.common.subsystem.drivetrain.differential.DifferentialGains;
import com.lightningrobotics.common.util.InterpolationMap;

public final class Constants {

	public static final double DEFAULT_DEMO_SPEED_LIM = 0.35;
    public static final double DEMO_INTAKE_POWER = 0.8d;
    public static final double DEMO_INDEXER_POWER = 0.8d;
    public static final double DEMO_RPM = 2000d;

    // Drivetrain
    public static final double KP = 0.00066934;
    public static final double KI = 0.01;
    public static final double KD = 0.04;
    
    public static final double KS = 0.74863;
    public static final double KV = 2.1297;
    public static final double KA = 0.35019;

    public static final double DRIVETRAIN_BRAKE_KP = 0.036934;
    public static final double DRIVETRAIN_MAX_ACCELERATION = 200; //encoder ticks per 100 ms

    public static final boolean LEFT_1_INVERT = false;
    public static final boolean LEFT_2_INVERT = false;
    public static final boolean LEFT_3_INVERT = false;

    public static final boolean RIGHT_1_INVERT = true;
    public static final boolean RIGHT_2_INVERT = true;
    public static final boolean RIGHT_3_INVERT = true;

    public static final double MAX_SPEED = 2.2;
    public static final double MAX_ACCELERATION = 0.75;
    public static final double TRACK_WIDTH = 0.75616; 
	public static final double WHEEL_DIAMETER = 4.0725;
	public static final double GEAR_REDUCTION = 6.7d;
	public static final double TICKS_PER_REV_FALCON = 2048d;

	public static final DifferentialGains DIFFERENTIAL_GAINS = new DifferentialGains(
        Constants.MAX_SPEED,
        Constants.MAX_ACCELERATION,
        Constants.TRACK_WIDTH,
        new boolean[]{Constants.LEFT_1_INVERT, Constants.LEFT_2_INVERT, Constants.LEFT_3_INVERT},
        new boolean[]{Constants.RIGHT_1_INVERT, Constants.RIGHT_2_INVERT, Constants.RIGHT_3_INVERT},
        new PIDFController(Constants.KP, Constants.KI, Constants.KD),
        new FeedForwardController(Constants.KS, Constants.KV, Constants.KA)
    );

    public static final double MAXIMUM_LINEAR_SPEED_TO_SHOOT = 0.3; //TODO: tune. also in meters per second.

    public static final double MAXIMUM_ANGULAR_SPEED_TO_SHOOT = 1; //TODO: tune. also in meters per second.

    // Turret
	public static final PIDFController TURRET_PID_SLOW = new PIDFController(Constants.TURRET_kP_SLOW, Constants.TURRET_kI_SLOW, Constants.TURRET_kD_SLOW);
    public static final PIDFController TURRET_PID_FAST = new PIDFController(Constants.TURRET_kP_FAST, Constants.TURRET_kI_FAST, Constants.TURRET_kD_FAST);
    public static final double TURN_TURRET_GEAR_RATIO = 14;
    public static final double MIN_TURRET_PWR = 0.054;
    public static final double TURRET_NORMAL_MAX_MOTOR_OUTPUT = 0.6d;
    public static final double TURRET_REDUCED_MAX_MOTOR_OUTPUT = 0.2d;
    public static final double TURRET_kP_SLOW = 0.03; 
    public static final double TURRET_kI_SLOW = 0.00; 
    public static final double TURRET_kD_SLOW = 0.0009; 
    public static final double TURRET_kP_FAST = 0.02; 
    public static final double TURRET_kI_FAST = 0.00; 
    public static final double TURRET_kD_FAST = 0.001; 
    public static final double DEFAULT_ANGLE = 0;
    public static final double MAX_TURRET_ANGLE = 100d;
    public static final double MIN_TURRET_ANGLE = -100d;
    public static final double TURRET_TOLERANCE = 5; // degrees
    public static final double SLOW_PID_THRESHOLD = 10; // degrees

    // Indexer
    public static final double DEFAULT_INDEXER_POWER = 1.0;
    public static final double AUTON_INDEXER_POWER = 0.9;
    public static final double AUTON_CENTER_POWER = 0.4;
    public static final double RED_THRESHOLD = 0.38;
    public static final double BLUE_THRESHOLD = 0.32;
    public static final double INDEX_DEBOUNCE_TIME = 0.1;

	// Shooter Constants
    public static final double SHOOTER_KP = 0.17;
    public static final double SHOOTER_KI = 0;
    public static final double SHOOTER_KD = 0;

    public static final double SHOOTER_KS = 0;
    public static final double SHOOTER_KF = 0.0455;
    public static final double SHOOTER_KA = 0;

	public static final double LONG_RPM_BIAS = 100; // TODO set large number for testing
	public static final double SHORT_RPM_BIAS = 50;
	public static final double SHORT_BIAS_DISTANCE = 3.25;
	public static final double RPM_BIAS_TIME = 1;

	public static final double SHOOTER_TOLERANCE = 100d;
	public static final double HOOD_TOLERANCE = .2d;
	public static final double DEFAULT_DISTANCE_BIAS = 0.15d;

	public static final double HOOD_KP = 0.55d;
    public static final double HOOD_KI = 0d;
    public static final double HOOD_KD = 0.00555d;
	public static final PIDFController HOOD_PID =  new PIDFController(Constants.HOOD_KP, Constants.HOOD_KI, Constants.HOOD_KD);
    public static final double HOOD_MANUAL_SPEED_MULTIPLIER = 0.1d;
    public static final double HOOD_ZERO_SPEED = -0.4d;

    public static final double SHOOT_LOW_RPM = 1750;
    public static final double SHOOT_LOW_ANGLE = 3.0;
    public static final double AUTO_SHOOT_COOLDOWN = 1.0;

	public static final double SHOOT_TARMAC_RPM = 3800;
	public static final double SHOOT_TARMAC_ANGLE = 0;

    public static final double DEFAULT_DISTANCE_BIAS_ADJUSTMENT = 0.05d;
    public static final double DEFAULT_ANGLE_BIAS_ADJUSTMENT = 1;

    // Canned shot Constants
    //Close wall
    public static final double CLOSE_CANNED_SHOT_HOOD_ANGLE = 0;
    public static final double CLOSE_CANNED_SHOT_TURRET_POSITION = 0;
    public static final double CLOSE_CANNED_SHOT_FLYWHEEL_SPEED = 3650;
    //Far wall
    public static final double FAR_CANNED_SHOT_HOOD_ANGLE = 2.4;
    public static final double FAR_CANNED_SHOT_TURRET_POSITION = 0;
    public static final double FAR_CANNED_SHOT_FLYWHEEL_SPEED = 5000;

	// Limelight
	public static final double MOUNT_HEIGHT = 37.5;
	public static final double HUB_HEIGHT = 104;
	public static final double MOUNT_ANGLE = 32;
	public static final double HUB_CENTER_OFFSET = 24;
	public static final double SNAPSHOT_DELAY = 0.3;
	public static final int DISTANCE_MOVING_AVG_ELEMENTS = 3;

    // Auto Shoot
    public static final double EJECT_BALL_RPM = 1500;
    public static final double EJECT_BALL_HOOD_ANGLE = 4.0;

    //distance in meters, power in RPMs 
    public static final InterpolationMap DISTANCE_RPM_MAP = new InterpolationMap() {
        {
			// put(2.46d, 3550d);
			// put(3.07d, 3650d);
			// put(3.56d, 3850d);
			// put(4.24d, 3950d);
			// put(4.6d, 4250d);
			// put(5.13d, 4300d);
			// put(6.02d, 4550d);

			// put(2.46d, 3550d);
            // put(3.11d, 3750d);
            // put(3.67d, 3850d);
            // put(4.13d, 4000d);
            // // put(4.9d, 4400d);
			// put(5.22, 4350d);
            // put(5.6d, 4500d);
			// put(6.19d, 4550d);
            // // put(6.08d, 4800d);
            // // put(7.11d, 5000d);
			// put(7.33d, 4750d);
			// put(8.03, 5300d);
			// put(9.35d, 5400d);

			// put(8.03d, 5400d);
            // put(9.35d, 5500d);




			put(2.46d, 3550d);
            put(3.11d, 3750d);
            put(3.67d, 3850d);
            put(4.13d, 4000d);
            put(4.9d, 4400d);
            put(5.6d, 4650d);
            put(6.08d, 4800d);
            put(7.11d, 5200d);
			put(8.03d, 5600d);
        }
    };

    //distance in meters, angle in degrees
    public static final InterpolationMap HOOD_ANGLE_MAP = new InterpolationMap() {
        {
			// put(2.46d, 0d);
			// put(3.07d, 0d);
			// put(3.56d, 0.2d);
			// put(4.24d, 0.4d);
			// put(4.6d, 2.0d);
			// put(5.13d, 0.8d);
			// put(6.02d, 1.1d);

           
            // put(2.46d, 0d);
            // put(3.11d, 0d);
            // put(3.67d, 1.2d);
            // put(4.13d, 1.4d);
            // put(4.9d, 1.9d);
            // put(5.6d, 2.3d);
			// put(6.19d, 2.7d);
            // // put(6.08d, 2.6d);
            // // put(7.11d, 2.4d);
			// put(7.33d, 2.5);
			// put(8.03d, 2.3d);
            // put(9.35d, 3.0d);





			    
            put(2.46d, 0d);
            put(3.11d, 0d);
            put(3.67d, 1.2d);
            put(4.13d, 1.4d);
            put(4.9d, 1.9d);
            put(5.6d, 2.3d);
            put(6.08d, 2.6d);
            put(7.11d, 2.4d);
			put(8.03d, 2.3d);
        }
    };

    //distance in meters, time the ball takes to get in from the shooter to the target
    public static final InterpolationMap DISTANCE_TO_TIME_SHOOT_MAP = new InterpolationMap() {
        {
            put(0d, 1.5d);
            put(10d, 1.5d);
            put(20d, 1.5d);
        }
    };

    public static final boolean SHOT_TUNING = false; // use this when making a new interpolation
                                                    //TODO: set this back later
	// Intake
	public static final double INTAKE_DEPLOY_TIME = 2d;
    public static final double INTAKE_RETRACT_TIME = 2.3d;
    public static final double DEFAULT_INTAKE_POWER = 1.0; // 0.5
    public static final double DEFAULT_INTAKE_WINCH_POWER = 0.8d;

    //Climber
    public static final double DEFAULT_PIVOT_POWER = 1.0;
    public static final double MID_RUNG_VALUE = 275000; // 250500d; // 296000;
    public static final double MAX_ARM_VALUE = 372000;
    public static final double HOLD_HEIGHT = 34000;
    public static final double RELEASE_HEIGHT = 150000;
    public static final double REACH_HEIGHT = 327000;
    public static final double ARM_TARGET_THRESHOLD = 750;
    public static final double ON_RUNG_ANGLE = 1.4;
    public static final double ARM_KP = 1d;

}
