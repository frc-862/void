package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.wpilibj2.command.CommandBase;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class ZeroTurretHood extends CommandBase {

	private Hood hood;
	private Turret turret;

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
		var motorOutput = turret.getMotorOutput(turret.getTarget());
		turret.setPower(motorOutput);
		hood.setAngle(0);
	}

	@Override
	public void end(boolean interrupted) {
		turret.setPower(0);
		hood.setPower(0);
	}

	@Override
    public boolean isFinished() {
        return turret.onTarget() && hood.onTarget();
    }
	
}
