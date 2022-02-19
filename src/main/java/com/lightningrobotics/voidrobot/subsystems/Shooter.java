package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.lightningrobotics.common.controller.FeedForwardController;
import com.lightningrobotics.common.controller.PIDFController;
import com.lightningrobotics.common.subsystem.drivetrain.PIDFDashboardTuner;
import com.lightningrobotics.voidrobot.Constants;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Shooter extends SubsystemBase {
	// Creates the flywheel motor and hood motors
	private VictorSPX flywheelMotor;
	private TalonSRX hoodMotor;
	private Encoder shooterEncoder;

	// Creates our shuffleboard tabs for seeing important values
	private ShuffleboardTab shooterTab = Shuffleboard.getTab("shooter test");
    private NetworkTableEntry displayRPM;
    private NetworkTableEntry setRPM;
    private NetworkTableEntry shooterPower;

	// Creates a PID and FeedForward controller for our shooter
	private PIDFController pid = new PIDFController(Constants.SHOOTER_KP, Constants.SHOOTER_KI, Constants.SHOOTER_KD);
	private FeedForwardController feedForward = new FeedForwardController(Constants.SHOOTER_KS,  Constants.SHOOTER_KV, Constants.SHOOTER_KA);

	// PID tuner for the shooter gains
	private PIDFDashboardTuner tuner = new PIDFDashboardTuner("shooter test", pid);

	// The power point we want the shooter to be at
	private double powerSetPoint;

	public Shooter() {
		// Sets the IDs of the hood and shooter
		flywheelMotor = new VictorSPX(Constants.FLYWHEEL_MOTOR_ID);
		hoodMotor = new TalonSRX(Constants.HOOD_MOTOR_ID);
		shooterEncoder = new Encoder(9, 8); // sets encoder ports

		flywheelMotor.setInverted(true); // Inverts the flywheel motor

		shooterEncoder.setDistancePerPulse(1d/2048d); // encoder ticks per rev (or, the other way around)

		// Creates the tables to see important values
		shooterPower = shooterTab
			.add("shooter power output", getPowerSetpoint())
			.getEntry();
		setRPM = shooterTab
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
		flywheelMotor.set(VictorSPXControlMode.PercentOutput, power); 
	}

	public void setVelocity(double shooterVelocity) {
		//TODO: use falcon built-in functions
		flywheelMotor.set(VictorSPXControlMode.Velocity, shooterVelocity); 
	}

	public void stop() {
		flywheelMotor.set(VictorSPXControlMode.PercentOutput, 0);; 
	}

	public void hoodMove(double moveAmount) {
		hoodMotor.set(TalonSRXControlMode.PercentOutput, moveAmount);
		//TODO: add logic to actually increment
	}

	public double getEncoderRPMs() {
		return shooterEncoder.getRate() * 60; //converts from revs per second to revs per minute
	}

	public double getEncoderDist() {
		return shooterEncoder.getDistancePerPulse();
	}

	public double currentEncoderTicks() {
		return shooterEncoder.getRaw(); 
	}

	public void setRPM(double targetRPMs) {
		targetRPMs = feedForward.calculate(targetRPMs); // maybe not??
		powerSetPoint = pid.calculate(getEncoderRPMs(), targetRPMs);
		setPower(powerSetPoint);
	}

	public double getPowerSetpoint() {
		return powerSetPoint;
	}

	public void setSmartDashboardCommands() {
		displayRPM.setDouble(getEncoderRPMs());
		shooterPower.setDouble(getPowerSetpoint());
	}

	public double getRPMsFromDashboard() {
		return setRPM.getDouble(0);
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
		setSmartDashboardCommands();
	}

}
