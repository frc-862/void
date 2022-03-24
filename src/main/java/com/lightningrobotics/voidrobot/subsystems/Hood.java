package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.lightningrobotics.common.logging.DataLogger;
import com.lightningrobotics.common.subsystem.drivetrain.PIDFDashboardTuner;
import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import com.lightningrobotics.voidrobot.constants.Constants;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Hood extends SubsystemBase {

	// Creates the flywheel motor and hood motors
	private TalonSRX hoodMotor;
	private DigitalInput resetHoodSensor;

	// Creates our shuffleboard tabs for seeing important values
	private ShuffleboardTab hoodTab = Shuffleboard.getTab("hood");
	
	private ShuffleboardTab tuneTab = Shuffleboard.getTab("tune tab");
	
	private NetworkTableEntry setHoodAngleTuneEntry = tuneTab.add("set hood tune", 0).getEntry();

	private ShuffleboardTab shooterTestTab = Shuffleboard.getTab("shooter test");
	private NetworkTableEntry targetAngle = hoodTab.add("target hood angle", 0).getEntry();
	private NetworkTableEntry currentAngle = hoodTab.add("current hood angle", 0).getEntry();;
	private NetworkTableEntry rawAngle = hoodTab.add("raw hood angle", 0).getEntry();

	private NetworkTableEntry setHoodAngleEntry = shooterTestTab.add("set hood", 0).getEntry();

	private final PIDFDashboardTuner tuner = new PIDFDashboardTuner("hood", Constants.HOOD_PID);
	
	private boolean disableHood = false;
	private NetworkTableEntry hoodDisable = hoodTab.add("disabel hood", disableHood).getEntry();
	
	private double hoodOffset; 

	// The power point we want the shooter to be at
	private double pwrSetpoint;
	private double angle;
	private boolean manualOverride = false;

	public Hood() {
		// Sets the IDs of the hood and shooter
		hoodMotor = new TalonSRX(RobotMap.HOOD_MOTOR_ID);
		hoodMotor.configSelectedFeedbackSensor(FeedbackDevice.Analog);

		hoodMotor.setNeutralMode(NeutralMode.Brake);

		hoodMotor.setSensorPhase(true);

		zero();

		initLogging();

		CommandScheduler.getInstance().registerSubsystem(this);

	}

	@Override
	public void periodic() {	
		if (Constants.SHOT_TUNING) {
			setAngle(setHoodAngleTuneEntry.getDouble(0));
		}

		disableHood = hoodDisable.getBoolean(false);
		if (!manualOverride && !disableHood) {
			if (angle <= 0) {
				hoodMotor.set(TalonSRXControlMode.PercentOutput, Constants.HOOD_ZERO_SPEED);
				zero();
			} else { 
				pwrSetpoint = Constants.HOOD_PID.calculate(getAngle(), this.angle);
				hoodMotor.set(TalonSRXControlMode.PercentOutput, pwrSetpoint);
			}
		}
		else if(disableHood) {
			hoodMotor.set(TalonSRXControlMode.PercentOutput, 0);
		}
		
		setSmartDashboardCommands();

		SmartDashboard.putBoolean("hood limit switch ", getLimitSwitch());
	}

	private void initLogging() {
		DataLogger.addDataElement("hoodAngle", this::getAngle);
		DataLogger.addDataElement("hoodSetPoint", this::getSetPoint);
	}

	public void zero() {
		if (getLimitSwitch()) {
			hoodOffset = getRawAngle();
		}	
	}

	public boolean getLimitSwitch() {
		return hoodMotor.isRevLimitSwitchClosed() == 1;
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
		this.angle = hoodAngle;
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

	 public double getAngleFromDashboard() {
		return setHoodAngleEntry.getDouble(0);
	}
}
