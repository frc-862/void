package com.lightningrobotics.voidrobot.commands.shooter;

import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.common.util.filter.MovingAverageFilter;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Indexer.BallColor;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class StopAndShoot extends CommandBase {

	private final Shooter shooter;
	private final Hood hood;
	private final Indexer indexer;
	private final HubTargeting targeting;
	private final Drivetrain drivetrain;
	private final LightningIMU imu;
	private final MovingAverageFilter mafX = new MovingAverageFilter(2);
	private final MovingAverageFilter mafY = new MovingAverageFilter(2);
	private final MovingAverageFilter mafZ = new MovingAverageFilter(2);

	private double enenmyOurTimer = 0;
	private final double STOPPING_TOLERANCE = 0.25d;
	private final double ENENMY_OUR_WAIT_TIME = 0.4d;

	public StopAndShoot(Shooter shooter, Hood hood, Indexer indexer, HubTargeting targeting, Drivetrain drivetrain, LightningIMU imu) {
		this.shooter = shooter;
		this.hood = hood;
		this.indexer = indexer;
		this.targeting = targeting;
		this.drivetrain = drivetrain;
		this.imu = imu;

		addRequirements(shooter, hood, indexer, drivetrain);
	}

	@Override
	public void execute() {
		drivetrain.pidStop();

		var allianceBallColor = DriverStation.getAlliance().toString();
		boolean isEnenmyBall = indexer.isEnenmyBall();

		var getRPM = isEnenmyBall ? Constants.EJECT_BALL_RPM : targeting.getTargetFlywheelRPM();
		var getHoodAngle = isEnenmyBall ? Constants.EJECT_BALL_HOOD_ANGLE : targeting.getTargetHoodAngle();

		var setRPM = getRPM;
		var setHoodAngle = getHoodAngle;
		
		var accelX = mafX.filter(imu.getNavxAccelerationX());
		var accelY = mafY.filter(imu.getNavxAccelerationY());
		var accelZ = mafZ.filter(imu.getNavxAccelerationZ());

		var isXStopping = Math.abs(accelX) < STOPPING_TOLERANCE;
		var isYStopping = Math.abs(accelY) - 1  < STOPPING_TOLERANCE;
		var isZStopping = Math.abs(accelZ) < STOPPING_TOLERANCE;

		shooter.setRPM(setRPM);
		hood.setAngle(setHoodAngle);
		SmartDashboard.putNumber("IMU X acc", imu.getNavxAccelerationX());
		SmartDashboard.putNumber("IMU Y acc", imu.getNavxAccelerationY());
		SmartDashboard.putNumber("IMU Z acc", imu.getNavxAccelerationZ());
		SmartDashboard.putBoolean("IMU X stopped", isXStopping);
		SmartDashboard.putBoolean("IMU Y stopped", isYStopping);
		SmartDashboard.putBoolean("IMU Z stopped", isZStopping);

		if (drivetrain.getCurrentVelocity() < Constants.MAXIMUM_LINEAR_SPEED_TO_SHOOT && targeting.onTarget() && isXStopping && isYStopping && isZStopping){
			indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
		} 
	}

	@Override
	public void end(boolean interrupted) {
		shooter.coast();
		indexer.stop();
		hood.setAngle(0);
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
