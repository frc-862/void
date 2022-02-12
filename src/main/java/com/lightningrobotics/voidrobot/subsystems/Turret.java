package com.lightningrobotics.voidrobot.subsystems;

import com.lightningrobotics.common.controller.PIDFController;
import com.lightningrobotics.common.subsystem.drivetrain.PIDFDashboardTuner;
import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.Constants;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Turret extends SubsystemBase {

	private final CANSparkMax turretMotor;
	private final RelativeEncoder turretEncoder;
	private final PIDFController PID = new PIDFController(Constants.TURRET_kP, 0, 0);

	private final PIDFDashboardTuner tuner = new PIDFDashboardTuner("Turret", PID);

	private Rotation2d target;

	public Turret() {
		turretMotor = new CANSparkMax(Constants.TURN_TURRET_ID, MotorType.kBrushless); // TODO: change CAN ids for both motors
		turretMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
		turretMotor.setClosedLoopRampRate(.02); // too low?
		turretEncoder = turretMotor.getEncoder();

	}
	
	@Override
	public void periodic() {
		Rotation2d constrainedAngle = Rotation2d.fromDegrees(LightningMath.constrain(target.getDegrees(), -135, 135));

		double output = PID.calculate(getTurretAngle().getDegrees(), constrainedAngle.getDegrees());

		turretMotor.set(output);
	}

	public void setTargetAngle(Rotation2d target) {
		this.target = target;
	}

	public void stopTurret() {
		turretMotor.set(0);
	}

	public Rotation2d getTurretAngle() {
		return Rotation2d.fromDegrees(getEncoderValue() * 360d / Constants.TURN_TURRET_GEAR_RATIO); // maybe divid by Constants.TURN_TURRET_GEAR_RATIO then multiply 360d also maybe divid by 2
		// double angle = (getEncoderValue() * 360 / Constants.TURN_TURRET_GEAR_RATIO);
		// if(angle > 0){
		//     angle -= 180;
		// } 
		// else if(angle < 0){
		//     angle += 180;
		// }
		// return Rotation2d.fromDegrees(angle);

		
	}

	public double getEncoderValue() {
		return turretEncoder.getPosition();
	}

}
