package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ShootClose extends CommandBase {

    private final double CLOSE_DISTANCE = 2d;
    private final double TURRET_ANGLE = 0d;

    private Shooter shooter;
    private Indexer indexer;
    private Turret turret;

    public ShootClose(Shooter shooter, Indexer indexer, Turret turret) {

        this.shooter = shooter;
        this.indexer = indexer;
        this.turret = turret;

        addRequirements(shooter, indexer, turret); // not adding vision or turret as it is read only

    }

    @Override
    public void execute() {

        var rpm = Constants.DISTANCE_RPM_MAP.get(CLOSE_DISTANCE);
        var hoodAngle = Constants.HOOD_ANGLE_MAP.get(CLOSE_DISTANCE);

        shooter.setRPM(rpm);
        shooter.setHoodAngle(hoodAngle);
        turret.setTarget(TURRET_ANGLE);

        if(shooter.getArmed() && turret.getArmed()) {
        	indexer.toShooter();
        }

    }

    @Override
    public void end(boolean interrupted) {
        shooter.stop();
        indexer.stop();
    }

    @Override
    public boolean isFinished() {
        return indexer.getBallCount() == 0;
    }

}
