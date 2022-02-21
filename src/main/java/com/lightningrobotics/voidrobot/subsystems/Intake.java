package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import com.lightningrobotics.voidrobot.constants.Constants;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {

	// Creates our intake motor
	private final VictorSPX intakeMotor;

	public Intake() {
		// Sets the ID of the intake motor
		intakeMotor = new VictorSPX(RobotMap.INTAKE_MOTOR_ID);
		// TODO correctly set inverts for motors
	}

	@Override
	public void periodic() {
	}

	public void setPower(double intakePower) {
		intakeMotor.set(VictorSPXControlMode.PercentOutput, intakePower);
	}

	public void stop() {
		intakeMotor.set(VictorSPXControlMode.PercentOutput, 0);
	}

	public void deployIntake() {
		// TODO figure out how to deploy intake
	}

	public void retractIntake() {
		// TODO figure out how to retract intake
	}

}
