package com.lightningrobotics.voidrobot;

public final class Constants {

	// Climber
    public static final int WINCH1_MOTOR_ID = 11;
    public static final int WINCH2_MOTOR_ID = 12;

	// Intake
    public static final int INTAKE_MOTOR_ID = 7;
    public static final int INTAKE_SENSOR_ID = 14;

    // Turret 
    // public static final double TURN_TURRET_KP = 0.008;
    public static final double TURN_TURRET_GEAR_RATIO = 77;
    public static final int TURN_TURRET_ID = 2;    
    public static final int TURRET_MOTOR_ID = 13;
    public static final int TURRET_GEAR_RATIO = 11;
    public static final double TURRET_kP = 0.0035; //TODO: tune this
    public static final double DEFAULT_ANGLE = 0;
    public static final double MAX_TURRET_ANGLE = 135d;
    public static final double MIN_TURRET_ANGLE = -135d;
    public static final int TURRET_CONTROLLER_ID = 13;

    // Indexer 
    public static final int INDEXER_MOTOR_ID = 3; //TODO: what is a number
    public static final int ENTER_BEAM_BREAK = 8;
    public static final int EXIT_BEAM_BREAK = 9;
    public static final double DEFAULT_INDEXER_POWER = 0.7;

	// Shooter
    public static final int FLYWHEEL_MOTOR_ID = 3;
    public static final int HOOD_MOTOR_ID = 10;
    public static final double SHOOTER_KP = 0.00013484;
    public static final double SHOOTER_KI = 0;
    public static final double SHOOTER_KD = 0;
    public static final double SHOOTER_KS = 1.4864;//-.81807;
    public static final double SHOOTER_KV = 0.62326;//2.1597;
    public static final double SHOOTER_KA = 0.44521;//2.3746;


    

}
