package com.lightningrobotics.voidrobot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Vision extends SubsystemBase {

	// No actuators here - just networktable stuff. Update in periodic

	// TODO add nt entries for these -- cordinate with vision
	private static double targetAngle = 0d;
	private static double targetDistance = 0d;

	public Vision() {}

	@Override
	public void periodic() {
		// TODO update targetAngle & targetDistance from nt entries
	}

	public double getTargetAngle() {
		return targetAngle;
	}

	public double getTargetDistance() {
		return targetDistance;
	}

}
