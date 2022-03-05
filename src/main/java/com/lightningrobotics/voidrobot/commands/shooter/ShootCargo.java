package com.lightningrobotics.voidrobot.commands.shooter;

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

	private double startTime = 0;
	private boolean hasShot = false;
	private final double WAIT_AFTER_SHOOT_TIME = 1.5;

	public ShootCargo(Shooter shooter, Indexer indexer, Turret turret, Vision vision) {

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

		// shooter.setRPM(rpm);
		// shooter.setHoodAngle(hoodAngle);
		//TODO: uncomment the above if you want to use the interpolated map
		shooter.setRPM(2500);
		shooter.setHoodAngle(2);

		SmartDashboard.putBoolean("Shooter Armed", shooter.getArmed());
		SmartDashboard.putBoolean("TUrret Armed", turret.getArmed());
		hasShot = false;
		if(shooter.getArmed() && turret.getArmed()) {
			indexer.toShooter();
		}
		if(indexer.getUpperStatus()){
			startTime = Timer.getFPGATimestamp();
			hasShot = true;
		}
	}

	@Override
	public void end(boolean interrupted) {
		shooter.stop();
		indexer.stop();
	}

	@Override
	public boolean isFinished() {
		return Timer.getFPGATimestamp() - startTime > WAIT_AFTER_SHOOT_TIME && hasShot;
	}
	
}
