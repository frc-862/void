package com.lightningrobotics.voidrobot.subsystems;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

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
	private ShuffleboardTab trimTab = Shuffleboard.getTab("Biases");
    private NetworkTableEntry displayRPM;
    private NetworkTableEntry setRPM;
    private NetworkTableEntry displayShooterPower;
	private NetworkTableEntry targetHoodAngle;
	private NetworkTableEntry currentHoodAngle;
	private NetworkTableEntry hasShotShuffEntry;
	private NetworkTableEntry hoodTrimEntry = trimTab.add("Hood Bias", 0).getEntry();
	private NetworkTableEntry RPMTrimEntry = trimTab.add("RPM Bias", 0).getEntry();

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
	private boolean disableHood = false;
	private boolean manualOverrideHood = false;
	private boolean manualOverrideHoodPower = false;
	private double manualOverrideTarget = 0;
	
	private static ShuffleboardTab driverView = Shuffleboard.getTab("Competition");
	private static ShuffleboardTab disableTab = Shuffleboard.getTab("disable tab");
	private static NetworkTableEntry shooterArmedEntry = driverView.add("Shooter armed", false).getEntry();
    private static NetworkTableEntry disableHoodEntry = disableTab.add("disable hood", false).getEntry();
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
		// return hoodMotor.getSelectedSensorPosition() / 4096 * 360 - hoodOffset;
	}

	public double getRawHoodAngle() {
		return hoodMotor.getSelectedSensorPosition() / 4096 * 360;
	}

	public void setHoodAngle(double hoodAngle) {
		if(manualOverrideHood) {
			this.hoodAngle = LightningMath.constrain(hoodAngle + hoodTrimEntry.getDouble(0), Constants.MIN_HOOD_ANGLE, Constants.MAX_HOOD_ANGLE);
			hoodPowerSetPoint = Constants.HOOD_PID.calculate(getHoodAngle(), manualOverrideTarget);
			setHoodPower(hoodPowerSetPoint);
		} else {
			this.hoodAngle = LightningMath.constrain(hoodAngle + hoodTrimEntry.getDouble(0), Constants.MIN_HOOD_ANGLE, Constants.MAX_HOOD_ANGLE);
			hoodPowerSetPoint = Constants.HOOD_PID.calculate(getHoodAngle(), this.hoodAngle);
			setHoodPower(hoodPowerSetPoint);
		}
	}

	public void setManualHoodOverride(boolean override, double manualOverrideTarget) {
		manualOverrideHood = override;
		this.manualOverrideTarget = manualOverrideTarget;
	}

	public void setHoodPower(double power) {
		if(disableHood) {
			hoodMotor.set(TalonSRXControlMode.PercentOutput, 0);
		} else if(manualOverrideHoodPower) {
			hoodMotor.set(TalonSRXControlMode.PercentOutput, power); //TODO: use a set manual hood power
		} else {
			hoodMotor.set(TalonSRXControlMode.PercentOutput, power);
		}
	}

	public void setManualHoodPower(double power) {
		if(manualOverrideHoodPower) {
			hoodMotor.set(TalonSRXControlMode.PercentOutput, power);
		}
	}

	public void setManualHoodOverride(boolean override) {
		manualOverrideHoodPower = override; 
	}
	public boolean getManualHoodOverride() {
		return manualOverrideHoodPower;
	}
	public void setPower(double power) {
		flywheelMotor.set(TalonFXControlMode.PercentOutput, power); 
	}

	public void setVelocity(double shooterVelocity) {
		if(shooterVelocity == 0) {
			stop();
			return;
		}
		flywheelMotor.set(TalonFXControlMode.Velocity, shooterVelocity); 
	}

	public void stop() {
		flywheelMotor.set(TalonFXControlMode.PercentOutput, 0); 
	}

	public double getEncoderRPM() {
		return flywheelMotor.getSelectedSensorVelocity() / 2048 * 600; //converts from revs per second to revs per minute
	}

	public void setRPM(double targetRPMs) {
		this.targetRPM = targetRPMs + RPMTrimEntry.getDouble(0);
		setVelocity(this.targetRPM / 600 * 2048);
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
		// SmartDashboard.putNumber("Shooter RPM DIff", getEncoderRPM() - targetRPM);
		boolean hood = Math.abs(getHoodAngle() - hoodAngle) < Constants.HOOD_TOLERANCE;
		return flywheel && hood;
	}

	// Update Displays on Dashboard
	 public void setSmartDashboardCommands() {
		displayRPM.setDouble(getEncoderRPM());
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

		disableHood = disableHoodEntry.getBoolean(false);

	}

}
