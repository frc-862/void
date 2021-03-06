package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.lightningrobotics.common.logging.DataLogger;
import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import com.lightningrobotics.voidrobot.constants.Constants;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Turret extends SubsystemBase {
	// Creating turret motor, encoder, and PID controller
	private final TalonSRX turretMotor;

	private final DigitalInput centerSensor = new DigitalInput(RobotMap.CENTER_SENSOR_ID);

	// A PID tuner that displays to a tab on the dashboard (values dont save, rember what you typed)
	// private final PIDFDashboardTuner tunerSlow = new PIDFDashboardTuner("Turret slow", Constants.TURRET_PID_SLOW);
	// private final PIDFDashboardTuner tunerFast = new PIDFDashboardTuner("Turret fast", Constants.TURRET_PID_FAST);

	private double targetAngle;
	private boolean disabelTurret = false;
	private boolean manualOverride = false;

	private double minTurretConstraint = Constants.MIN_TURRET_ANGLE;
	private double maxTurretConstraint = Constants.MAX_TURRET_ANGLE; 

	private ShuffleboardTab turretTab = Shuffleboard.getTab("Turret");
	private NetworkTableEntry currentAngle;
	private NetworkTableEntry centerSensorEntry;
	private NetworkTableEntry setTargetAngleEntry;
	private NetworkTableEntry leftLimitSwitchEntry;
	private NetworkTableEntry rightLimitSwitchEntry;

	public Turret() {

		// Motor config
		turretMotor = new TalonSRX(RobotMap.TURRET_MOTOR_ID);
		turretMotor.setNeutralMode(NeutralMode.Brake);
		turretMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);

		// Dashboard info
		centerSensorEntry = turretTab.add("Hit Center Censor", "false").getEntry();
		leftLimitSwitchEntry = turretTab.add("Hit Left Limit Switch", false).getEntry();
		rightLimitSwitchEntry = turretTab.add("Hit Right Limit Switch", false).getEntry();
		setTargetAngleEntry = turretTab.add("Set Turret Angle", 0).getEntry();
		currentAngle = turretTab.add("current angle", 0).getEntry(); 

		initLogging();

		// Reset values
		resetEncoder();
	    targetAngle = 0;
		
		CommandScheduler.getInstance().registerSubsystem(this);

	}
	
	@Override
	public void periodic() {
		currentAngle.setDouble(getCurrentAngle().getDegrees());
		centerSensorEntry.setBoolean(getCenterSensor());
		leftLimitSwitchEntry.setBoolean(getLeftLimitSwitch());
		rightLimitSwitchEntry.setBoolean(getRightLimitSwitch());
		setTargetAngleEntry.setDouble(getTarget());

		if (!manualOverride && !disabelTurret) {
			var power = getMotorOutput(getTarget());
			turretMotor.set(TalonSRXControlMode.PercentOutput, LightningMath.constrain(power, -Constants.TURRET_NORMAL_MAX_MOTOR_OUTPUT, Constants.TURRET_NORMAL_MAX_MOTOR_OUTPUT));
		}
		else if (disabelTurret) {
			turretMotor.set(TalonSRXControlMode.PercentOutput, 0);
		}

		// if (getCenterSensor()) {
		// 	resetEncoder();
		// }
	}

	private void initLogging() {
		DataLogger.addDataElement("centerSensor", () -> getCenterSensor() ? 1 : 0);
		DataLogger.addDataElement("turretTarget", this::getTarget);
		DataLogger.addDataElement("turretCurrent", () -> getCurrentAngle().getDegrees());
		//DataLogger.addDataElement("turretCommand", () -> "foo");
		// this.getCurrentCommand().getName()
	}

	public void stop() {
		turretMotor.set(TalonSRXControlMode.PercentOutput, 0);
		manualOverride = true;
	}

	public void setDisableTurret(boolean disabelTurret) {
		this.disabelTurret = disabelTurret;
	}

	public Rotation2d getCurrentAngle(){
		return Rotation2d.fromDegrees(getEncoderRotation() / Constants.TURN_TURRET_GEAR_RATIO * 360d);
	}

	public double getEncoderRotation() {
		return turretMotor.getSelectedSensorPosition() / 4096;
	}

	public void setAngle(double targetAngle) {
		manualOverride = false;
		double sign = Math.signum(targetAngle);
        targetAngle =  sign * (((Math.abs(targetAngle) + 180) % 360) - 180);

        this.targetAngle = LightningMath.constrain(targetAngle, minTurretConstraint, maxTurretConstraint);
	}

	public void resetConstraint() {
		minTurretConstraint = Constants.MIN_TURRET_ANGLE;
		maxTurretConstraint = Constants.MAX_TURRET_ANGLE;
	}

	public void setConstraint(double center, double constrainRange) {
		System.out.println("ive been called " + center);
		minTurretConstraint = LightningMath.constrain(center - constrainRange, Constants.MIN_TURRET_ANGLE, Constants.MAX_TURRET_ANGLE);
		maxTurretConstraint = LightningMath.constrain(center + constrainRange, Constants.MIN_TURRET_ANGLE, Constants.MAX_TURRET_ANGLE);
	}

	public void resetEncoder() {
		turretMotor.setSelectedSensorPosition(0); 
	}

	public boolean getLeftLimitSwitch() {
		return turretMotor.isFwdLimitSwitchClosed() == 1;
	}

	public boolean getRightLimitSwitch() {
		return turretMotor.isRevLimitSwitchClosed() == 1;
	}

	public boolean getCenterSensor() {
		return !centerSensor.get();
	}

	public void setPower(double power) {
		manualOverride = true;
		turretMotor.set(TalonSRXControlMode.PercentOutput, LightningMath.constrain(power, -Constants.TURRET_NORMAL_MAX_MOTOR_OUTPUT, Constants.TURRET_NORMAL_MAX_MOTOR_OUTPUT));
	}

	public double getMotorOutput(double constrainedAngle){
		double motorOutput;
		if(Math.abs(constrainedAngle - getCurrentAngle().getDegrees()) <= Constants.SLOW_PID_THRESHOLD) {
            motorOutput = Constants.TURRET_PID_SLOW.calculate(getCurrentAngle().getDegrees(), constrainedAngle);
        } else {
            motorOutput = Constants.TURRET_PID_FAST.calculate(getCurrentAngle().getDegrees(), constrainedAngle);
        }

		if(Math.abs(motorOutput) > (Constants.TURRET_kP_SLOW/2) && Math.abs(motorOutput) < Constants.MIN_TURRET_PWR) {
			motorOutput = Math.signum(motorOutput) * Constants.MIN_TURRET_PWR;
		}

		return motorOutput;
	}

	public double getTarget(){
		return targetAngle;
	}
}	