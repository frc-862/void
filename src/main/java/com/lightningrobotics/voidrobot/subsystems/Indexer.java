package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class Indexer extends SubsystemBase {

    // Creates our indexer motor
    private final VictorSPX indexer;

	// Indexer Power
	private final double INDEX_POWER = 1.0;

    // Creates our beam breaks that count the balls
    private static final DigitalInput enterSensor = new DigitalInput(RobotMap.ENTER_BEAM_BREAK);
    private static final DigitalInput exitSensor  = new DigitalInput(RobotMap.EXIT_BEAM_BREAK);

    // define variables to store beam break data
    private static boolean lower;
    private static boolean upper;

    // define variables to remember previous value of beam break data
    private static boolean lowerPrev;
    private static boolean upperPrev;

    // Lazy variables so I don't have to type out falling and rising edge logic multiple times
    private static boolean collect1;
    private static boolean eject1;

    // Creating a start time and setting a buffer timer variable
    private static double bufferStartTime;
    private static double bufferTime = 0.5;
    private static boolean buffer = false;

    // For telling us that we are at max balls
    private boolean atMaxBallCount = false;

    // Creates the color sensor
    private final ColorSensorV3 intakeSensor;

    // Initialize ball count to 0 on enable
    public static int ballCount = 0;

    // Enum to determin the color of each ball
    enum Color {
        red,
        blue,
        nothing
    }

    // Ball 1 color
    Color ball1Color = Color.nothing;
    // Ball 2 color
    Color ball2Color = Color.nothing;

    //Create Shuffleboard and entries
    private static ShuffleboardTab driverVeiw = Shuffleboard.getTab("Competition");
    private static NetworkTableEntry currentBallCountEntry  = driverVeiw.add("Ball count", 1).getEntry();
    

    public Indexer() {
        // Sets Motor and color ID/ports
        indexer = new VictorSPX(RobotMap.INDEXER_MOTOR_ID);
        intakeSensor = new ColorSensorV3(RobotMap.I2C_PORT);

        bufferStartTime = Timer.getFPGATimestamp(); // Sets an initial start time 
    }


    @Override
    public void periodic() {

        
        buffer = !(Timer.getFPGATimestamp() - bufferStartTime > bufferTime); // Tells us when were in our buffer time
        
        // Setting our current beam break status
        lower = getLowerStatus();
        upper = getUpperStatus();

        collect1 = !lowerPrev && lower; //Rising edge 
        eject1 = !upper && upperPrev; //Falling edge

        // Swaps the collect and eject when we are reversing the indexer
        if (isMotorReversing()) {
            boolean temp = collect1;
            collect1 = eject1;
            eject1 = temp;
        }

        // automatically suck in balls when we first see them

            // Does our ball increment and decrement with a limited number of possible cases
            switch(ballCount) {
                case 0:
                    if (collect1 && !buffer) {
                        ballCount = 1;
                        bufferStartTime = Timer.getFPGATimestamp();
                        atMaxBallCount = false;
                    } 
                break;

                case 1:
                    if (collect1 && !buffer) {
                        ballCount = 2;
                        bufferStartTime = Timer.getFPGATimestamp();
                        atMaxBallCount = false;
                    }
                    if (eject1) {
                        ballCount = 0;
                        atMaxBallCount = false;
                    }
                break;

                case 2:
                    if (eject1) {
                        ballCount = 1;
                        ball1Color = ball2Color;
                        atMaxBallCount = false;
                    }
                    if(collect1 && !buffer) {
                        atMaxBallCount = true;
                    }
                break;
            }

            if (!buffer)  {
                lowerPrev = lower;
            }
                upperPrev = upper;

        // Sets the possible color cases of the ball
        switch(getColorSensorOutputs()) {
            case 0: 
                if(ballCount == 0) {
                    ball1Color = Color.nothing;
                    ball2Color = Color.nothing;
                }
            break;

            case 1:
                if(ballCount == 1) {
                    ball1Color = Color.red;
                    ball2Color = Color.nothing;
                } else if(ballCount == 2) {
                    ball2Color = Color.red;
                }
            break;

            case 2:
                if(ballCount == 1) {
                    ball1Color = Color.blue;
                    ball2Color = Color.nothing;
                } else if(ballCount == 2) {
                    ball2Color = Color.blue;
                }
            break;
        } 
        // Puts the ball count to the dashboard
	
        SmartDashboard.putString("ball1 color", ball1Color.toString());
        SmartDashboard.putString("ball2 color", ball2Color.toString());

        currentBallCountEntry.setNumber(ballCount);

    }
    

    public boolean getAtMaxBallCount(){
        return atMaxBallCount;
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
        indexer.set(VictorSPXControlMode.PercentOutput, power);
    }

	public void toShooter() {
		setPower(INDEX_POWER);
	}

    public boolean getLowerStatus(){
        //the ! is added here to make it trigger on enter, not on release
        return !enterSensor.get();
    }

    public boolean getUpperStatus(){
        //the ! is added here to make it trigger on enter, not on release
        return !exitSensor.get();
    }

    public int getBallCount(){
        return ballCount;
    }

    public int getColorSensorOutputs() {
        if(intakeSensor.getColor().red >= Constants.RED_THRESHOLD) {
            // SmartDashboard.putString("Color", "red");
            return 1;
        } else if(intakeSensor.getColor().blue >= Constants.BLUE_THRESHOLD) {
            // SmartDashboard.putString("Color", "blue");
            return 2;
        }        
        return 0;
    }
    
    private boolean isMotorReversing() {
        return indexer.getMotorOutputPercent() < 0;
    }

    public void stop() {
        setPower(0d);
    }
    
}