package com.lightningrobotics.voidrobot.commands.demo;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class DemoShoot extends CommandBase {

	private final Shooter shooter;
	private final Indexer indexer;

	public DemoShoot(Shooter shooter, Indexer indexer) {
		this.shooter = shooter;
		this.indexer = indexer;

		addRequirements(shooter, indexer);
	}

	@Override
	public void execute() {
		shooter.setRPM(Constants.DEMO_RPM);
		indexer.setPower(Constants.DEMO_INDEXER_POWER);
	}

	@Override
	public void end(boolean interrupted) {
		shooter.stop();
		indexer.stop();
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
}
