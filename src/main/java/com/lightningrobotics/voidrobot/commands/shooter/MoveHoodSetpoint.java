package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class MoveHoodSetpoint extends CommandBase {

	private static final double DEFAULT_HOOD_ANGLE = 3d;

    private static ShuffleboardTab hoodTab = Shuffleboard.getTab("Hood");
	private static NetworkTableEntry hoodSetpoint = hoodTab.add("Hood Setpoint", DEFAULT_HOOD_ANGLE).getEntry();
    private static NetworkTableEntry hoodAngle = hoodTab.add("Hood Angle", DEFAULT_HOOD_ANGLE).getEntry();

	private Shooter shooter;

	public MoveHoodSetpoint(Shooter shooter) {
		this.shooter = shooter;

		addRequirements(shooter);
	}

	@Override
	public void initialize() {
	}

	@Override
	public void execute() {
		shooter.setHoodAngle(hoodSetpoint.getDouble(DEFAULT_HOOD_ANGLE));
		hoodAngle.setDouble(shooter.getHoodAngle());
	}

	@Override
	public void end(boolean interrupted) {
		shooter.setHoodPower(0);
	}

	@Override
	public boolean isFinished() {
		return false;
	}

}
