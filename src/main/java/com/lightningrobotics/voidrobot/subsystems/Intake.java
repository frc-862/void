package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.lightningrobotics.voidrobot.Constants;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {

	
	// VictorSPX to run
	// TalonSRX to deploy

	private VictorSPX intakeMotor;//TODO: correctly set sign for motors
	private TalonSRX deployIntakeMotor;

	private final I2C.Port i2cPort = I2C.Port.kOnboard;

	private final ColorSensorV3 intakeSensor;

	public Intake() {
		intakeMotor = new VictorSPX(Constants.INTAKE_MOTOR_ID); 
		deployIntakeMotor = new TalonSRX(Constants.DEPLOY_INTAKE_MOTOR_ID); 
		intakeSensor = new ColorSensorV3(i2cPort);
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
		deployIntakeMotor.set(TalonSRXControlMode.PercentOutput, 0.7); //TODO: implement sensor input
	}

	public void retractIntake() {
		deployIntakeMotor.set(TalonSRXControlMode.PercentOutput, -0.7); //TODO: implement sensor input
	}

	// public void stopDeployIntakeMotor() {
	// 	deployIntakeMotor.set(TalonSRXControlMode.PercentOutput, 0);
	// }


	
	
	
}
