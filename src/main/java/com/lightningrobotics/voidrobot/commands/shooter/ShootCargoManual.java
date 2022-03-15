package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ShootCargoManual extends CommandBase {

	private Shooter shooter;
	private Vision vision;
	private Indexer indexer;
	private Turret turret;

	private static double rpm;
	private static double distance;
	private static double hoodAngle;

	public ShootCargoManual(Shooter shooter, Indexer indexer, Turret turret, Vision vision) {
		this.shooter = shooter;
		this.indexer = indexer;
		this.vision = vision;
		this.turret = turret;

		addRequirements(shooter, indexer); // not adding vision or turret as it is read only

	}

	@Override
	public void execute() {

		

		// } else { //if no vision
			shooter.setRPM(Constants.SHOOT_TARMAC_RPM);	
			shooter.setHoodAngle(Constants.SHOOT_TARMAC_ANGLE);
			System.out.println("NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO Vision");
			
		// }
			
		
		indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
	

		SmartDashboard.putNumber("hood angle from map", Constants.HOOD_ANGLE_MAP.get(distance));
	}

	@Override
	public void end(boolean interrupted) {
		shooter.stop();
		indexer.stop();
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
}
