package com.lightningrobotics.voidrobot.commands.auto;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.commands.indexer.AutoIndexCargo;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Intake;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.commands.indexer.RunIndexer;
import com.lightningrobotics.voidrobot.commands.intake.RunIntake;
import com.lightningrobotics.voidrobot.commands.shooter.RunShooter;
import com.lightningrobotics.voidrobot.constants.Constants;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutonShoot2 extends CommandBase {

	private Indexer indexer;
	private Shooter shooter;
	private Turret turret;

	private double RPM;
	private double hoodAngle;
	private double turretAngle;

	private double startTime;
	private final double shootDelay;

	enum ShootingState {
		WATING_TO_ARM,
		WATING_TO_SHOOT,
		SHOOTING
	}
	ShootingState shootingState; 

	public AutonShoot2(Indexer indexer, Shooter shooter, Turret turret, double shootDelay, double RPM, double hoodAngle, double turretAngle) {
		this.indexer = indexer;
		this.shooter = shooter;
		this.turret = turret;
		this.RPM = RPM;
		this.turretAngle = turretAngle;
		this.shootDelay = shootDelay;

		addRequirements(indexer, shooter, turret);
	}

	@Override
	public void initialize() {
		shooter.setRPM(RPM);
		shootingState = ShootingState.WATING_TO_ARM;
        setTurretAngle(turretAngle);
		shooter.setHoodAngle(hoodAngle);
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

    public void setTurretAngle(double targetAngle) {
		double motorOutput = 0;

		double sign = Math.signum(targetAngle);
        targetAngle =  sign * (((Math.abs(targetAngle) + 180) % 360) - 180);

        double constrainedAngle = LightningMath.constrain(targetAngle, Constants.MIN_TURRET_ANGLE, Constants.MAX_TURRET_ANGLE);

        motorOutput = Constants.TURRET_PID_FAST.calculate(turret.getCurrentAngle().getDegrees(), constrainedAngle);
        

		// Set Motor Output
        motorOutput *= 0.1;
        turret.setPower(motorOutput);
	}

	@Override
	public void end(boolean interrupted) {
		super.end(interrupted);
		indexer.stop();
		shooter.stop();
        turret.stop();
	}

	@Override
	public boolean isFinished() {
		return indexer.getBallCount() == 0;
	}
}
