package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class RunShooter extends CommandBase {
    
    // Creates the shooter subsystem
    private Shooter shooter;

    public RunShooter(Shooter shooter) {
        this.shooter = shooter;

        addRequirements(shooter);
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        shooter.setRPM(shooter.getRPMsFromDashboard()); // Gets the desired RPMs from the dashboard and sets them to the motor
    }

    @Override
    public void end(boolean interrupted) {

    }

    @Override
    public boolean isFinished() {
        return false;
    }
	
}
