package com.lightningrobotics.voidrobot;

import edu.wpi.first.wpilibj.RobotBase;

public final class Main {

	// HELLO
	
	private Main() { }

	public static void main(String... args) {
		RobotBase.startRobot(Robot::new);
	}

}
