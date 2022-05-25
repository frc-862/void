package com.lightningrobotics.voidrobot.simulation;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class FieldController {
    private static Field2d field = new Field2d();

    public static void Initialize(){
        SmartDashboard.putData(field);
        field.setRobotPose(new Pose2d(0,0,new Rotation2d()));
    }

    public static void SetRobotPosition(Pose2d pos){
        field.setRobotPose(pos);
    }
}
