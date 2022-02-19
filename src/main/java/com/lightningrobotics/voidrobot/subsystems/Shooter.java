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
	private VictorSPX flywheelMotor;//TODO: correctly set invert for motors
	private TalonSRX hoodMotor;
	private Encoder shooterEncoder;

	private ShuffleboardTab shooterTab = Shuffleboard.getTab("shooter test");
    private NetworkTableEntry displayRPM;
    private NetworkTableEntry setRPM;
    private NetworkTableEntry shooterPower;

	private PIDFController pid = new PIDFController(Constants.SHOOTER_KP, Constants.SHOOTER_KI, Constants.SHOOTER_KD);
	private FeedForwardController feedForward = new FeedForwardController(Constants.SHOOTER_KS,  Constants.SHOOTER_KV, Constants.SHOOTER_KA);

	private PIDFDashboardTuner tuner = new PIDFDashboardTuner("shooter test", pid);

	private double powerSetPoint;
	private double targetRPM;
	private static boolean armed;

	public Shooter() {
		flywheelMotor = new VictorSPX(Constants.FLYWHEEL_MOTOR_ID);
		hoodMotor = new TalonSRX(Constants.HOOD_MOTOR_ID);
		shooterEncoder = new Encoder(9, 8);

		flywheelMotor.setInverted(true);

		shooterEncoder.setDistancePerPulse(1d/2048d); //encoder ticks per rev (or, the other way around)

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

	public double getEncoderRPM() {
		return shooterEncoder.getRate() * 60; //converts from revs per second to revs per minute
	}

	public double getEncoderDist() {
		return shooterEncoder.getDistancePerPulse();
	}

	public double currentEncoderTicks() {
		return shooterEncoder.getRaw(); 
	}

	public void setRPM(double targetRPM) {
		this.targetRPM = targetRPM;
		targetRPM = feedForward.calculate(targetRPM); // maybe not??
		powerSetPoint = pid.calculate(getEncoderRPM(), targetRPM);
		setPower(powerSetPoint);
	}

	/**
	 * Checks if flywheel RPM is within a threshold
	 */
	public void setArmed() {
		armed = Math.abs(getEncoderRPM() - targetRPM) < 50;
	}

	/**
	 * 
	 * @return Whether flywheel RPM is within a threshold and ready to shoot
	 */
	public boolean getArmed() {
		return armed;	
	}

	public double getPowerSetpoint() {
		return powerSetPoint;
	}

	public void setSmartDashboardCommands() {
		displayRPM.setDouble(getEncoderRPM());
		shooterPower.setDouble(getPowerSetpoint());
	}

	public double getRPMFromDashboard() {
		return setRPM.getDouble(0);
	}

	@Override
	public void periodic() {
		setSmartDashboardCommands();
		setArmed(); // Checks to see the shooter is at desired RPMS
	}

}
