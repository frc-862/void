package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Indexer.BallColor;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutoFlywheelHood extends CommandBase {

	private final Shooter shooter;
	private final Hood hood;
	private final HubTargeting targeting;
	private final Indexer indexer;


	public AutoFlywheelHood(Shooter shooter, Hood hood, HubTargeting targeting, Indexer indexer){
		this.shooter = shooter;
		this.hood = hood;
		this.targeting = targeting;
		this.indexer = indexer;

		addRequirements(shooter, hood);
	}

	@Override
	public void execute() {
		boolean isEnenmyBall = indexer.isEnenmyBall();
		
		if(indexer.getBallCount() > 0){
			var rpm = isEnenmyBall ? Constants.EJECT_BALL_RPM : targeting.getTargetFlywheelRPM();
			var hoodAngle = isEnenmyBall ? Constants.EJECT_BALL_HOOD_ANGLE : targeting.getTargetHoodAngle();

			shooter.setRPM(rpm);
			// hood.setAngle(hoodAngle);

			// // if the only ball is enemy ball, shoots directly
			// if(isEnenmyBall && indexer.getBallCount() == 1){
			// 	indexer.setPower(1);
			// }
			// else{
			// 	indexer.setPower(0);
			// }
		}
		else{
			shooter.coast();
			hood.setAngle(0);
			// indexer.setPower(0);
		}
	}

	@Override
	public void end(boolean interrupted) {
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
}
