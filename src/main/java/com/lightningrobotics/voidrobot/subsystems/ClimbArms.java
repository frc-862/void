package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXSimCollection;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.lightningrobotics.common.logging.DataLogger;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.constants.RobotMap;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.simulation.BatterySim;
import edu.wpi.first.wpilibj.simulation.ElevatorSim;
import edu.wpi.first.wpilibj.simulation.RoboRioSim;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ClimbArms extends SubsystemBase {
	private TalonFX leftArm;
	private TalonFX rightArm;

	private TalonFXSimCollection leftSim;
	private TalonFXSimCollection rightSim;

	//initialize set point for arm height
	private double armsTarget = 0;

	private ShuffleboardTab climbTab = Shuffleboard.getTab("climber");
	private NetworkTableEntry leftArmPos = climbTab.add("left arm", -1000).getEntry();
	private NetworkTableEntry rightArmPos = climbTab.add("right arm", -1000).getEntry();

	private ElevatorSim armLeft;
	private ElevatorSim armRight;

	private final Mechanism2d m_leftClimbMech2d = new Mechanism2d(20, 50);
	private final MechanismRoot2d m_leftClimbMech2dRoot = m_leftClimbMech2d.getRoot("Left", 10, 0);
	private MechanismLigament2d m_leftClimbLigament2d;

	private final Mechanism2d m_rightClimbMech2d = new Mechanism2d(20, 50);
	private final MechanismRoot2d m_rightClimbMech2dRoot = m_rightClimbMech2d.getRoot("Left", 10, 0);
	private MechanismLigament2d m_rightClimbLigament2d;

  	public ClimbArms() {
		// Sets the IDs of our arm motors
		leftArm = new TalonFX(RobotMap.LEFT_CLIMB);
		rightArm = new TalonFX(RobotMap.RIGHT_CLIMB);
		leftSim = leftArm.getSimCollection();
		rightSim = rightArm.getSimCollection();

		//climb motors need to be in brake mode to hold climb up
		leftArm.setNeutralMode(NeutralMode.Brake);
		rightArm.setNeutralMode(NeutralMode.Brake);

		//set arm inverts
		leftArm.setInverted(false);
		rightArm.setInverted(RobotBase.isReal()); // simulation doesn't need to be inverted, but robot does

		resetEncoders();
		setGains();
		initLogging();

		//setup right/left sims
		armLeft = new ElevatorSim(
			DCMotor.getFalcon500(1),
			1,
			Units.lbsToKilograms(11.5),
			Units.inchesToMeters(0.492),
			Units.inchesToMeters(39),
			Units.inchesToMeters(65),
			VecBuilder.fill(0.01)
		);

		armRight = new ElevatorSim(
			DCMotor.getFalcon500(1),
			1,
			Units.lbsToKilograms(11.5),
			Units.inchesToMeters(0.492),
			Units.inchesToMeters(39),
			Units.inchesToMeters(65),
			VecBuilder.fill(0.01)
		);
		
		m_leftClimbLigament2d = m_leftClimbMech2dRoot.append(
			new MechanismLigament2d("Elevator Left", Units.metersToInches(armLeft.getPositionMeters()), 90)
		);
			
		m_rightClimbLigament2d = m_rightClimbMech2dRoot.append(
			new MechanismLigament2d("Elevator Right", Units.metersToInches(armRight.getPositionMeters()), 90)
		);
				
		SmartDashboard.putData("Left Climb Arm Sim", m_leftClimbMech2d);
		SmartDashboard.putData("Right Climb Arm Sim", m_rightClimbMech2d);

		CommandScheduler.getInstance().registerSubsystem(this);
	}

	private void initLogging() {
		DataLogger.addDataElement("leftArmPosition", () -> leftArm.getSelectedSensorPosition());
		DataLogger.addDataElement("rightArmPosition", () -> rightArm.getSelectedSensorPosition());
		DataLogger.addDataElement("armsTarget", () -> armsTarget);

		DataLogger.addDataElement("left arm reach", () -> leftArm.isFwdLimitSwitchClosed());
		DataLogger.addDataElement("right arm reach", () -> rightArm.isFwdLimitSwitchClosed());
		DataLogger.addDataElement("left arm hold", () -> leftArm.isRevLimitSwitchClosed());
		DataLogger.addDataElement("right arm hold", () -> rightArm.isRevLimitSwitchClosed());
	}

	private void setGains() {
		leftArm.config_kF(1, 0d);
		leftArm.config_kP(1, Constants.ARM_KP);
		leftArm.config_kI(1, 0);
		leftArm.config_kD(1, 0);
		leftArm.configAllowableClosedloopError(1, Constants.ARM_TARGET_THRESHOLD);

		rightArm.config_kF(1, 0d);
		rightArm.config_kP(1, Constants.ARM_KP);
		rightArm.config_kI(1, 0);
		rightArm.config_kD(1, 0);
		rightArm.configAllowableClosedloopError(1, Constants.ARM_TARGET_THRESHOLD);

		leftArm.selectProfileSlot(1, 0);
		rightArm.selectProfileSlot(1, 0);

	}

	public void setPower(double leftPower, double rightPower) {
		leftArm.set(TalonFXControlMode.PercentOutput, leftPower);
		rightArm.set(TalonFXControlMode.PercentOutput, rightPower);
	}

	public boolean getUpperLimitSwitches() {
		return leftArm.isFwdLimitSwitchClosed() == 1 && rightArm.isFwdLimitSwitchClosed() == 1;
	}

	public boolean getLowerLimitSwitches() {
		return leftArm.isRevLimitSwitchClosed() == 1 && rightArm.isRevLimitSwitchClosed() == 1;
	}

	/**
	 * @param armTarget desired set point, in encoder ticks
	 */
	public void setTarget(double armTarget) {
		// leftArm.selectProfileSlot(1, 0);
		// rightArm.selectProfileSlot(1, 0);
		this.armsTarget = armTarget;
		leftArm.set(TalonFXControlMode.Position, armsTarget);
		rightArm.set(TalonFXControlMode.Position, armsTarget);
	}
	
	public void resetEncoders() {
		leftArm.setSelectedSensorPosition(0);
		rightArm.setSelectedSensorPosition(0);
	}

	public double getleftEncoder() {
		return leftArm.getSelectedSensorPosition();
	}

	public double getRightEncoder() {
		return rightArm.getSelectedSensorPosition();
	}

	public void setSimAngle(int degrees) {
		m_leftClimbLigament2d.setAngle(degrees);
		m_rightClimbLigament2d.setAngle(degrees);
	}

	/**
	 * @return true if the arms are within a given threshhold
	 */
	public boolean onTarget() {
		return Math.abs(leftArm.getSelectedSensorPosition()  - armsTarget) < Constants.ARM_TARGET_THRESHOLD && 
			   Math.abs(rightArm.getSelectedSensorPosition() - armsTarget) < Constants.ARM_TARGET_THRESHOLD;
	}
	//here lies jusnoor's gyro code
	//shouldnt have been merged lol

	@Override
	public void periodic() {
		leftArmPos.setNumber(leftArm.getSelectedSensorPosition());
		rightArmPos.setNumber(rightArm.getSelectedSensorPosition());	
		SmartDashboard.putBoolean("upper limits", getUpperLimitSwitches());
		SmartDashboard.putBoolean("lower limits", getLowerLimitSwitches());
		if (getLowerLimitSwitches()) {
			resetEncoders();
		}

		leftSim.setBusVoltage(RobotController.getBatteryVoltage());
		rightSim.setBusVoltage(RobotController.getBatteryVoltage());

		armLeft.setInput(leftSim.getMotorOutputLeadVoltage());
		armRight.setInput(rightSim.getMotorOutputLeadVoltage());

		armLeft.update(0.020);
		armRight.update(0.020);

		RoboRioSim.setVInCurrent(
			BatterySim.calculateDefaultBatteryLoadedVoltage(armLeft.getPositionMeters())
		);
		RoboRioSim.setVInCurrent(
			BatterySim.calculateDefaultBatteryLoadedVoltage(armRight.getPositionMeters())
		);

		m_leftClimbLigament2d.setLength(armLeft.getPositionMeters());
		m_rightClimbLigament2d.setLength(armRight.getPositionMeters());
	}
	
	public void stop() {
		setPower(0, 0);
	}

}
