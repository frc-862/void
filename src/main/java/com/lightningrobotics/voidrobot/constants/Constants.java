package com.lightningrobotics.voidrobot.constants;

import java.util.HashMap;

import com.lightningrobotics.util.InterpolatedMap;

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

    // Indexer Constants
    public static final double DEFAULT_INDEXER_POWER = 0.5;

	// Shooter Constants
    public static final double SHOOTER_KP = 0.25; // 0.00023742; // tune
    public static final double SHOOTER_KI = 0;
    public static final double SHOOTER_KD = 0;

    public static final double SHOOTER_KS = 0; //0.58093;//-.81807;
    public static final double SHOOTER_KF = 0.0455;//2.1597;
    public static final double SHOOTER_KA = 0; //0.02415;//2.3746;

    public static final double SHOOTER_COOLDOWN = 0.25;


    public static final double HOOD_KP = 0d; // TODO Change these
    public static final double HOOD_KI = 0d;
    public static final double HOOD_KD = 0d;

    public static final double MAX_HOOD_ANGLE = 70; // TODO get these soft limit values that we want
    public static final double MIN_HOOD_ANGLE = 0;

    //distance in feet, power in RPMs 
    public static final InterpolatedMap DISTANCE_RPM_MAP = new InterpolatedMap() {
        {
            put(10d, 3900d);
            put(11d, 4000d);
            put(12d, 4200d);
            put(13d, 4300d);
            put(14d, 4450d);
            put(15d, 4550d);
            put(16d, 4700d);
        }
    };

    //height in pixels, angle in degrees //TODO: distance or pixels?  Also tune.
    public static final InterpolatedMap HOOD_ANGLE_MAP = new InterpolatedMap() {
        {
            put(10d, 65.2d);
            put(11d, 64.9d);
            put(12d, 64.7d);
            put(13d, 64.6d);
            put(14d, 64.3d);
            put(15d, 64.2d);
            put(16d, 64.2d);
        }
    };

    // Drivetrain Inverts
    public static final boolean LEFT_1_INVERT = false;
    public static final boolean LEFT_2_INVERT = false;
    public static final boolean LEFT_3_INVERT = false;

    public static final boolean RIGHT_1_INVERT = true;
    public static final boolean RIGHT_2_INVERT = true;
    public static final boolean RIGHT_3_INVERT = true;

}
