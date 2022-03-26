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

		if ((targeting.onTarget())) {
			indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
		}

	}

	@Override
	public void end(boolean interrupted) {
		shooter.setRPM(4000);
		hood.setAngle(0.5);
		targeting.zeroBias();
	}

	@Override
	public boolean isFinished() {
		return indexer.getBallCount() == 0;
	}
	
}