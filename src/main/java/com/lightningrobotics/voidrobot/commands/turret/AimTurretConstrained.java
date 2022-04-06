package com.lightningrobotics.voidrobot.commands.turret;

import com.lightningrobotics.common.util.InterpolationMap;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.wpilibj.DriverStation;

public class AimTurretConstrained extends AimTurret {

    private final Drivetrain drivetrain;

    private static final InterpolationMap TIME_ANGLE_MAP = new InterpolationMap() {
        {
			// put(0, 5);
            // put(0, 70);
            // put(0, 88);
            // put(0, 50);
            // put();
        }
    };

    private final double hubY = -2.607;
    private final double hubX = 0.699;

    public AimTurretConstrained(Turret turret, HubTargeting targeting, Drivetrain drivetrain) {
        super(turret, targeting);
        this.drivetrain = drivetrain;
    }
    
    @Override
    public void execute() {
        // var poseY = drivetrain.getPose().getY();
        // var poseX = drivetrain.getPose().getX();

        // var alpha = Math.atan2(poseX - hubX, poseY - hubY);

        // var hub_angle = (alpha + drivetrain.getPose().getRotation().getDegrees());

        // if (hub_angle < 180) {
        //     hub_angle += 360;
        // } 
        // else if(hub_angle > 180) {
        //     hub_angle -= 360;
        // }

        //var hub_angle = TIME_ANGLE_MAP.get(DriverStation.getMatchTime());

        turret.setConstraint(0, 25);

        super.execute();
    }

    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
        turret.resetConstraint();
    }
}
