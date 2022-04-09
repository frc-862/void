package com.lightningrobotics.voidrobot.commands.auto.commands;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Intake;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutonDeployIntake extends CommandBase {

	private final Intake intake;

	public AutonDeployIntake(Intake intake) {
		this.intake = intake;
	}

	@Override
	public void initialize() {
	}

	@Override
	public void execute() {
		intake.actuateIntake(Constants.DEFAULT_INTAKE_WINCH_POWER);
	}

	@Override
	public void end(boolean interrupted) {
		intake.stopDeploy();
	}

}
