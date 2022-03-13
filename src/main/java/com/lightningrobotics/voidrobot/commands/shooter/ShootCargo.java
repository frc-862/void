package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.common.util.filter.MovingAverageFilter;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ShootCargo extends CommandBase {

	private Shooter shooter;
	private Vision vision;
	private Indexer indexer;
	private Turret turret;

	private static double rpm;
	private static double distance;
	private static double hoodAngle;

	private MovingAverageFilter maf = new MovingAverageFilter(10);

	public ShootCargo(Shooter shooter, Indexer indexer, Turret turret, Vision vision) {
		this.shooter = shooter;
		this.indexer = indexer;
		this.vision = vision;
		this.turret = turret;

		addRequirements(shooter, indexer); // not adding vision or turret as it is read only

	}

	@Override
	public void execute() {

		distance = vision.getTargetDistance();
		if (distance > 0) {
			distance = maf.filter(distance);
		} else {
			distance = maf.get(); // TODO
		}
		
		rpm = Constants.DISTANCE_RPM_MAP.get(distance);
		hoodAngle = Constants.HOOD_ANGLE_MAP.get(distance);

		shooter.setRPM(rpm);
		shooter.setHoodAngle(hoodAngle);
		System.out.println("Has Vision");
			
		if(shooter.getArmed() && turret.getArmed()) { // TODO if (shooter.getArmed() && turret.getArmed() && shooter.getShooterPower() > 0) {
			indexer.toShooter();
		}

		SmartDashboard.putNumber("hood angle from map", Constants.HOOD_ANGLE_MAP.get(distance));
		SmartDashboard.putNumber("shooter from map", Constants.DISTANCE_RPM_MAP.get(distance));
		
	}

	@Override
	public void end(boolean interrupted) {
		shooter.stop();
		indexer.stop();
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
}
