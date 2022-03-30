package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Indexer.BallColor;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ShootCargo extends CommandBase {

	private final Shooter shooter;
	private final Hood hood;
	private final Indexer indexer;
	private final HubTargeting targeting;

	public ShootCargo(Shooter shooter, Hood hood, Indexer indexer, HubTargeting targeting) {
		this.shooter = shooter;
		this.hood = hood;
		this.indexer = indexer;
		this.targeting = targeting;

		addRequirements(shooter, hood, indexer);
	}

	@Override
	public void execute() {
		boolean isEnenmyBall = !DriverStation.getAlliance().toString().equals(indexer.getUpperBallColor().toString()) && indexer.getUpperBallColor() != BallColor.nothing;
		
		var rpm = !isEnenmyBall ? targeting.getTargetFlywheelRPM() : Constants.EJECT_BALL_RPM;
		var hoodAngle = !isEnenmyBall ? targeting.getTargetHoodAngle() : Constants.EJECT_BALL_HOOD_ANGLE;

		shooter.setRPM(rpm);
		hood.setAngle(hoodAngle);
			
		if((targeting.onTarget() && !isEnenmyBall) || isEnenmyBall) {
			indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
		}
		
	}

	@Override
	public void end(boolean interrupted) {
		shooter.coast();
		indexer.stop();
		if (indexer.getBallCount() == 0) {
			hood.setAngle(0);
		} else {
			hood.setPower(0);
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
}
