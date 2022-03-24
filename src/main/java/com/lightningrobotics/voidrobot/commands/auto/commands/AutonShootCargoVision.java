package com.lightningrobotics.voidrobot.commands.auto.commands;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutonShootCargoVision extends CommandBase {

	private Shooter shooter;
	private Hood hood;
	private Indexer indexer;
	private Turret turret;
	private HubTargeting targeting;

	public AutonShootCargoVision(Shooter shooter, Hood hood, Indexer indexer, Turret turret, HubTargeting targeting) {
		this.shooter = shooter;
		this.indexer = indexer;
		this.turret = turret;
		this.hood = hood;
		this.targeting = targeting;

		addRequirements(shooter, hood, indexer);

	}

	@Override
	public void initialize() {}
	
	@Override
	public void execute() {

		var rpm = targeting.getTargetFlywheelRPM();
		var hoodAngle = targeting.getTargetHoodAngle();

		System.out.println("wanted RPM " + rpm);
		System.out.println("current RPM " + shooter.getCurrentRPM());

		shooter.setRPM(rpm);
		hood.setAngle(hoodAngle);

		if(shooter.onTarget() && turret.onTarget() && hood.onTarget()) { 
			indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
		}

	}

	@Override
	public void end(boolean interrupted) {
		shooter.coast();
		indexer.stop();
		hood.stop();
	}

	@Override
	public boolean isFinished() {
		return indexer.getEjectedBall();
	}
	
}