package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ShootClose extends CommandBase {

	private final Shooter shooter;
	private final Hood hood;
	private final Indexer indexer;
	private final Turret turret;
	private final Vision vision;

	public ShootClose(Shooter shooter, Hood hood, Indexer indexer, Turret turret, Vision vision) {
		this.shooter = shooter;
		this.hood = hood;
		this.indexer = indexer;
		this.turret = turret;
		this.vision = vision;

		addRequirements(shooter, indexer); // not adding vision or turret as it is read onl
	}
	@Override
	public void initialize() {
		turret.setAngle(0d);
		vision.turnOffVisionLight();
	}

	@Override
	public void execute() {
		shooter.setRPM(Constants.SHOOT_LOW_RPM);
		hood.setAngle(Constants.SHOOT_LOW_ANGLE);
		
		if(shooter.onTarget() && turret.onTarget() && hood.onTarget()) {
			indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
		}
	}

	@Override
	public void end(boolean interrupted) {
		shooter.coast();
		indexer.stop();
		hood.setAngle(0);
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
}
