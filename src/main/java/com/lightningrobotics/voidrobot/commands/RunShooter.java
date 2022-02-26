package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class RunShooter extends CommandBase {
    
    // Creates the shooter subsystem
    private Shooter shooter;

    private double targetRPMs;
    public RunShooter(Shooter shooter, double targetRPMs) {
        this.shooter = shooter;

        addRequirements(shooter);

        this.targetRPMs = targetRPMs;
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        // shooter.setRPM(targetRPMs);
    }

    @Override
    public void end(boolean interrupted) {

    }

    @Override
    public boolean isFinished() {
        return false;
    }
	
}
