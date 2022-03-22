package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.commands.hood.ResetHood;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ZeroTurretHood extends CommandBase {

	private final Hood hood;
	private final Turret turret;
	private boolean endBoolean = false;

	public ZeroTurretHood(Hood hood, Turret turret) {
		this.hood = hood;
		this.turret = turret;

		addRequirements(hood, turret);
	}

	@Override
	public void initialize() {}

	@Override
	public void execute() {
		turret.setAngle(0);
		hood.setPower(-0.2);
		if(hood.resetHoodSensorTriggered()) {
		  hood.setPower(0);
		  (new ResetHood(hood)).schedule();
		  hood.readZero();
	
		  endBoolean = true;
		}
	}

	@Override
	public void end(boolean interrupted) {
	}

	@Override
	public boolean isFinished() {
		return endBoolean;
	}
	


}
