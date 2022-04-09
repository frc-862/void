package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.lightningrobotics.voidrobot.constants.RobotMap;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {

	// Creates our intake motor
	private final VictorSPX intakeMotor;
	private final VictorSPX winchMotor;

	private final DigitalInput bumperSensor;
	private final DigitalInput deployedSensor;

	public Intake() {
		// Sets the ID of the intake motor
		intakeMotor = new VictorSPX(RobotMap.INTAKE_MOTOR_ID);
		intakeMotor.setInverted(false);
		winchMotor = new VictorSPX(RobotMap.INTAKE_WINCH_ID);
		winchMotor.setNeutralMode(NeutralMode.Brake);
		winchMotor.setInverted(true);

		bumperSensor = new DigitalInput(RobotMap.INTAKE_BUMPER_SENSOR);
		deployedSensor = new DigitalInput(RobotMap.INTAKE_DEPLOYED_SENSOR);
		
		CommandScheduler.getInstance().registerSubsystem(this);
	}

	@Override
	public void periodic() {}

	public boolean getBumperSensor(){
		return bumperSensor.get();
	}

	public boolean getDeployedSensor(){
		return bumperSensor.get();
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
