package com.lightningrobotics.voidrobot.commands.turret;

import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AimTurret extends CommandBase {

    protected final Turret turret;
	protected final HubTargeting targeting;

	private static boolean zero = false;

    public AimTurret(Turret turret, HubTargeting targeting) {
        this.turret = turret;
		this.targeting = targeting;
        addRequirements(turret); // HubTargeting is read only
    }

	@Override
	public void initialize() {
		zero = false;
	}

    @Override
    public void execute() {
		var targetAngle = targeting.getTargetTurretAngle();
		turret.setAngle(targetAngle);
			
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