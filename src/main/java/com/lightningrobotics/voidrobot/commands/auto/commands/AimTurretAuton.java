package com.lightningrobotics.voidrobot.commands.auto.commands;

import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AimTurretAuton extends CommandBase {

    protected final Turret turret;
	protected final HubTargeting targeting;

    public AimTurretAuton(Turret turret, HubTargeting targeting) {
        this.turret = turret;
		this.targeting = targeting;

        addRequirements(turret); // HubTargeting is read only
    }

	@Override
	public void initialize() {}

    @Override
    public void execute() {
		var target_angle = targeting.calcTurretAngle();
		turret.setAngle(target_angle);
	}

	@Override
    public void end(boolean interrupted) {
        turret.stop();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}