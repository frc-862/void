package com.lightningrobotics.voidrobot.commands.auto.commands;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutonVisionShooting extends CommandBase {

	private final Shooter shooter;
	private final Hood hood;
	private final Vision vision;
	private final Indexer indexer;
	private final Turret turret;

	private double visionOffset;
	private double distanceOffset;

	private boolean shooting = false;

	public AutonVisionShooting(Shooter shooter, Hood hood, Indexer indexer, Turret turret, Vision vision, double visionOffset, double distanceOffset) {
		this.shooter = shooter;
		this.indexer = indexer;
		this.vision = vision;
		this.turret = turret;
		this.hood = hood;
		this.visionOffset = visionOffset;
		this.distanceOffset = distanceOffset;

		addRequirements(shooter, hood, indexer, turret);

	}

	@Override
	public void initialize() {	
		vision.zeroBias();
		vision.adjustBiasDistance(distanceOffset);
		vision.adjustBiasAngle(visionOffset);
	}
	
	@Override
	public void execute() {
		var distance = vision.getTargetDistance();
		var rpm = (Constants.DISTANCE_RPM_MAP.get(distance) + Constants.ANGLE_POWER_MAP.get(turret.getCurrentAngle().getDegrees()));
		var hoodAngle = Constants.HOOD_ANGLE_MAP.get(distance);

		shooter.setRPM(rpm);
		hood.setAngle(hoodAngle);
		turret.setAngle(turret.getCurrentAngle().getDegrees() + vision.getOffsetAngle());

		if ((shooter.onTarget() && hood.onTarget()) || shooting) {
			indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
			shooting = true;
		}
		else if (indexer.getColorSensor().getProximity() < 350) {
			indexer.setPower(0.5d);
		} else {
			indexer.stop();
		}

	}

	@Override
	public void end(boolean interrupted) {
		// shooter.coast();
		// indexer.stop();
		// hood.stop();
		// turret.stop();
		shooting = false;
		vision.zeroBias();
		
	}

	@Override
	public boolean isFinished() {
		return indexer.getEjectedBall();
	}
	
}