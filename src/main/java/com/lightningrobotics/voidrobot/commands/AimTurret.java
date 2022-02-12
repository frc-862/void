package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AimTurret extends CommandBase {

    private Turret turret;
    private Vision vision;

    private double degrees;

    public AimTurret(Turret turret, Vision vision) {
        this.turret = turret;
        this.vision = vision;

        // Not adding vision since its use is read-only
        addRequirements(turret);
    }

    public AimTurret(Turret turret, double degrees) { // for tmep use
        this.turret = turret;
        this.degrees = degrees;

        // Not adding vision since its use is read-only
        addRequirements(turret);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void execute() {
        // double targetAngle = vision.getTargetAngle(); TODO: dont comment this out (Nick did this and wrote this comment)
        double targetAngle = degrees;
        turret.setTargetAngle(Rotation2d.fromDegrees(targetAngle));
    }

    @Override
    public void end(boolean interrupted) {
        turret.stopTurret();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

}
