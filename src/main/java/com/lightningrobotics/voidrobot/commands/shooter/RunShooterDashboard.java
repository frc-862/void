package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class RunShooterDashboard extends CommandBase {
    
    // Creates the shooter subsystem
    private final Shooter shooter;
    private final Hood hood;

    public RunShooterDashboard(Shooter shooter, Hood hood) {
        this.shooter = shooter;
        this.hood = hood;
        addRequirements(shooter, hood);
		
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {		
        shooter.setRPM(shooter.getRPMFromDashboard()); // Gets the desired RPM from the dashboard and sets them to the motor
        hood.setAngle(hood.getAngleFromDashboard());
    }


    @Override
    public void end(boolean interrupted) {

    }

    @Override
    public boolean isFinished() {
        return false;
    }
	
}
