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

	// Some super cool stuff ported from "Julia's Design calculator" (a fork of JVN design calculator)

	/**
	 * calculates the moment of inertia of the flywheel
	 * @param shooterMass the total mass of each spinning element, in lbs
	 * @param diameter the diameter of the flywheel, in inches
	 * @return the moment of inertia, in lb-in^2
	 */
	private double calculateMOI(double shooterMass, double diameter) {
		//formula is 0.5mR^2
		return 0.5 * shooterMass * Math.pow(diameter / 2, 2);
	}

	/**
	 * calculates the percent of the flywheel's speed that is transferred to the ball
	 * @param ballDiameter the diameter of the ball, in inches
	 * @param ballMass the mass of the ball, in lbs
	 * @param wheelDiameter the diameter of the flywheel, in inches
	 * @param MOI the moment of inertia of the shooter, in lb-in^2
	 * @return the percent of the flywheel's speed that is transferred to the ball
	 */
	private double calculateSpeedTransfer(double ballDiameter, double ballMass, double wheelDiameter, double MOI) {
		return 1/(2+ballMass*Math.pow(wheelDiameter/2,2)/MOI+(2/5)*ballMass*Math.pow(ballDiameter/2,2)/MOI*Math.pow(wheelDiameter/ballDiameter,2));
	}

	/**
	 * convert rotations per minute to radians per second
	 * @param RPM input rotations per minute
	 * @return output radians per second
	 */
	private double RPMtoRPS(double RPM) {
		return ((RPM/60) * 2 * Math.PI); 
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
	 * @return the exit velocity of the ball from the shooter
	 */
	private double getBallExitVelocity() {
		double MOI = calculateMOI(Constants.SHOOTER_MASS, Constants.FLYWHEEL_DIAMETER);
		double SPEED_TRANSER = calculateSpeedTransfer(Constants.CARGO_DIAMETER, Constants.CARGO_MASS, Constants.FLYWHEEL_DIAMETER, MOI);
		
		return (RPMtoRPS(targeting.getTargetFlywheelRPM()*Constants.FLYWHEEL_DIAMETER/2)/12)*SPEED_TRANSER;
	}

	/**
	 * @return the time it will take the ball to reach the hub
	 */
	private double getBallTravelTime() {
		return targeting.getHubDistance()/(getBallExitVelocity()*Math.cos(targeting.getTargetHoodAngle()));
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
