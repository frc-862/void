package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ShootOTF extends CommandBase {

	private final Shooter shooter;
	private final Hood hood;
	private final Indexer indexer;
	private final HubTargeting targeting;
	private final Drivetrain drivetrain;

	public ShootOTF(Shooter shooter, Hood hood, Indexer indexer, HubTargeting targeting, Drivetrain drivetrain) {
		this.shooter = shooter;
		this.hood = hood;
		this.indexer = indexer;
		this.targeting = targeting;
		this.drivetrain = drivetrain;

		addRequirements(shooter, hood, indexer);
	}

	// Some super cool stuff from 846's shooter physics video

	/**
	 * Calculate the surface speed of an object given it's angulat velocity
	 * @param RPM the RPM of the object
	 * @param diameter the diameter of the object
	 * @return the surface speed of the object
	 */
	private double calculateSurfaceSpeed(double RPM, double diameter) {
        return RPM * (diameter / 2);
    }

	private double getBallExitVelocity(double ballMass, double wheelMass) {
        return 1 + (1.4 / (wheelMass / ballMass)) / 2 * calculateSurfaceSpeed(targeting.getTargetFlywheelRPM() / 60, Constants.FLYWHEEL_DIAMETER);
    }


	/**
	 * @return the robot's velocity on the X axis
	 */
	private double getXvelocity() {
		return 0d; //TODO: hack lightning to give x and y velocities from lightning IMU
	}

	/**
	 * @return the robot's velocity on the Y axis
	 */
	private double getYvelocity() {
		return 0d;
	}

	/**
	 * @return the time it will take the ball to reach the hub
	 */
	private double getBallTravelTime() {
		return targeting.getHubDistance()/(getBallExitVelocity(Constants.CARGO_MASS, Constants.SHOOTER_MASS)*Math.cos(targeting.getTargetHoodAngle()));
	}

	/**
	 * @return Calculate how far the robot will move on the X axis the time it takes the ball to travel to the hub
	 */
	private double getBotTravelDistanceX() {
		return getXvelocity()*getBallTravelTime();
	}

	/**
	 * @return how far the robot will move on the Y axis the time it takes the ball to travel to the hub
	 */
	private double getBotTravelDistanceY() {
		return getYvelocity()*getBallTravelTime();
	}

	/**
	 * @return the offset to apply to the turret to account for the robot's movement
	 */
	private double getTurretOffset() {
		double initialTurretAngle = targeting.getTargetTurretAngle();
		double hubDistance = targeting.getHubDistance();
		double botOffsetX = getBotTravelDistanceX();
		double botOffsetY = getBotTravelDistanceY();
		double a = hubDistance*Math.sin(initialTurretAngle);
		double b = hubDistance*Math.cos(initialTurretAngle);

		a -= botOffsetX;
		b -= botOffsetY;

		return Math.atan2(a, b) - initialTurretAngle;
	}

	/**
	 * @return the distance offset that accounts for the robot's movement
	 */
	private double getDistanceOffset() {
		double initialTurretAngle = targeting.getTargetTurretAngle();
		double hubDistance = targeting.getHubDistance();
		double botOffsetX = getBotTravelDistanceX();
		double botOffsetY = getBotTravelDistanceY();
		double a = hubDistance*Math.sin(initialTurretAngle);
		double b = hubDistance*Math.cos(initialTurretAngle);

		a -= botOffsetX;
		b -= botOffsetY;
		
		return Math.hypot(a, b) - targeting.getHubDistance();
	}

	@Override
	public void execute() {
		targeting.setBiasAngle(getTurretOffset());

		targeting.setBiasDistance(getDistanceOffset());

		boolean isEnenmyBall = indexer.isEnenmyBall();
		var rpm =  isEnenmyBall ? Constants.EJECT_BALL_RPM : targeting.getTargetFlywheelRPM();
		var hoodAngle = isEnenmyBall ? Constants.EJECT_BALL_HOOD_ANGLE : targeting.getTargetHoodAngle();

		shooter.setRPM(rpm);
		hood.setAngle(hoodAngle);
		
		if (drivetrain.getCurrentVelocity() < Constants.MAXIMUM_LINEAR_SPEED_TO_SHOOT && targeting.onTarget()) { // getCurrentVelocity() may not work, may need another constant
			indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
		} 
	}

	@Override
	public void end(boolean interrupted) {
		shooter.coast();
		indexer.stop();
		hood.setAngle(0); // TODO: see if eric likes -nick
		// if (indexer.getBallCount() == 0) {
		// 	hood.setAngle(0);
		// } else {
		// 	hood.setPower(0);
		// }
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
}
