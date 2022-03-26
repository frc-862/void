package com.lightningrobotics.voidrobot.commands.auto.commands;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutonVisionShooting extends CommandBase {

	private final Shooter shooter;
	private final Hood hood;
	private final Indexer indexer;
	private final HubTargeting targeting;

	private double angleBias;
	private double distanceBias;
	private double RPMBias;

	private boolean shooting = false;


	public AutonVisionShooting(Shooter shooter, Hood hood, Indexer indexer, HubTargeting targeting, double angleBias, double distanceBias, double RPMBias) {
		this.shooter = shooter;
		this.indexer = indexer;
		this.hood = hood;
		this.targeting = targeting;
		this.angleBias = angleBias;
		this.distanceBias = distanceBias;
		this.RPMBias = RPMBias;

		addRequirements(shooter, hood, indexer);

	}

	@Override
	public void initialize() {	
		targeting.zeroBias();
		targeting.adjustBiasDistance(distanceBias);
	}
	
	@Override
	public void execute() {
		SmartDashboard.putNumber("distance bias thingy", distanceBias);
		SmartDashboard.putNumber("angle bias things", angleBias);

		var rpm = targeting.getTargetFlywheelRPM();
		var hoodAngle = targeting.getTargetHoodAngle();

		shooter.setRPM(rpm + RPMBias);
		hood.setAngle(hoodAngle);

		if (indexer.getCollectedBall()) {			
			targeting.adjustBiasAngle(angleBias);
		}

		if ((targeting.onTarget()) || shooting) {
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
		targeting.zeroBias();
		
	}

	@Override
	public boolean isFinished() {
		return indexer.getEjectedBall();
	}
	
}