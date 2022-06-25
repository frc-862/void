package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {

	// Creates our intake motor
	private final TalonFX intakeMotor;
	private final TalonSRX winchMotor;

	public Intake() {
		// Sets the ID of the intake motor
		intakeMotor = new TalonFX(RobotMap.INTAKE_MOTOR_ID);
		intakeMotor.setInverted(true);
		winchMotor = new TalonSRX(RobotMap.INTAKE_WINCH_ID);
		winchMotor.setNeutralMode(NeutralMode.Brake);
		winchMotor.setInverted(false);

		CommandScheduler.getInstance().registerSubsystem(this);
	}

	@Override
	public void periodic() {
		SmartDashboard.putBoolean("bumber limit swtich", getBumperSensor());
		SmartDashboard.putBoolean("deployed litmit swtich", getDeployedSensor());

	}

	public boolean getBumperSensor(){
		return winchMotor.isRevLimitSwitchClosed() == 1;
	}

	public boolean getDeployedSensor(){
		return winchMotor.isFwdLimitSwitchClosed() == 1;
	}

	public void setPower(double intakePower) {
		intakeMotor.set(TalonFXControlMode.PercentOutput, intakePower * 0.5d);
	}

	public void stop() {
		intakeMotor.set(TalonFXControlMode.PercentOutput, 0);
	}

	public void stopDeploy() {
		winchMotor.set(TalonSRXControlMode.PercentOutput, 0);
	}

	public void actuateIntake(double pwr) {
		winchMotor.set(TalonSRXControlMode.PercentOutput, pwr);
	}
}
