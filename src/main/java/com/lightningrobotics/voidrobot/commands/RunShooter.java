package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class RunShooter extends CommandBase {
    
    // Creates the shooter subsystem
    private Shooter shooter;

    private double targetRPM;

    public RunShooter(Shooter shooter, double targetRPM) {
        this.shooter = shooter;

        addRequirements(shooter);

        this.targetRPM = targetRPM;
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        shooter.setRPM(shooter.getRPMFromDashboard()); // Gets the desired RPM from the dashboard and sets them to the motor
    }

    @Override
    public void end(boolean interrupted) {

    }

    @Override
    public boolean isFinished() {
        return false;
    }
	
}
