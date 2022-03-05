package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class RunShooter extends CommandBase {
    
    // Creates the shooter subsystem
    private Shooter shooter;

    private double targetRPM;

    public RunShooter(Shooter shooter, double targetRPM) {
        this.shooter = shooter;
        this.targetRPM = targetRPM;
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        shooter.setRPM(targetRPM); 
    }

    @Override
    public void end(boolean interrupted) {
        shooter.stop();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
	
}
