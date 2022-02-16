package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.lightningrobotics.common.controller.FeedForwardController;
import com.lightningrobotics.common.controller.PIDFController;
import com.lightningrobotics.common.subsystem.drivetrain.PIDFDashboardTuner;
import com.lightningrobotics.util.InterpolatedMap;
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

	private InterpolatedMap flywheelSpeedInterpolationTable  = new InterpolatedMap();

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

		configureShooterCurve();

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

	private void configureShooterCurve() {
		for (double distance: Constants.DISTANCE_RPM_MAP.keySet()) {
			flywheelSpeedInterpolationTable.put(distance, Constants.DISTANCE_RPM_MAP.get(distance));
		}
	}

	/**
	 * gets the optimal shooter RPM from an inputted height in pixels using an interpolation map
	 * @param height in pixels
	 * @return motor RPMs
	 */
	public double getRPMsFromHeight(double height) {
		if (height > 0) {
            return flywheelSpeedInterpolationTable.get(height);
        } else {
            return 0;
        }
	}

	@Override
	public void periodic() {
		setSmartDashboardCommands();
	}

}
