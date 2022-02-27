package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ShootCargo extends CommandBase {

	private Shooter shooter;
	private Vision vision;
	private Indexer indexer;
	private Turret turret;

	public ShootCargo(Shooter shooter, Indexer indexer, Vision vision, Turret turret) {

		this.shooter = shooter;
		this.indexer = indexer;
		this.vision = vision;
		this.turret = turret;

		addRequirements(shooter, indexer); // not adding vision or turret as it is read only

	}

	@Override
	public void execute() {

		var distance = vision.getTargetDistance();
		var rpm = Constants.DISTANCE_RPM_MAP.get(distance);
		var hoodAngle = Constants.HOOD_ANGLE_MAP.get(distance);

		shooter.setRPM(rpm);
		shooter.setHoodAngle(hoodAngle);

		if(shooter.getArmed() && turret.getArmed()) {
			indexer.toShooter();
		}
		
	}

	@Override
	public void end(boolean interrupted) {
		shooter.stop();
		indexer.stop();
	}

	@Override
	public boolean isFinished() {
		return indexer.getBallCount() == 0;
	}
	
}
