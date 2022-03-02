package com.lightningrobotics.voidrobot.constants;

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
        Constants.PID,
        Constants.FEEDFORWARD
    );

	// These are not final so we can tune on dash
	public static PIDFController PID = new PIDFController(Constants.KP, Constants.KI, Constants.KD);
    public static FeedForwardController FEEDFORWARD = new FeedForwardController(Constants.KS, Constants.KV, Constants.KA); 

    // Turret
    public static final double TURN_TURRET_GEAR_RATIO = 14;
    public static final double TURRET_NORMAL_MAX_MOTOR_OUTPUT = 0.6d;
    public static final double TURRET_REDUCED_MAX_MOTOR_OUTPUT = 0.2d;
    public static final double TURRET_kP = 0.03; 
    public static final double TURRET_kI = 0.00; 
    public static final double TURRET_kD = 0.0009; 
    public static final double DEFAULT_ANGLE = 0;
    public static final double MAX_TURRET_ANGLE = 80d;
    public static final double MIN_TURRET_ANGLE = -80d;
    public static final double TURRET_ANGLE_TOLERANCE = 5; // degrees

    // Indexer
    public static final double DEFAULT_INDEXER_POWER = 0.5;

    // Intake
    public static final double INTAKE_DEPLOY_TIME = 1;
    public static final double DEFAULT_INTAKE_POWER = 1;

	// Shooter
    public static final double SHOOTER_KP = 0.25; // 0.00023742; // tune
    public static final double SHOOTER_KI = 0;
    public static final double SHOOTER_KD = 0;

    public static final double SHOOTER_KS = 0; //0.58093;//-.81807;
    public static final double SHOOTER_KF = 0.0455;//2.1597;
    public static final double SHOOTER_KA = 0; //0.02415;//2.3746;

	public static final double HOOD_KP = 0d; // TODO tune these
    public static final double HOOD_KI = 0d;
    public static final double HOOD_KD = 0d;
	public static final PIDFController HOOD_PID = new PIDFController(Constants.HOOD_KP, Constants.HOOD_KI, Constants.HOOD_KD);

    public static final double MAX_HOOD_ANGLE = 70; // TODO get these soft limit values that we want
    public static final double MIN_HOOD_ANGLE = 0;

    //height in pixels, power in RPMs //TODO: distance or pixels? Also tune.
    public static final InterpolatedMap DISTANCE_RPM_MAP = new InterpolatedMap() {
        {
            put(0.0, 0.0);
            put(1.0, 1.0);
        }
    };

    //height in pixels, angle in degrees //TODO: distance or pixels?  Also tune.
    public static final InterpolatedMap HOOD_ANGLE_MAP = new InterpolatedMap() {
        {
            put(0.0, 0.0);
            put(1.0, 1.0);
        }
    };

}
