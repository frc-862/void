package com.lightningrobotics.voidrobot.commands.auto.commands;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;

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
		targeting.adjustBiasAngle(angleBias);
		targeting.adjustBiasDistance(distanceBias + Constants.DEFAULT_DISTANCE_BIAS);
	}
	
	@Override
	public void execute() {
		var rpm = targeting.getTargetFlywheelRPM();
		var hoodAngle = targeting.getTargetHoodAngle();

		var biasedRPM = rpm + RPMBias;
		shooter.setRPM(biasedRPM);
		hood.setAngle(hoodAngle);

		if ((targeting.onTarget(biasedRPM, hoodAngle))) {
			indexer.setPower(Constants.AUTON_INDEXER_POWER);
		}

	}

	@Override
	public void end(boolean interrupted) {
		indexer.stop();
		shooter.setRPM(4000);
		hood.setAngle(0.5);
		targeting.zeroBias();
	}

	@Override
	public boolean isFinished() {
		return indexer.getBallCount() == 0;
	}
	
}