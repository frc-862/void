package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.lightningrobotics.common.controller.FeedForwardController;
import com.lightningrobotics.common.controller.PIDFController;
import com.lightningrobotics.common.subsystem.drivetrain.PIDFDashboardTuner;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import com.lightningrobotics.voidrobot.constants.Constants;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Encoder;
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
    private NetworkTableEntry setDashboardRPM;
    private NetworkTableEntry shooterPower;

	// Creates a PID and FeedForward controller for our shooter
	private PIDFController pid = new PIDFController(Constants.SHOOTER_KP, Constants.SHOOTER_KI, Constants.SHOOTER_KD);
	private FeedForwardController feedForward = new FeedForwardController(Constants.SHOOTER_KS,  Constants.SHOOTER_KF, Constants.SHOOTER_KA);

	// PID tuner for the shooter gains
	private PIDFDashboardTuner tuner = new PIDFDashboardTuner("shooter test", pid);

	// The power point we want the shooter to be at
	private double powerSetPoint;

	public Shooter() {
		// Sets the IDs of the hood and shooter
		flywheelMotor = new TalonFX(RobotMap.FLYWHEEL_MOTOR_ID);
		hoodMotor = new TalonSRX(RobotMap.HOOD_MOTOR_ID);

		flywheelMotor.setInverted(true); // Inverts the flywheel motor
		// flywheelMotor.config_kF(0, value);

		changePIDGains(Constants.SHOOTER_KP, Constants.SHOOTER_KI, Constants.SHOOTER_KD, Constants.SHOOTER_KF);

		// Creates the tables to see important values
		shooterPower = shooterTab
			.add("shooter power output", getPowerSetpoint())
			.getEntry();
		setDashboardRPM = shooterTab
			.add("set RPM", 0)
			.getEntry(); 
		displayRPM  = shooterTab
			.add("RPM-From encoder", 0)
			.getEntry();

	}

	public PIDFController getPIDFController(){
		return pid;
	}
	
	public FeedForwardController getFeedForwardController(){
		return feedForward;
	}

	public void setPower(double power) {
		//TODO: use falcon built-in functions
		flywheelMotor.set(TalonFXControlMode.PercentOutput, power); 
	}

	public void setVelocity(double shooterVelocity) {
		//TODO: use falcon built-in functions
		flywheelMotor.set(TalonFXControlMode.Velocity, shooterVelocity); 
	}

	public void stop() {
		flywheelMotor.set(TalonFXControlMode.PercentOutput, 0);; 
	}

	public void hoodMove(double moveAmount) {
		hoodMotor.set(TalonSRXControlMode.PercentOutput, moveAmount);
		//TODO: add logic to actually increment
	}

	public double getEncoderRPMs() {
		return flywheelMotor.getSelectedSensorVelocity() / 2048 * 600; //converts from revs per second to revs per minute
	}

	public double currentEncoderTicks() {
		return flywheelMotor.getSelectedSensorPosition();
	}

	public void setRPM(double targetRPMs) {
		// targetRPMs = feedForward.calculate(targetRPMs); // maybe not??
		// powerSetPoint = pid.calculate(getEncoderRPMs(), targetRPMs);
		setVelocity(targetRPMs / 600 * 2048);
	}

	public double getPowerSetpoint() {
		return powerSetPoint;
	}

	public void setSmartDashboardCommands() {
		displayRPM.setDouble(getEncoderRPMs());
		shooterPower.setDouble(getPowerSetpoint());
	}

	public double getRPMsFromDashboard() {
		return setDashboardRPM.getDouble(0);
	}

	private void changePIDGains(double kP, double kI, double kD, double kV) {
		flywheelMotor.config_kP(0, kP);
		flywheelMotor.config_kI(0, kI);
		flywheelMotor.config_kD(0, kD);
		flywheelMotor.config_kF(0, kV);
	}

	public boolean isOnTarget() {
		if(Math.abs(pid.getP()) < 100) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void periodic() {
		setRPM(getRPMsFromDashboard());
		setSmartDashboardCommands();
	}

}
