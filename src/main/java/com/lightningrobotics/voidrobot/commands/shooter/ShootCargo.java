package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.common.util.filter.MovingAverageFilter;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ShootCargo extends CommandBase {

	private Shooter shooter;
	private Hood hood;
	private Indexer indexer;
	private Vision vision;
	private Turret turret;

	private static double rpm;
	private static double distance;
	private static double hoodAngle;

	private MovingAverageFilter maf = new MovingAverageFilter(10);

	public ShootCargo(Shooter shooter, Hood hood, Indexer indexer, Turret turret, Vision vision) {
		this.shooter = shooter;
		this.hood = hood;
		this.indexer = indexer;
		this.vision = vision;
		this.turret = turret;

		addRequirements(shooter, hood, indexer);

	}

	@Override
	public void execute() {

		distance = vision.getTargetDistance();
		if (distance > 0) {
			distance = maf.filter(distance);
		} else {
			distance = maf.get();
		}
		
		rpm = Constants.DISTANCE_RPM_MAP.get(distance);
		hoodAngle = Constants.HOOD_ANGLE_MAP.get(distance);

		shooter.setRPM(rpm);
		hood.setAngle(hoodAngle);
		System.out.println("Has Vision");
			
		if(shooter.onTarget() && turret.onTarget() && hood.onTarget()) {
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
