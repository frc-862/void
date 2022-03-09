package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class RunShooterDashboard extends CommandBase {
    
	private static ShuffleboardTab shooterTestTab = Shuffleboard.getTab("shooter test");
	private static NetworkTableEntry visonDist = shooterTestTab.add("Vision Dist", 0).getEntry();
	private static NetworkTableEntry mapRPM = shooterTestTab.add("mapped rpm", 0).getEntry();
	private static NetworkTableEntry mapAngle = shooterTestTab.add("mapped angle", 0).getEntry();
    // Creates the shooter subsystem
    private Shooter shooter;
	Vision vision;

    public RunShooterDashboard(Shooter shooter, Vision vision) {
        this.shooter = shooter;
        addRequirements(shooter);
		this.vision = vision;
		
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
		var dist = vision.getTargetDistance();
		
        shooter.setRPM(shooter.getRPMFromDashboard());  // shooter.getRPMFromDashboard() // Gets the desired RPM from the dashboard and sets them to the motor
        shooter.setHoodAngle(shooter.getHoodAngleFromDashboard());

		visonDist.setNumber(dist);
		mapRPM.setNumber(Constants.DISTANCE_RPM_MAP.get(dist));
		mapAngle.setNumber(Constants.HOOD_ANGLE_MAP.get(dist));
    }


    @Override
    public void end(boolean interrupted) {

    }

    @Override
    public boolean isFinished() {
        return false;
    }
	
}
