package com.lightningrobotics.voidrobot;

public final class Constants {
    public static final int INTAKE_MOTOR_ID = 7;

    public static final int FLYWHEEL_MOTOR_ID = 12;
    public static final int HOOD_MOTOR_ID = 10;

    public static final int WINCH1_MOTOR_ID = 11;
    public static final int WINCH2_MOTOR_ID = 12;

    public static final int INTAKE_SENSOR_ID = 14;

    public static final int INDEXER_MOTOR_ID = 8; //TODO: what is a number

    // Turret 
    // public static final double TURN_TURRET_KP = 0.008;
    public static final double TURN_TURRET_GEAR_RATIO = 77;
    public static final int TURN_TURRET_ID = 2;    
    public static final int TURRET_MOTOR_ID = 13;
    public static final int TURRET_GEAR_RATIO = 11;
    public static final double TURRET_kP = 0.035; //TODO: tune this
    public static final double DEFAULT_ANGLE = 0;



    // beam breaks 
    public static final int ENTER_BEAM_BREAK = 0;
    public static final int EXIT_BEAM_BREAK = 1;

    public static final double SHOOTER_KP = 0.0025;
    public static final double SHOOTER_KI = 0;
    public static final double SHOOTER_KD = 0;

}
