package com.lightningrobotics.voidrobot.commands.turret;

import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Turret;

public class AimTurretConstrained extends AimTurret {

    private final Drivetrain drivetrain;

    private final double hubY = -2.607;
    private final double hubX = 0.699;

    public AimTurretConstrained(Turret turret, HubTargeting targeting, Drivetrain drivetrain) {
        super(turret, targeting);
        this.drivetrain = drivetrain;
    }
    
    @Override
    public void execute() {
        var poseY = drivetrain.getPose().getY();
        var poseX = drivetrain.getPose().getX();

        var alpha = Math.atan2(poseX - hubX, poseY - hubY);

        var hub_angle = (alpha + drivetrain.getPose().getRotation().getDegrees());

        if (hub_angle < 180) {
            hub_angle += 360;
        } 
        else if(hub_angle > 180) {
            hub_angle -= 360;
        }

        turret.setConstraint(hub_angle, 15);

        super.execute();
    }

    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
        turret.resetConstraint();
    }
}
