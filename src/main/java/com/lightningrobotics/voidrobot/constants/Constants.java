package com.lightningrobotics.voidrobot.constants;

public final class Constants {

    // Drivetrain Constants
    public static final double KP = 0.00602;
    public static final double KI = 0.01;
    public static final double KD = 0.04;
    
    public static final double KS = 0.71254;
    public static final double KV = 1.9331;
    public static final double KA = 0.23729;

    public static final double MAX_SPEED = 1.0;
    public static final double MAX_ACCELERATION = 1.0;
    public static final double TRACK_WIDTH = 0.7499; // 0.6565; 

    // Turret Constants
    public static final double TURN_TURRET_GEAR_RATIO = 77;
    public static final int TURRET_GEAR_RATIO = 11;
    public static final double TURRET_kP = 0.0035; //TODO: tune this
    public static final double DEFAULT_ANGLE = 0;
    public static final double MAX_TURRET_ANGLE = 135d;
    public static final double MIN_TURRET_ANGLE = -135d;

    // Indexer Constants
    public static final double DEFAULT_INDEXER_POWER = 0.5;


	// Shooter Constants
    public static final double SHOOTER_KP = 0.25; // 0.00023742; // tune
    public static final double SHOOTER_KI = 0;
    public static final double SHOOTER_KD = 0;

    public static final double SHOOTER_KS = 0; //0.58093;//-.81807;
    public static final double SHOOTER_KF = 0.0455;//2.1597;
    public static final double SHOOTER_KA = 0; //0.02415;//2.3746;

    // Drivetrain Inverts
    public static final boolean LEFT_1_INVERT = false;
    public static final boolean LEFT_2_INVERT = false;
    public static final boolean LEFT_3_INVERT = false;

    public static final boolean RIGHT_1_INVERT = true;
    public static final boolean RIGHT_2_INVERT = true;
    public static final boolean RIGHT_3_INVERT = true;

}
