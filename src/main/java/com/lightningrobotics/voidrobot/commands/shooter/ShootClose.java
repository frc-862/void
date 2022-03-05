package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ShootClose extends CommandBase {

	private Shooter shooter;
	private Indexer indexer;
	private Turret turret;

	private double startTime = 0;
	private boolean hasShot = false;

	public ShootClose(Shooter shooter, Indexer indexer, Turret turret) {

		this.shooter = shooter;
		this.indexer = indexer;
		this.turret = turret;

		addRequirements(shooter, indexer); // not adding vision or turret as it is read only

	}

	@Override
	public void execute() {
		shooter.setRPM(Constants.SHOOT_CLOSE_RPM);
		shooter.setHoodAngle(Constants.SHOOT_CLOSE_ANGLE);

		SmartDashboard.putBoolean("Shooter Armed", shooter.getArmed());
		SmartDashboard.putBoolean("Turret Armed", turret.getArmed());
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
		return Timer.getFPGATimestamp() - startTime > Constants.AUTO_SHOOT_COOLDOWN && hasShot;
	}
	
}
