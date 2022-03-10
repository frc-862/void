package com.lightningrobotics.voidrobot.constants;

// import javax.smartcardio.CardNotPresentException;

import com.lightningrobotics.common.controller.FeedForwardController;
import com.lightningrobotics.common.controller.PIDFController;
import com.lightningrobotics.common.subsystem.drivetrain.differential.DifferentialGains;
import com.lightningrobotics.util.InterpolatedMap;

public final class Constants {

    // Drivetrain
    public static final double KP = 0.00058943;
    public static final double KI = 0.01;
    public static final double KD = 0.04;
    
    public static final double KS = 0.6848;
    public static final double KV = 2.0829;
    public static final double KA = 0.22588;

    public static final boolean LEFT_1_INVERT = false;
    public static final boolean LEFT_2_INVERT = false;
    public static final boolean LEFT_3_INVERT = false;

    public static final boolean RIGHT_1_INVERT = true;
    public static final boolean RIGHT_2_INVERT = true;
    public static final boolean RIGHT_3_INVERT = true;

    public static final double MAX_SPEED = 2.2;
    public static final double MAX_ACCELERATION = 0.75;
    public static final double TRACK_WIDTH = 0.76211; // 0.6565; 
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
    public static final double TURRET_NORMAL_MAX_MOTOR_OUTPUT = 0.6d;
    public static final double TURRET_REDUCED_MAX_MOTOR_OUTPUT = 0.2d;
    public static final double TURRET_kP_SLOW = 0.03; 
    public static final double TURRET_kI_SLOW = 0.00; 
    public static final double TURRET_kD_SLOW = 0.0009; 
    public static final double TURRET_kP_FAST = 0.02; 
    public static final double TURRET_kI_FAST = 0.00; 
    public static final double TURRET_kD_FAST = 0.001; 
    public static final double DEFAULT_ANGLE = 0;
    public static final double MAX_TURRET_ANGLE = 90d;
    public static final double MIN_TURRET_ANGLE = -90d;
    public static final double TURRET_ANGLE_TOLERANCE = 5; // degrees
    public static final double SLOW_PID_THRESHOLD = 10; // degrees
    public static final double READ_VISION_TIME = 1; //seconds
    public static final double TURRET_MANUAL_SPEED_MULTIPLIER = 0.25d;

    // Indexer
    public static final double DEFAULT_INDEXER_POWER = 0.5;
    public static final double RED_THRESHOLD = 0.295;
    public static final double BLUE_THRESHOLD = 0.25;

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

    public static final double MAX_HOOD_ANGLE = 5.5; // TODO get these soft limit values that we want
    public static final double MIN_HOOD_ANGLE = 0.3;

    public static final double SHOOT_LOW_RPM = 1500;  // 4100 TODO tune these
    public static final double SHOOT_LOW_ANGLE = 5.5;
    public static final double AUTO_SHOOT_COOLDOWN = 1.0;

	public static final double SHOOT_TARMAC_RPM = 3800;
	public static final double SHOOT_TARMAC_ANGLE = 0;
    //distance in meters, power in RPMs 
    public static final InterpolatedMap DISTANCE_RPM_MAP = new InterpolatedMap() {
        {
            
            put(0d, 0d);
            put(2.286d, 3500d);
			put(2.5908d, 3700d);
			put(3.175d, 3900d);
			put(4.191d, 4100d);
			put(4.064d, 4100d);
			put(3.9624d, 4250d);
			put(3.6576d, 4100d);
			put(3.3528d, 3900d);
			put(3.048d, 3800d);
			put(4.2672d, 4300d);
			put(4.572d, 4400d);
			put(5.1816d, 4700d);
			put(5.7919d, 4900d);
			put(6.4008d, 5350d);
			put(5.4964d, 4850d);
        }
    };

    //distance in meters, angle in degrees
    public static final InterpolatedMap HOOD_ANGLE_MAP = new InterpolatedMap() {
        {
            put(0d, 0d);
            put(2.286d, 0d);
			put(2.5908d, 0d);
			put(3.175d, 0d);
			put(4.191d, 1.5d);
			put(4.064d, 1.5d);
			put(3.9624d, 2d);
			put(3.6576d, 1.8d);
			put(3.3528d, 1.7d);
			put(3.048d, 1.6d);
			put(4.2672d, 2.6d);
			put(4.572d, 3.2d);
			put(5.1816d, 3.85d);
			put(5.7919d, 4.4d);
			put(6.4008d, 5.5d);
			put(5.4964d, 4.4d);
        }
    };

	// Intake
	public static final double INTAKE_DEPLOY_TIME = 2d;
    public static final double INTAKE_RETRACT_TIME = 2.3d;
    public static final double DEFAULT_INTAKE_POWER = 0.5;
    public static final double DEFAULT_WINCH_POWER = 1;
}
