package com.lightningrobotics.voidrobot.commands.auto;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.commands.indexer.AutoIndexCargo;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Intake;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.commands.indexer.RunIndexer;
import com.lightningrobotics.voidrobot.commands.intake.RunIntake;
import com.lightningrobotics.voidrobot.commands.shooter.RunShooter;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutonShoot extends CommandBase {

	private Indexer indexer;
	private Shooter shooter;
	private Turret turret;
	private Intake intake;

	private double RPM;
	private double hoodAngle;
	private double turretAngle;

	private double startTime;
	private final double shootDelay = 0.3d;

	enum ShootingState {
		WATING_TO_ARM,
		WATING_TO_SHOOT,
		SHOOTING
	}
	ShootingState shootingState; 

	public AutonShoot(Indexer indexer, Shooter shooter, Turret turret, double RPM, double hoodAngle, double turretAngle) {
		this.indexer = indexer;
		this.shooter = shooter;
		this.turret = turret;
		this.RPM = RPM;
		this.turretAngle = turretAngle;

		addRequirements(indexer);
	}

	@Override
	public void initialize() {
		shooter.setRPM(RPM);
		// turret.setTarget(turretAngle);
		shootingState = ShootingState.WATING_TO_ARM;
		// shooter.setHoodAngle(hoodAngle);
	}

	@Override
	public void execute() {
		switch (shootingState) {
			case WATING_TO_ARM: 
				if (shooter.getArmed()) {
					shootingState = ShootingState.WATING_TO_SHOOT;
					startTime = Timer.getFPGATimestamp();
				}
			break;

			case WATING_TO_SHOOT: 
				if (Timer.getFPGATimestamp() - startTime >= shootDelay) {
					shootingState = ShootingState.SHOOTING;
					indexer.setPower(1d);
				}
			break;

			case SHOOTING: 
			break;
		}
	}

	@Override
	public void end(boolean interrupted) {
		super.end(interrupted);
		indexer.stop();
		shooter.stop();
	}

	@Override
	public boolean isFinished() {
		return indexer.getEjectedBall();
	}
}
