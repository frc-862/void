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

    private static double targetAngle = 0; // this is the angle that we are setting to the turret

    private double offsetAngle = 0d;
    private double startAngle; 
    private DoubleSupplier controllerInput;

    public AimTurret(Turret turret, Vision vision, DoubleSupplier controllerInput) {
        this.turret = turret;
        this.vision = vision;
        this.controllerInput = controllerInput;

        // Not adding vision since its use is read-only
        addRequirements(turret, vision);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void execute() {
     //   offsetAngle  = vision.getOffsetAngle(); // gets the vision offset angle 
        offsetAngle += controllerInput.getAsDouble();
        // startAngle = turret.getTurretAngle().getDegrees(); // gets the current angle of the turret
        
        // this is getting us the angle that we need to go to using the current angle and the needed rotation 
        // targetAngle = startAngle + offsetAngle;
        
        turret.setTargetAngle(offsetAngle); // setting the target angle of the turret
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
