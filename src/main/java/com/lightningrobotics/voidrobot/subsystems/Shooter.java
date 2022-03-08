package com.lightningrobotics.voidrobot.subsystems;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.sound.midi.SysexMessage;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import com.lightningrobotics.voidrobot.constants.Constants;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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
	private NetworkTableEntry targetHoodAngle;
	private NetworkTableEntry currentHoodAngle;
	private NetworkTableEntry hasShotShuffEntry;

	// For reading robot_constants json
	Scanner sc;
	File robotConstantsFile = Paths.get("/home/lvuser/robot_constants/", "robot_constants.txt").toFile();
	private double hoodOffset = 15;

	// The power point we want the shooter to be at
	private double shooterPower;
	private double hoodPowerSetPoint;
	private double targetRPM;
	private double hoodAngle;
	private double prevTarget;
	private double currentTarget;
	private double startTime = 0;
	private boolean hasShot = false;
	
	private static ShuffleboardTab driverView = Shuffleboard.getTab("Competition");
	private static NetworkTableEntry shooterArmedEntry = driverView.add("Shooter armed", false).getEntry();

	public Shooter() {

		// Sets the IDs of the hood and shooter
		flywheelMotor = new TalonFX(RobotMap.FLYWHEEL_MOTOR_ID);
		hoodMotor = new TalonSRX(RobotMap.HOOD_MOTOR_ID);
		hoodMotor.configSelectedFeedbackSensor(FeedbackDevice.Analog);

		hoodMotor.setNeutralMode(NeutralMode.Brake);

		flywheelMotor.setInverted(true); // Inverts the flywheel motor
		flywheelMotor.setNeutralMode(NeutralMode.Coast);

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
		targetHoodAngle = shooterTab
			.add("set hood angle", 0)
			.getEntry();
		currentHoodAngle = shooterTab
			.add("current hood angle", 0)
			.getEntry();
		hasShotShuffEntry = shooterTab
			.add("has shot", false)
			.getEntry();

		try {
			sc = new Scanner(robotConstantsFile);
			sc.useDelimiter(":");
		} catch (Exception e) {
		
		}
		
		 if((Files.exists(Paths.get("/home/lvuser/robot_constants")))){ 
			if (sc.next().equals("hoodOffset")) {				
				hoodOffset = Double.parseDouble(sc.next());
			}
		}

		hoodMotor.setSensorPhase(true);

	}

	public double getHoodAngle() {
		return  LightningMath.constrain((hoodMotor.getSelectedSensorPosition() / 4096 * 360) - hoodOffset, Constants.MIN_HOOD_ANGLE, Constants.MAX_HOOD_ANGLE); // Should retrun the angle; maybe 4096
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
		flywheelMotor.set(TalonFXControlMode.PercentOutput, 0); 
	}

	public double getEncoderRPM() {
		return flywheelMotor.getSelectedSensorVelocity() / 2048 * 600; //converts from revs per second to revs per minute
	}

	public void setRPM(double targetRPMs) {
		this.targetRPM = targetRPMs;
		setVelocity(targetRPMs / 600 * 2048);
	}

	public double getShooterPower() {
		return shooterPower;
	}

	public double currentEncoderTicks() {
		return flywheelMotor.getSelectedSensorPosition();
	}

	// Checks if flywheel RPM is within a threshold
	public boolean getArmed() {
		boolean flywheel = Math.abs(getEncoderRPM() - targetRPM) < Constants.SHOOTER_TOLERANCE;
		SmartDashboard.putNumber("Shooter RPM DIff", getEncoderRPM() - targetRPM);
		boolean hood = Math.abs(getHoodAngle() - hoodAngle) < Constants.HOOD_TOLERANCE;
		return flywheel && hood;
	}

	// Update Displays on Dashboard
	public void setSmartDashboardCommands() {
		displayRPM.setDouble(getEncoderRPM());
		displayShooterPower.setDouble(getShooterPower());
		currentHoodAngle.setDouble(getHoodAngle());
		hasShotShuffEntry.setBoolean(hasShot);
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

	public double getHoodAngleFromDashboard() {
		return targetHoodAngle.getDouble(0);
	}

	public boolean getHasShot() {
		return hasShot;
	}

	@Override
	public void periodic() {	
		
		shooterArmedEntry.setBoolean(getArmed());
		
		SmartDashboard.putNumber("hood offset from funky file", hoodOffset);

		// currentTarget = targetRPM;

		// if(prevTarget != currentTarget) {
		// 	startTime = Timer.getFPGATimestamp();
		// }
		
		// prevTarget = currentTarget;

		// if(Timer.getFPGATimestamp() - startTime <= Constants.SHOOTER_COOLDOWN) {
		// 		hasShot = false;
		// } else {
		// 		hasShot = getArmed();
		// }	
		
		setSmartDashboardCommands();

	}

}
