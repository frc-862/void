package com.lightningrobotics.voidrobot.constants;

// import javax.smartcardio.CardNotPresentException;

import com.lightningrobotics.common.controller.FeedForwardController;
import com.lightningrobotics.common.controller.PIDFController;
import com.lightningrobotics.common.subsystem.drivetrain.differential.DifferentialGains;
import com.lightningrobotics.common.util.InterpolationMap;

public final class Constants {

    // Drivetrain
    public static final double KP = 0.00066934;
    public static final double KI = 0.01;
    public static final double KD = 0.04;
    
    public static final double KS = 0.74863;
    public static final double KV = 2.1297;
    public static final double KA = 0.35019;

    public static final boolean LEFT_1_INVERT = false;
    public static final boolean LEFT_2_INVERT = false;
    public static final boolean LEFT_3_INVERT = false;

    public static final boolean RIGHT_1_INVERT = true;
    public static final boolean RIGHT_2_INVERT = true;
    public static final boolean RIGHT_3_INVERT = true;

    public static final double MAX_SPEED = 2.2;
    public static final double MAX_ACCELERATION = 0.75;
    public static final double TRACK_WIDTH = 0.75616; // 0.6565; 
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
    public static final double MAX_TURRET_ANGLE = 110d;
    public static final double MIN_TURRET_ANGLE = -110d;
    public static final double TURRET_TOLERANCE = 5; // degrees
    public static final double SLOW_PID_THRESHOLD = 10; // degrees
    public static final double READ_VISION_TIME = 1; //seconds
    public static final double TURRET_MANUAL_SPEED_MULTIPLIER = 0.25d;

    // Indexer
    public static final double DEFAULT_INDEXER_POWER = 1.0; // 0.5
    public static final double RED_THRESHOLD = 0.295;
    public static final double BLUE_THRESHOLD = 0.25;
    public static final double INDEX_DEBOUNCE_TIME = 0.1;

	// Shooter Constants
    public static final double SHOOTER_KP = 0.25; // 0.00023742; // tune
    public static final double SHOOTER_KI = 0;
    public static final double SHOOTER_KD = 0;

    public static final double SHOOTER_KS = 0; //0.58093;//-.81807;
    public static final double SHOOTER_KF = 0.0455;//2.1597;
    public static final double SHOOTER_KA = 0; //0.02415;//2.3746;

    public static final double SHOOTER_COOLDOWN = 1;

	public static final double SHOOTER_TOLERANCE = 50d;
	public static final double HOOD_TOLERANCE = .2d;

	public static final double HOOD_KP = 0.9d;
    public static final double HOOD_KI = 0d;
    public static final double HOOD_KD = 0d;
	public static final PIDFController HOOD_PID = new PIDFController(Constants.HOOD_KP, Constants.HOOD_KI, Constants.HOOD_KD);
    public static final double HOOD_MANUAL_SPEED_MULTIPLIER = 0.1d;
    public static final double HOOD_ZERO_SPEED = -0.4d;

    public static final double SHOOT_LOW_RPM = 1750;  // 4100 TODO tune these
    public static final double SHOOT_LOW_ANGLE = 3.0;
    public static final double AUTO_SHOOT_COOLDOWN = 1.0;

	public static final double SHOOT_TARMAC_RPM = 3800;
	public static final double SHOOT_TARMAC_ANGLE = 0;


    //distance in meters, power in RPMs 
    public static final InterpolationMap ANGLE_POWER_MAP = new InterpolationMap() {
        {
			put(-135d, 150d);
			put(-90d, 150d);
			put(-45d, 150d);
			put(-15d, 0d);
			put(15d, 0d);
			put(45d, 150d);
			put(90d, 150d);
			put(135d, 150d);
        }
    };

    //distance in meters, power in RPMs 
    public static final InterpolationMap DISTANCE_RPM_MAP = new InterpolationMap() {
        {
			put(0d,0d);
			put(2.46d, 3550d);
			put(3.07d, 3650d);
			put(3.56d, 3850d);
			put(4.24d, 3950d);
			put(5.13d, 4250d);
			put(6.02d, 4550d);
			put(7.11d, 5000d);
        }
    };

    //distance in meters, angle in degrees
    public static final InterpolationMap HOOD_ANGLE_MAP = new InterpolationMap() {
        {
			put(0d, 0d);
			put(2.46d, 0d);
			put(3.07d, 0d);
			put(3.56d, 0.2d);
			put(4.24d, 0.4d);
			put(5.13d, 0.8d);
			put(6.02d, 1.1d);
			put(7.11d, 2.4d);
        }
    };

    public static final boolean SHOT_TUNING = false; // use this when making a new interpolation

	// Intake
	public static final double INTAKE_DEPLOY_TIME = 2d;
    public static final double INTAKE_RETRACT_TIME = 2.3d;
    public static final double DEFAULT_INTAKE_POWER = 1.0; // 0.5
    public static final double DEFAULT_WINCH_POWER = 1;

    //Climber
    public static final double DEFAULT_PIVOT_POWER = 0.5;
    public static final double MID_RUNG_VALUE = 257000;
    public static final double MAX_ARM_VALUE = 312000;
    public static final double HOLD_HEIGHT = 35000; //height to engage the traversal hooks
    public static final double REACH_HEIGHT = 320000; //height climber reaches to when pivoting back
    public static final double ARM_TARGET_THRESHOLD = 1000;
    public static final double ON_RUNG_ANGLE = 1.4;
    
    //TODO: tune these values
    public static final double GYRO_SETTLE_THRESHOLD = 0;
    public static final double GYRO_SETTLE_TIME = 0;

}
