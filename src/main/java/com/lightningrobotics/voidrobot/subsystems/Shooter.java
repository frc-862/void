package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.lightningrobotics.common.controller.PIDFController;
import com.lightningrobotics.common.subsystem.drivetrain.PIDFDashboardTuner;
import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import com.lightningrobotics.voidrobot.constants.Constants;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
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
	private NetworkTableEntry targetHoodAngle;
	private NetworkTableEntry currentHoodAngle;
	private NetworkTableEntry hasShotShuffEntry;

	// Creates a PID and FeedForward controller for our shooter
	private PIDFController hoodPID = new PIDFController(Constants.HOOD_KP, Constants.HOOD_KI, Constants.HOOD_KD);

	// PID tuner for the shooter gains
	private PIDFDashboardTuner hoodTuner = new PIDFDashboardTuner("hood test", Constants.HOOD_PID);

	// The power point we want the shooter to be at
	private double shooterPower;
	private double hoodPowerSetPoint;
	private double targetRPM;
	private double hoodAngle;
	private static boolean armed;
	private double prevTarget;
	private double currentTarget;
	private double timeWhenChanged = 0;
	private boolean hasShot = false;
	
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
		targetHoodAngle = shooterTab
			.add("set hood angle", 0)
			.getEntry();
		currentHoodAngle = shooterTab
			.add("current hood angle", 0)
			.getEntry();
		hasShotShuffEntry = shooterTab
			.add("has shot", false)
			.getEntry();

	}

	/**
	 * gets the hood angle in degrees
	 */
	public double getHoodAngle() {
		return hoodMotor.getSelectedSensorPosition() / 4096 * 360; // Should retrun the angle; maybe 4096
	}

	/**
	 * commands the hood to move to an angle using a PID
	 * @param hoodAngle target hood angle in degrees
	 */
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

	/**
	 * stops the shooter motor
	 */
	public void stop() {
		flywheelMotor.set(TalonFXControlMode.PercentOutput, 0);; 
	}

	/**
	 * gets the shooter's RPMs
	 * @return the RPM's of the shooter
	 */
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
		boolean flywheel = Math.abs(getEncoderRPM() - targetRPM) < Constants.SHOOTER_TOLERANCE;
		boolean hood = Math.abs(getHoodAngle() - hoodAngle) < Constants.HOOD_TOLERANCE;
		armed = flywheel && hood;
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
		hasShotShuffEntry.setBoolean(hasShot);
	}

	private void configPIDGains(double kP, double kI, double kD, double kV) {
		flywheelMotor.config_kP(0, kP);
		flywheelMotor.config_kI(0, kI);
		flywheelMotor.config_kD(0, kD);
		flywheelMotor.config_kF(0, kV);
	}

	/**
	 * 
	 * @return the target RPMs for the shooter taken from the dashboard
	 */
	public double getRPMFromDashboard() {
		return setRPM.getDouble(0);
	}

	public double getHoodAngleFromDashboard() {
		return targetHoodAngle.getDouble(0);
	}

	/**
	 * gets the optimal shooter RPM from an inputted height in pixels using an interpolation map
	 * @param height in pixels
	 * @return motor RPMs
	 */
	public double getRPMsFromHeight(double height) {
		if (height > 0) {
            return Constants.DISTANCE_RPM_MAP.get(height);
        } else {
            return 0;
        }
	}

	/**
	 * gets the optimal hood angle from an inputted distance in X using an interpolation map
	 * @param height in pixels
	 * @return target angle
	 */
	public double getAngleFromHeight(double distance) {
		if (distance > 0) {
            return Constants.HOOD_ANGLE_MAP.get(distance);
        } else {
            return 0;
        }
	}

	@Override
	public void periodic() {
		
		// TODO move to manual shoot command
		// currentTarget = getRPMFromDashboard();
		// setRPM(currentTarget);
		// setHoodAngle(targetHoodAngle.getDouble(0));
		
		if(Timer.getFPGATimestamp() - timeWhenChanged < Constants.SHOOTER_COOLDOWN) {
			hasShot = false;
		} else {
			hasShot = getArmed();
		}
		
		prevTarget = currentTarget;

		if(prevTarget != currentTarget) {
			timeWhenChanged = Timer.getFPGATimestamp();
		}

		
		setSmartDashboardCommands();

	}

}
