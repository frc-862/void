package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ReverseFlywheel extends CommandBase {

	private final Shooter shooter;
	private final Indexer indexer;

	public ReverseFlywheel(Shooter shooter, Indexer indexer) {
		this.shooter = shooter;
		this.indexer = indexer;

		addRequirements(shooter, indexer);
	}

	@Override
	public void execute() {
		shooter.setPower(-0.3d);
		indexer.setPower(-1);
	}

	@Override
	public void end(boolean interrupted) {
		shooter.coast();
		indexer.stop();
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
}
