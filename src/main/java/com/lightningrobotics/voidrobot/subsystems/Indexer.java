package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.lightningrobotics.common.logging.DataLogger;
import com.lightningrobotics.common.util.LightningMath;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Indexer extends SubsystemBase {

    // Creates our indexer motor
    private final VictorSPX indexerMotor;

    // Creates our beam breaks that count the balls
    private final DigitalInput enterSensor = new DigitalInput(RobotMap.ENTER_BEAM_BREAK);
    private final DigitalInput exitSensor  = new DigitalInput(RobotMap.EXIT_BEAM_BREAK);

    Debouncer enterDebouncer = new Debouncer(Constants.INDEX_DEBOUNCE_TIME);
    Debouncer exitDebouncer = new Debouncer(0);

    // define variables to store beam break data
    private boolean lower;
    private boolean upper;

    // define variables to remember previous value of beam break data
    private boolean lowerPrev;
    private boolean upperPrev;

    // Lazy variables so I don't have to type out falling and rising edge logic multiple times
    private boolean collect1;
    private boolean eject1;

    // Creates the color sensor
    private final ColorSensorV3 indexerColorSensor;

    // Initialize ball count to 0 on enable
    public int ballCount = 0;

    // Enum to determin the color of each ball
    public enum BallColor {
        Red,
        Blue,
        nothing
    }

    // Ball 1 color
    BallColor upperBallColor = BallColor.nothing;
    // Ball 2 color
    BallColor lowerBallColor = BallColor.nothing;

    private ShuffleboardTab indexerTab = Shuffleboard.getTab("Indexer");
    private final NetworkTableEntry colorBlindModeEntry = indexerTab.add("Color Blind Mode", false).withWidget(BuiltInWidgets.kToggleSwitch).getEntry();

    public Indexer() {
        // Sets Motor and color ID/ports
        indexerMotor = new VictorSPX(RobotMap.INDEXER_MOTOR_ID);
        indexerColorSensor = new ColorSensorV3(RobotMap.I2C_PORT);

        initLogging();

        indexerTab.addString("upper ball color", () -> upperBallColor.toString());
        indexerTab.addString("llower ball color", () -> lowerBallColor.toString());
        indexerTab.addNumber("ball count", () -> ballCount);
        indexerTab.addBoolean("upper sensor", () -> upper);
        indexerTab.addBoolean("lower sensor", () -> lower);

		CommandScheduler.getInstance().registerSubsystem(this);
    }

    @Override
    public void periodic() {
        // Setting our current beam break status
        lower = getEnterStatus();
        upper = getExitStatus();

        SmartDashboard.putBoolean("lower", lower);
        SmartDashboard.putBoolean("lower prev", lowerPrev);
        SmartDashboard.putNumber("prox", indexerColorSensor.getProximity());
        SmartDashboard.putNumber("Red", indexerColorSensor.getColor().red);
        SmartDashboard.putNumber("Blue", indexerColorSensor.getColor().blue);
        SmartDashboard.putNumber("Green", indexerColorSensor.getColor().green);

        collect1 = !lowerPrev && lower; //Rising edge 
        eject1 = !upper && upperPrev; //Falling edge

        // Swaps the collect and eject when we are reversing the indexer
        if (isMotorReversing()) {
            boolean temp = collect1;
            collect1 = eject1;
            eject1 = temp;
        }

        if (collect1) {
            ballCount++;
        }
        if (eject1) {
            ballCount--;
            upperBallColor = lowerBallColor;
        }
        ballCount = LightningMath.constrain(ballCount, 0, 2);

        lowerPrev = lower;
        upperPrev = upper;

        // Sets the possible color cases of the ball
        switch(getColorSensorOutputs()) {
            case 0: 
                if(ballCount == 0) {
                    upperBallColor = BallColor.nothing;
                    lowerBallColor = BallColor.nothing;
                }
            break;

            case 1:
                if(ballCount == 1) {
                    upperBallColor = BallColor.Red;
                    lowerBallColor = BallColor.nothing;
                } else if(ballCount == 2) {
                    lowerBallColor = BallColor.Red;
                }
            break;

            case 2:
                if(ballCount == 1) {
                    upperBallColor = BallColor.Blue;
                    lowerBallColor = BallColor.nothing;
                } else if(ballCount == 2) {
                    lowerBallColor = BallColor.Blue;
                }
            break;
        }
    }

    private void initLogging() {
        DataLogger.addDataElement("enterSensor", () -> getEnterStatus() ? 1 : 0);
        DataLogger.addDataElement("exitSensor", () -> getExitStatus() ? 1 : 0);
        DataLogger.addDataElement("colorSensor", this::getColorSensorOutputs); // 1 red, 2 blue, 0 nothing 
        DataLogger.addDataElement("ballCount", this::getBallCount);
        DataLogger.addDataElement("indexPower", indexerMotor::getMotorOutputPercent);
        DataLogger.addDataElement("colorProximity", indexerColorSensor::getProximity);
    }

	public void initializeBallsHeld() {
		ballCount = 1;
	}
     
    public void setBallCount(int ballCount) {
        this.ballCount = ballCount;
    }

    public void resetBallCount() {
        ballCount = 0;
    } 
 
    public boolean getCollectedBall() {
        return collect1; // checks to see of the beam break has seen a ball
    }

    public boolean getEjectedBall() {
        return eject1;
    }

    public void setPower(double power) {
        SmartDashboard.putNumber("INDEXER POWER", power);
        indexerMotor.set(VictorSPXControlMode.PercentOutput, power);
    }

    public boolean getEnterStatus(){
        //the ! is added here to make it trigger on enter, not on release
        return enterDebouncer.calculate(!enterSensor.get());
    }

    public boolean getEnterStatusNoDebounce(){
        return !enterSensor.get();
    }

    public boolean getExitStatusNoDebounce(){
        return !exitSensor.get();
    }

    public boolean getExitStatus(){
        //the ! is added here to make it trigger on enter, not on release
        return exitDebouncer.calculate(!exitSensor.get());
    }

    public int getBallCount(){
        return ballCount;
    }

    public int getColorSensorOutputs() {
        if(indexerColorSensor.getColor().red >= Constants.RED_THRESHOLD) {
            // SmartDashboard.putString("Color", "red");
            return 1;
        } else if(indexerColorSensor.getColor().blue >= Constants.BLUE_THRESHOLD) {
            // SmartDashboard.putString("Color", "blue");
            return 2;
        }        
        return 0;
    }
    
    public boolean isEnenmyBall(){
        if(colorBlindModeEntry.getBoolean(false)){
            return false;
        }
        else{
            var allianceBallColor = DriverStation.getAlliance().toString();
            return !allianceBallColor.equals(getUpperBallColor().toString()) && getUpperBallColor() != BallColor.nothing;
        }
    }

    public ColorSensorV3 getColorSensor() {
        return indexerColorSensor;
    }
    
    private boolean isMotorReversing() {
        return indexerMotor.getMotorOutputPercent() < 0;
    }

    public void stop() {
        setPower(0d);
    }

	public BallColor getUpperBallColor() {
        return upperBallColor;
    }

    public BallColor getLowerBallColor() {
        return lowerBallColor;
    }
}