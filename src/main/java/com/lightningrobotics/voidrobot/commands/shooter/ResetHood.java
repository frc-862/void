package com.lightningrobotics.voidrobot.commands.shooter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.wpilibj2.command.InstantCommand;

public class ResetHood extends InstantCommand {

	private Shooter shooter;
	
	public ResetHood(Shooter shooter) {
		this.shooter = shooter;
		addRequirements(shooter);	
	}

	@Override
	public void initialize() {
		
		var file = Paths.get("/home/lvuser/robot_constants/", "robot_constants.txt").toFile();
		file.delete();

		var newFile = Paths.get("/home/lvuser/robot_constants/", "robot_constants.txt").toFile();

		try {
			FileWriter fw = new FileWriter(newFile, false);
			var offset = shooter.getRawHoodAngle();
			String newline = "hoodOffset: " + offset;
			fw.write(newline);
			System.out.println(newline);
			fw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

}
