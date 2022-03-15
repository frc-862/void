package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class RunShooterDashboard extends CommandBase {
    
    // Creates the shooter subsystem
    private final Shooter shooter;
    private final Hood hood;

	private ShuffleboardTab shooterTestTab = Shuffleboard.getTab("shooter test");
	private NetworkTableEntry setRPMEntry = shooterTestTab.add("set RPM", 0).getEntry();
	private NetworkTableEntry setAngleEntry = shooterTestTab.add("set hood angle", 0).getEntry();

    public RunShooterDashboard(Shooter shooter, Hood hood) {
        this.shooter = shooter;
        this.hood = hood;
        addRequirements(shooter, hood);
		
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {		
        shooter.setRPM(setRPMEntry.getDouble(0));  // shooter.getRPMFromDashboard() // Gets the desired RPM from the dashboard and sets them to the motor
        hood.setAngle(setAngleEntry.getDouble(0));
    }


    @Override
    public void end(boolean interrupted) {

    }

    @Override
    public boolean isFinished() {
        return false;
    }
	
}
