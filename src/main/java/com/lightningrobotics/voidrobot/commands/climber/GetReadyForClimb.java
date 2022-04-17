package com.lightningrobotics.voidrobot.commands.climber;

import java.lang.invoke.ConstantBootstraps;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Intake;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class GetReadyForClimb extends CommandBase {

	private final Hood hood;
	private final Turret turret;
	private final Shooter shooter;
	private final Intake intake;
	private final HubTargeting targeting;
	
	public GetReadyForClimb(Hood hood, Turret turret, Shooter shooter, Intake intake, HubTargeting targeting) {
		this.hood = hood;
		this.turret = turret;
		this.shooter  = shooter;
		this.intake = intake;
		this.targeting = targeting;

		addRequirements(hood, turret, shooter, intake);
	}

	@Override
	public void initialize() {}

	@Override
	public void execute() {
		turret.setAngle(0);
		hood.setAngle(0);
		intake.actuateIntake(Constants.DEFAULT_INTAKE_WINCH_POWER);
		shooter.stop();
	}

	@Override
	public void end(boolean interrupted) {
		turret.setDisableTurret(true);
		hood.setDisableHood(true);
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}