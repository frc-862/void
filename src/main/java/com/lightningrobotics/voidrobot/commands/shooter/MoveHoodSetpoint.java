package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class MoveHoodSetpoint extends CommandBase {

	private static final double DEFAULT_HOOD_ANGLE = 3d;

    private static ShuffleboardTab hoodTab = Shuffleboard.getTab("Hood");
	private static NetworkTableEntry hoodSetpoint = hoodTab.add("Hood Setpoint", DEFAULT_HOOD_ANGLE).getEntry();

	private Hood hood;

	public MoveHoodSetpoint(Hood hood) {
		this.hood = hood;

		addRequirements(hood);
	}

	@Override
	public void initialize() {
	}

	@Override
	public void execute() {
		hood.setAngle(hoodSetpoint.getDouble(DEFAULT_HOOD_ANGLE));
	}

	@Override
	public void end(boolean interrupted) {
		hood.stop();
	}

	@Override
	public boolean isFinished() {
		return false;
	}

}
