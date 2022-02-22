package com.lightningrobotics.voidrobot.constants;

import com.lightningrobotics.common.controller.PIDFController;

import edu.wpi.first.math.geometry.Rotation2d;


public final class HowitzerConstants {

    public static class RobotMap {
        public static final int FRONT_LEFT_DRIVE_MOTOR = 8;
        public static final int FRONT_LEFT_ANGLE_MOTOR = 7;
        public static final int FRONT_LEFT_CANCODER = 16;
        public static final int FRONT_RIGHT_DRIVE_MOTOR = 11;
        public static final int FRONT_RIGHT_ANGLE_MOTOR = 12;
        public static final int FRONT_RIGHT_CANCODER = 17;
        public static final int BACK_LEFT_DRIVE_MOTOR = 10;
        public static final int BACK_LEFT_ANGLE_MOTOR = 9;
        public static final int BACK_LEFT_CANCODER = 15;
        public static final int BACK_RIGHT_DRIVE_MOTOR = 13;
        public static final int BACK_RIGHT_ANGLE_MOTOR = 14;
        public static final int BACK_RIGHT_CANCODER = 18;
        public static final int PIGEON = 19;
    }

    public static class ModuleConstants {

        public static final double DRIVE_P = 0.22;
        public static final double DRIVE_I = 0.22;
        public static final double DRIVE_D = 0.001;
        public static final double DRIVE_F = 0.0;
        public static final PIDFController DRIVE_CONTROLLER = new PIDFController(DRIVE_P, DRIVE_I, DRIVE_D, DRIVE_F);

        public static final double AZIMUTH_P = 0.22;
        public static final double AZIMUTH_I = 0.22;
        public static final double AZIMUTH_D = 0.0005;
        public static final double AZIMUTH_F = 0.054;
        public static final PIDFController AZIMUTH_CONTROLLER = new PIDFController(AZIMUTH_P, AZIMUTH_I, AZIMUTH_D, AZIMUTH_F);

        public static final double TICKS_PER_REV_CANCODER = 4096; // CANCoder has 4096 ticks/rotation

        public static final Rotation2d FRONT_LEFT_OFFSET = Rotation2d.fromDegrees(-95.09765625d);
        public static final Rotation2d FRONT_RIGHT_OFFSET = Rotation2d.fromDegrees(-12.744140625d);
        public static final Rotation2d BACK_LEFT_OFFSET = Rotation2d.fromDegrees(30.673828125d);
        public static final Rotation2d BACK_RIGHT_OFFSET = Rotation2d.fromDegrees(119.00390625d);

    }

    public static class DrivetrainConstants {
        public static final int NUM_MODULES = 4;

        // NOTE that in a perfect world, these two would be the same thing as we would be comfortable driving as fast as possible
        public static final double REAL_MAX_SPEED = 16.2; //ft/sec 
        public static final double MAX_SPEED = REAL_MAX_SPEED; //ft/sec
        public static final double MAX_ANGULAR_SPEED = MAX_SPEED; //ft/sec
        public static final double MAX_ANGULAR_ACCEL = 2 * Math.PI;
        public static final double MAX_ACCEL = 10d; //ft/sec^2
    }

    public static class Wheelbase {
        public static final double W = 22.5/12d; //ft
        public static final double L = 22.5/12d; //ft
        public static final double WHEEL_CIRCUMFERENCE = (4d / 12d) * Math.PI; //ft
        public static final double GEARING = 6.86d;
        public static final double TICKS_PER_REV_TALON_FX = 2048d;
    }

    public static class JoystickConstants {
        public static final int DRIVER_CONTROLLER = 0;
    }

}