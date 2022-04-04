package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.lightningrobotics.voidrobot.constants.RobotMap;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {

	// Creates our intake motor
	private final VictorSPX intakeMotor;
	private final VictorSPX winchMotor;


	public Intake() {
		// Sets the ID of the intake motor
		intakeMotor = new VictorSPX(RobotMap.INTAKE_MOTOR_ID);
		intakeMotor.setInverted(false);
		winchMotor = new VictorSPX(RobotMap.INTAKE_WINCH_ID);
		winchMotor.setNeutralMode(NeutralMode.Brake);
		winchMotor.setInverted(false);
		
		CommandScheduler.getInstance().registerSubsystem(this);
	}

	public void setPower(double intakePower) {
		intakeMotor.set(VictorSPXControlMode.PercentOutput, intakePower);
	}

	public void stop() {
		intakeMotor.set(VictorSPXControlMode.PercentOutput, 0);
	}

	public void stopDeploy() {
		winchMotor.set(VictorSPXControlMode.PercentOutput, 0);
	}

	public void actuateIntake(double pwr) {
		winchMotor.set(VictorSPXControlMode.PercentOutput, pwr);
	}
}
