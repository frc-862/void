package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Indexer.BallColor;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class StopAndShoot extends CommandBase {

	private final Shooter shooter;
	private final Hood hood;
	private final Indexer indexer;
	private final HubTargeting targeting;
	private final Drivetrain drivetrain;

	public StopAndShoot(Shooter shooter, Hood hood, Indexer indexer, HubTargeting targeting, Drivetrain drivetrain) {
		this.shooter = shooter;
		this.hood = hood;
		this.indexer = indexer;
		this.targeting = targeting;
		this.drivetrain = drivetrain;

		addRequirements(shooter, hood, indexer, drivetrain);
	}

	@Override
	public void execute() {

		boolean isEnenmyBall = !DriverStation.getAlliance().toString().equals(indexer.getUpperBallColor().toString()) && indexer.getUpperBallColor() != BallColor.nothing;
		if(!isEnenmyBall) {
			drivetrain.pidStop(); 
			var rpm = targeting.getTargetFlywheelRPM();
			var hoodAngle = targeting.getTargetHoodAngle();

			shooter.setRPM(rpm);
			hood.setAngle(hoodAngle);
			if (drivetrain.getCurrentVelocity() < Constants.MAXIMUM_LINEAR_SPEED_TO_SHOOT && targeting.onTarget()) { // getCurrentVelocity() may not work, may need another constant
				indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
			} 
		}
		else {
			shooter.setRPM(Constants.EJECT_BALL_RPM);
			hood.setAngle(Constants.EJECT_BALL_HOOD_ANGLE); 
			if (targeting.onTarget(Constants.EJECT_BALL_RPM, Constants.EJECT_BALL_HOOD_ANGLE)){
				indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
			}
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
