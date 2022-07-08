package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXSimCollection;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import com.lightningrobotics.common.logging.DataLogger;
import com.lightningrobotics.voidrobot.constants.Constants;

import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.simulation.BatterySim;
import edu.wpi.first.wpilibj.simulation.FlywheelSim;
import edu.wpi.first.wpilibj.simulation.RoboRioSim;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Shooter extends SubsystemBase {

	// Creates the flywheel motor and hood motors
	private TalonFX flywheelMotor;
	private TalonFXSimCollection flywheelMotorSim;

	// Creates our shuffleboard tabs for seeing important values
	private ShuffleboardTab shooterTab = Shuffleboard.getTab("shooter");

	private ShuffleboardTab shooterTestTab = Shuffleboard.getTab("shooter test");
    private NetworkTableEntry displayRPM = shooterTab.add("RPM-From encoder", 0).getEntry();
    private NetworkTableEntry setRPMEntry = shooterTestTab.add("set RPM", 0).getEntry();;
	private NetworkTableEntry displayTargetRPM = shooterTab.add("target RPM", 0).getEntry();
	private NetworkTableEntry isAutoShootingEntry = shooterTab.add("Is Autoshooting", false).getEntry();
	
	// private NetworkTableEntry kP = shooterTab.add("kP tune", 0).getEntry();
	// private NetworkTableEntry kD = shooterTab.add("kP tune", 0).getEntry();

	private ShuffleboardTab tuneTab = Shuffleboard.getTab("tune tab");
	private NetworkTableEntry setRPMAngleTuneEntry = tuneTab.add("set RPM tune", 0).getEntry();

	private FlywheelSim flywheelSim;

	// The power point we want the shooter to be at
	private double targetRPM;

	public Shooter() {
		// Sets the IDs of the hood and shooter
		flywheelMotor = new TalonFX(RobotMap.FLYWHEEL_MOTOR_ID);
		flywheelMotorSim = flywheelMotor.getSimCollection();

		flywheelMotor.setInverted(true);
		flywheelMotor.setNeutralMode(NeutralMode.Coast);
		flywheelMotor.configClosedloopRamp(0.02);

		configPIDGains(Constants.SHOOTER_KP, Constants.SHOOTER_KI, Constants.SHOOTER_KD, Constants.SHOOTER_KF);

		initLogging();
		
		flywheelSim = new FlywheelSim(
			DCMotor.getFalcon500(1),
			1,
			Units.inchesToMeters(10)
		);

		CommandScheduler.getInstance().registerSubsystem(this);

	}

	@Override
	public void periodic() {	

		// double kP_val = kP.getDouble(Constants.KP);
		// double kD_val = kD.getDouble(Constants.KD);
		// configPIDGains(, Constants.KI, kD.setDouble(Constants.KD), Constants.KV);

		if (Constants.SHOT_TUNING) {
			setRPM(setRPMAngleTuneEntry.getDouble(0));
		}

		flywheelMotorSim.setBusVoltage(RobotController.getBatteryVoltage());
		flywheelSim.setInput(flywheelMotorSim.getMotorOutputLeadVoltage());
		flywheelSim.update(0.020);

		RoboRioSim.setVInVoltage(
			BatterySim.calculateDefaultBatteryLoadedVoltage(flywheelSim.getCurrentDrawAmps())
		);

		// TODO: find a way to display flywheelSim via shuffleboard graphically

		setSmartDashboardCommands();

	}

	private void initLogging() {
		DataLogger.addDataElement("shooterRPM", this::getCurrentRPM);
		DataLogger.addDataElement("targetShooterRPM", () -> targetRPM);
	}

	public void setPower(double power) {
		flywheelMotor.set(TalonFXControlMode.PercentOutput, power); 
	}

	public void setVelocity(double shooterVelocity) {
		if(shooterVelocity == 0) {
			coast();
			return;
		}
		flywheelMotor.set(TalonFXControlMode.Velocity, shooterVelocity); 
	}

	public void stop() {
		setPower(0);
	}

	public void coast() {
		setPower(0.1d);
	}

	public double getCurrentRPM() {
		return flywheelMotor.getSelectedSensorVelocity() / 2048 * 600; //converts from revs per second to revs per minute
	}

	public void setRPM(double targetRPM) {
		this.targetRPM = targetRPM;
		setVelocity(this.targetRPM / 600 * 2048);
	}

	public double currentEncoderTicks() {
		return flywheelMotor.getSelectedSensorPosition();
	}

	// Update Displays on Dashboard
	 public void setSmartDashboardCommands() {
		displayRPM.setDouble(getCurrentRPM());
		displayTargetRPM.setDouble(targetRPM);
	 }

	private void configPIDGains(double kP, double kI, double kD, double kV) {
		flywheelMotor.config_kP(0, kP);
		flywheelMotor.config_kI(0, kI);
		flywheelMotor.config_kD(0, kD);
		flywheelMotor.config_kF(0, kV);
	}

	public double getRPMFromDashboard() {
		return setRPMEntry.getDouble(0);
	}

	public void toggleAutoShootingDisplay(){
		isAutoShootingEntry.setBoolean(!isAutoShootingEntry.getBoolean(false));
	}
}
