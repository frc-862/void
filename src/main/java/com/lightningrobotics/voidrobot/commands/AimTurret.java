package com.lightningrobotics.voidrobot.commands;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AimTurret extends CommandBase {

    private Turret turret;
    private Vision vision;
    
    private double offsetAngle = 0d;

    public AimTurret(Turret turret, Vision vision) {
        this.turret = turret;
        this.vision = vision;

        // Not adding vision since its use is read-only
        addRequirements(turret, vision);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void execute() {
        offsetAngle  = vision.getOffsetAngle(); // gets the vision offset angle 

        turret.setVisionOffset(offsetAngle); // setting the target angle of the turret
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
