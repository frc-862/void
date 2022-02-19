package com.lightningrobotics.voidrobot.constants;

public final class Constants {

    // Drivetrain Constants
    public static final double KP = 0.00048688;
    public static final double KI = 0.0;
    public static final double KD = 0.0;

    public static final double KS = 0.54496;
    public static final double KV = 2.2556;
    public static final double KA = 0.11811;

    public static final double MAX_SPEED = 1.0;
    public static final double MAX_ACCELERATION = 1.0;
    public static final double TRACK_WIDTH = 0.5842;

    // Turret Constants
    public static final double TURN_TURRET_GEAR_RATIO = 77;
    public static final int TURRET_GEAR_RATIO = 11;
    public static final double TURRET_kP = 0.0035; //TODO: tune this
    public static final double DEFAULT_ANGLE = 0;
    public static final double MAX_TURRET_ANGLE = 135d;
    public static final double MIN_TURRET_ANGLE = -135d;

	// Shooter Constants
    public static final double SHOOTER_KP = 0.00013484;
    public static final double SHOOTER_KI = 0;
    public static final double SHOOTER_KD = 0;
    public static final double SHOOTER_KS = 1.4864;//-.81807;
    public static final double SHOOTER_KV = 0.62326;//2.1597;
    public static final double SHOOTER_KA = 0.44521;//2.3746;

    // Inverts
    public static final boolean LEFT_1_INVERT = false;
    public static final boolean LEFT_2_INVERT = false;
    public static final boolean LEFT_3_INVERT = false;

    public static final boolean RIGHT_1_INVERT = true;
    public static final boolean RIGHT_2_INVERT = true;
    public static final boolean RIGHT_3_INVERT = true;

}