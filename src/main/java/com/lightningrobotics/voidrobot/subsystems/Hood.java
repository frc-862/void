package com.lightningrobotics.voidrobot.subsystems;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.lightningrobotics.common.logging.DataLogger;
import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import com.lightningrobotics.voidrobot.constants.Constants;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Hood extends SubsystemBase {

	// Creates the flywheel motor and hood motors
	private TalonSRX hoodMotor;

	// Creates our shuffleboard tabs for seeing important values
	private ShuffleboardTab hoodTab = Shuffleboard.getTab("hood");
	private NetworkTableEntry targetAngle = hoodTab.add("target hood angle", 0).getEntry();
	private NetworkTableEntry currentAngle = hoodTab.add("current hood angle", 0).getEntry();;
	private NetworkTableEntry rawAngle = hoodTab.add("raw hood angle", 0).getEntry();

	
	private boolean disableHood = false;
	private NetworkTableEntry hoodDisable = hoodTab.add("disabel hood", disableHood).getEntry();
	
	private double hoodOffset;

	// The power point we want the shooter to be at
	private double PowerSetPoint;
	private double angle;
	private boolean manualOverride = false;

	public Hood() {
		// Sets the IDs of the hood and shooter
		hoodMotor = new TalonSRX(RobotMap.HOOD_MOTOR_ID);
		hoodMotor.configSelectedFeedbackSensor(FeedbackDevice.Analog);

		hoodMotor.setNeutralMode(NeutralMode.Brake);

		hoodMotor.setSensorPhase(true);

		readZero();

		initLogging();

		CommandScheduler.getInstance().registerSubsystem(this);

	}

	@Override
	public void periodic() {	
		
		disableHood = hoodDisable.getBoolean(false);
		if (!manualOverride && !disableHood) {
			PowerSetPoint = Constants.HOOD_PID.calculate(getAngle(), this.angle);
			hoodMotor.set(TalonSRXControlMode.PercentOutput, PowerSetPoint);
		}
		else if(disableHood) {
			hoodMotor.set(TalonSRXControlMode.PercentOutput, 0);
		}
		
		setSmartDashboardCommands();

	}

	private void initLogging() {
		DataLogger.addDataElement("hoodAngle", this::getAngle);
		DataLogger.addDataElement("hoodSetPoint", this::getSetPoint);
	}

	public boolean onTarget() {
		return Math.abs(angle - getAngle()) < Constants.HOOD_TOLERANCE;
	}

	public void readZero() {
		Scanner sc = null;
		File robotConstantsFile = Paths.get("/home/lvuser/robot_constants/", "robot_constants.txt").toFile();
		try {
			sc = new Scanner(robotConstantsFile);
			sc.useDelimiter(":");

			if((Files.exists(Paths.get("/home/lvuser/robot_constants")))) { 
				if (sc.next().equals("hoodOffset")) {				
					hoodOffset = Double.parseDouble(sc.next());
				}
			}
		} catch (Exception e) {
			System.err.println("Failed to see robot_constants file" + e.getMessage());
		} finally {
			if (sc != null) {
				sc.close();

			}
		}
	}

	public double getSetPoint() {
		return angle;
	}

	public double getAngle() {
		return (hoodMotor.getSelectedSensorPosition() / 4096 * 360) - hoodOffset;
	}

	public double getRawAngle() {
		return hoodMotor.getSelectedSensorPosition() / 4096 * 360;
	}

	public void setAngle(double hoodAngle) {
		manualOverride = false;
		this.angle = LightningMath.constrain(hoodAngle, Constants.MIN_HOOD_ANGLE, Constants.MAX_HOOD_ANGLE);
	}

	public void setPower(double power) {
		manualOverride = true;
		hoodMotor.set(TalonSRXControlMode.PercentOutput, power);
	}

	public void stop() {
		setPower(0);
		manualOverride = true;
	}

	// Update Displays on Dashboard
	 public void setSmartDashboardCommands() {
		currentAngle.setDouble(getAngle());
		targetAngle.setDouble(getSetPoint());
		rawAngle.setDouble(getRawAngle());
	 }
}
