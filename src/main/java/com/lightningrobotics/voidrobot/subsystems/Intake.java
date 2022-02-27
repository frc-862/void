package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {

	// Creates our intake motor
	private final VictorSPX intakeMotor;
	private final TalonSRX winch;

	public Intake() {
		// Sets the ID of the intake motor
		intakeMotor = new VictorSPX(RobotMap.INTAKE_MOTOR_ID);
		winch = new TalonSRX(RobotMap.INTAKE_WINCH_ID);
		winch.setNeutralMode(NeutralMode.Brake);
		// TODO set inverts on winch/other
	}

	public void setPower(double intakePower) {
		intakeMotor.set(VictorSPXControlMode.PercentOutput, intakePower);
	}

	public void stop() {
		intakeMotor.set(VictorSPXControlMode.PercentOutput, 0);
	}

	public void actuateIntake(double pwr) {
		winch.set(ControlMode.PercentOutput, pwr);
	}

}
