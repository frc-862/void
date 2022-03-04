package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class RunShooterDashboard extends CommandBase {
    
    // Creates the shooter subsystem
    private Shooter shooter;

    private double targetRPM;

    public RunShooterDashboard(Shooter shooter, double targetRPM) {
        this.shooter = shooter;

        this.targetRPM = targetRPM;
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        shooter.setRPM(targetRPM);  // shooter.getRPMFromDashboard() // Gets the desired RPM from the dashboard and sets them to the motor
        //shooter.setRPM(targetRPM);
    }

    @Override
    public void end(boolean interrupted) {

    }

    @Override
    public boolean isFinished() {
        return false;
    }
	
}
