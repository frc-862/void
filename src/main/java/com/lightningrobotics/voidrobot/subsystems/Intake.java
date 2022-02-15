package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.lightningrobotics.voidrobot.Constants;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {

	private VictorSPX intakeMotor;


	public Intake() {
		intakeMotor = new VictorSPX(Constants.INTAKE_MOTOR_ID); 
		// TODO correctly set inverts for motors
	}

	@Override
	public void periodic() {
	}

	public void runIntake(double intakePower) {
		intakeMotor.set(VictorSPXControlMode.PercentOutput, intakePower);
	}

	public void stopIntake() {
		intakeMotor.set(VictorSPXControlMode.PercentOutput, 0);
	}

	public void deployIntake() {
		// TODO figure out how to deploy intake
	}

	public void retractIntake() {
		// TODO figure out how to retract intake
	}

}
