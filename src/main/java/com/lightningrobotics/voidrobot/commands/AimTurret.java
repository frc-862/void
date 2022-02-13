package com.lightningrobotics.voidrobot.commands;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AimTurret extends CommandBase {

    private Turret turret;
    private Vision vision;

    private DoubleSupplier degrees;

    public AimTurret(Turret turret, Vision vision) {
        this.turret = turret;
        this.vision = vision;

        // Not adding vision since its use is read-only
        addRequirements(turret);
    }

	@Deprecated
    public AimTurret(Turret turret, DoubleSupplier degrees) { // for tmep use
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
        // var targetAngle = vision.getTargetAngle();
        var targetAngle = degrees.getAsDouble();
        turret.setTargetAngle(targetAngle);
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
