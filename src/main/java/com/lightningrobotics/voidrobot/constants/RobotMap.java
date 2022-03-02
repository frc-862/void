package com.lightningrobotics.voidrobot.constants;

import edu.wpi.first.wpilibj.I2C;

public final class RobotMap {

    // PDH
    public static final int PDH_ID = 1;
    // Drivetain
    public static final int LEFT_MOTOR_1 = 4;
    public static final int LEFT_MOTOR_2 = 5;
    public static final int LEFT_MOTOR_3 = 6;
    public static final int RIGHT_MOTOR_1 = 1;
    public static final int RIGHT_MOTOR_2 = 2;
    public static final int RIGHT_MOTOR_3 = 3;

    // Climber
    public static final int WINCH1_MOTOR_ID = 7;
    public static final int WINCH2_MOTOR_ID = 8;
    public static final int WINCH3_MOTOR_ID = 9;
    public static final int WINCH4_MOTOR_ID = 10;

    // Intake
    public static final int INTAKE_MOTOR_ID = 15;
    public static final int INTAKE_WINCH_ID = 14;

    // Turret
    public static final int TURRET_MOTOR_ID = 12;
    public static final int LIMIT_SWITCH_NEGATIVE_ID = 2; 
    public static final int LIMIT_SWITCH_POSITIVE_ID = 3; 
    public static final int CENTER_SENSOR_ID = 69420;


    // Indexer 
    public static final int INDEXER_MOTOR_ID = 16;
    public static final int ENTER_BEAM_BREAK = 1;
    public static final int EXIT_BEAM_BREAK = 0;
    public static final I2C.Port i2cPort = I2C.Port.kMXP;
    
    // Shooter
    public static final int FLYWHEEL_MOTOR_ID = 11;
    public static final int HOOD_MOTOR_ID = 13;
    public static final int  SHOOTER_ENCODER_A = 9;
    public static final int  SHOOTER_ENCODER_B = 8;

}
