package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.lightningrobotics.common.subsystem.drivetrain.PIDFDashboardTuner;
import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import com.lightningrobotics.voidrobot.constants.Constants;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Shooter extends SubsystemBase {

	// Creates the flywheel motor and hood motors
	private TalonFX flywheelMotor;
	private TalonSRX hoodMotor;

	// Creates our shuffleboard tabs for seeing important values
	private ShuffleboardTab shooterTab = Shuffleboard.getTab("shooter test");
    private NetworkTableEntry displayRPM;
    private NetworkTableEntry setRPM;
    private NetworkTableEntry displayShooterPower;
	private NetworkTableEntry currentHoodAngle;

	// PID tuner for the shooter gains
	private PIDFDashboardTuner hoodTuner = new PIDFDashboardTuner("hood test", Constants.HOOD_PID);

	// The power point we want the shooter to be at
	private double shooterPower;
	private double hoodPowerSetPoint;
	private double targetRPM;
	private double hoodAngle;
	private static boolean armed;
	
	public Shooter() {

		// Sets the IDs of the hood and shooter
		flywheelMotor = new TalonFX(RobotMap.FLYWHEEL_MOTOR_ID);
		hoodMotor = new TalonSRX(RobotMap.HOOD_MOTOR_ID);
		hoodMotor.configSelectedFeedbackSensor(FeedbackDevice.Analog);

		flywheelMotor.setInverted(true); // Inverts the flywheel motor

		configPIDGains(Constants.SHOOTER_KP, Constants.SHOOTER_KI, Constants.SHOOTER_KD, Constants.SHOOTER_KF);

		// Creates the tables to see important values
		displayShooterPower = shooterTab
			.add("shooter power output", getShooterPower())
			.getEntry();
		setRPM = shooterTab
			.add("set RPM", 0)
			.getEntry(); 
		displayRPM  = shooterTab
			.add("RPM-From encoder", 0)
			.getEntry();
		currentHoodAngle  = shooterTab
			.add("hood angle", 0)
			.getEntry();

	}

	public double getHoodAngle() {
		return hoodMotor.getSelectedSensorPosition() / 4096 * 360; // Should retrun the angle; maybe 4096
	}

	public void setHoodAngle(double hoodAngle) {
		this.hoodAngle = LightningMath.constrain(hoodAngle, Constants.MIN_HOOD_ANGLE, Constants.MAX_HOOD_ANGLE);
		hoodPowerSetPoint = Constants.HOOD_PID.calculate(getHoodAngle(), this.hoodAngle);
		hoodMotor.set(TalonSRXControlMode.PercentOutput, hoodPowerSetPoint);
	}

	public void setHoodPower(double power) {
		hoodMotor.set(TalonSRXControlMode.PercentOutput, power);
	}

	public void setPower(double power) {
		flywheelMotor.set(TalonFXControlMode.PercentOutput, power); 
	}

	public void setVelocity(double shooterVelocity) {
		flywheelMotor.set(TalonFXControlMode.Velocity, shooterVelocity); 
	}

	public void stop() {
		flywheelMotor.set(TalonFXControlMode.PercentOutput, 0);; 
	}

	public void hoodMove(double moveAmount) {
		hoodMotor.set(TalonSRXControlMode.PercentOutput, moveAmount);
		//TODO: add logic to actually increment
	}

	public double getEncoderRPM() {
		return flywheelMotor.getSelectedSensorVelocity() / 2048 * 600; //converts from revs per second to revs per minute
	}

	public void setRPM(double targetRPMs) {
		setVelocity(targetRPMs / 600 * 2048);
	}

	public double getShooterPower() {
		return shooterPower;
	}

	public double currentEncoderTicks() {
		return flywheelMotor.getSelectedSensorPosition();
	}

	// Checks if flywheel RPM is within a threshold
	public void setArmed() {
		armed = Math.abs(getEncoderRPM() - targetRPM) < 50;
	}

	// Whether flywheel RPM is within a threshold and ready to shoot
	public boolean getArmed() {
		return armed;	
	}

	// Update Displays on Dashboard
	public void setSmartDashboardCommands() {
		displayRPM.setDouble(getEncoderRPM());
		displayShooterPower.setDouble(getShooterPower());
		currentHoodAngle.setDouble(getHoodAngle());
	}

	private void configPIDGains(double kP, double kI, double kD, double kV) {
		flywheelMotor.config_kP(0, kP);
		flywheelMotor.config_kI(0, kI);
		flywheelMotor.config_kD(0, kD);
		flywheelMotor.config_kF(0, kV);
	}

	public double getRPMFromDashboard() {
		return setRPM.getDouble(0);
	}

	@Override
	public void periodic() {
		// setRPM(getRPMFromDashboard());
		setSmartDashboardCommands();
	}

}
