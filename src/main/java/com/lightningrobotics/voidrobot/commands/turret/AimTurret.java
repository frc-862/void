package com.lightningrobotics.voidrobot.commands.turret;

import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AimTurret extends CommandBase {

    private final Turret turret;
	private final HubTargeting targeting;

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

		if (zero) {
			turret.setAngle(0);
		} else {
			var targetAngle = targeting.getTargetTurretAngle();
			turret.setAngle(targetAngle);
		}
    }

	public static void turnZeroOn() {
		zero = true;
	}

	public static void turnZeroOff() {
		zero = false;
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