package com.lightningrobotics.voidrobot.commands.auto.commands;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutonShootCargo extends CommandBase {

	private Shooter shooter;
	private Hood hood;
	private Vision vision;
	private Indexer indexer;
	private Turret turret;

	public AutonShootCargo(Shooter shooter, Hood hood, Indexer indexer, Turret turret, Vision vision) {
		this.shooter = shooter;
		this.indexer = indexer;
		this.vision = vision;
		this.turret = turret;
		this.hood = hood;

		addRequirements(shooter, hood, indexer); // not adding vision or turret as it is read only

	}

	@Override
	public void execute() {

		var distance = vision.getTargetDistance();
		var rpm = Constants.DISTANCE_RPM_MAP.get(distance);
		var hoodAngle = Constants.HOOD_ANGLE_MAP.get(distance);

		System.out.println("wanted RPM " + rpm);
		System.out.println("current RPM " + shooter.getCurrentRPM());

		shooter.setRPM(rpm);
		hood.setAngle(hoodAngle);


		if(shooter.onTarget() && turret.onTarget()) {
			indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
		}
	}

	@Override
	public void end(boolean interrupted) {
		shooter.stop();
		indexer.stop();
		hood.setAngle(0);
	}

	@Override
	public boolean isFinished() {
		return indexer.getBallCount() == 0;
	}
	
}