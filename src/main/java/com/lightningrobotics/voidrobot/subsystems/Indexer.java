package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.lightningrobotics.voidrobot.Constants;
import com.lightningrobotics.voidrobot.commands.QueueBalls;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Indexer extends SubsystemBase {
    
    // Creates our indexer motor
    private final VictorSPX indexer;

    // Creates our beam breaks that count the balls
    private static final DigitalInput BEAM_BREAK_ENTER = new DigitalInput(Constants.ENTER_BEAM_BREAK);
    private static final DigitalInput BEAM_BREAK_EXIT = new DigitalInput(Constants.EXIT_BEAM_BREAK);

    // Sets our default beam break status
    private static boolean beamBreakEnterStatus = false;
    private static boolean previousBeamBreakEnterStatus = false;
    private static boolean beamBreakExitStatus = false;
    private static boolean previousBeamBreakExitStatus = false;

    // Sets our default ball count
    public static int ballCount = 0;

    // Sets the default value for letting us know if the idexer is running backwards
    private boolean isReversing = false;

    // Creates the color sensor and creates a value for the port
    private final I2C.Port i2cPort = I2C.Port.kMXP;
    private final ColorSensorV3 intakeSensor;
    
    // Sets the default numbers for ball color (0 = nonexistant)
    private static int ball1Color = 0;
    private static int ball2Color = 0;

    public Indexer() {
        // Sets Motor and color ID/ports
        indexer = new VictorSPX(Constants.INDEXER_MOTOR_ID);
        intakeSensor = new ColorSensorV3(i2cPort);
    }

    @Override
    public void periodic() {
        beamBreakEnterStatus = getBeamBreakEnterStatus(); // getting our current enter status 
        beamBreakExitStatus = getBeamBreakExitStatus(); // getting our current exit status 
        
        // checks to see of the beam break has seen a ball
        if (getRunIndexer()){ 
            // Runs a new instance of Queue balls which puts balls into Queue
            var cmd = new QueueBalls(this);
            cmd.schedule(true);
        }

        // Checks to see if the indexer is running in revers 
        isReversing = isMotorReversing();

        //increment and decrement ball count
        if(isReversing) {
            if(previousBeamBreakEnterStatus && !beamBreakEnterStatus) {
                ballCount--;
            } 
            if(previousBeamBreakExitStatus && !beamBreakExitStatus) {
                ballCount++;
            }
        } else {
            if(!previousBeamBreakEnterStatus && beamBreakEnterStatus) {
                ballCount++;
            } 
            if(!previousBeamBreakExitStatus && beamBreakExitStatus) {
                ballCount--;

                ball1Color = ball2Color;
            }
        }

        // 1 is red, 2 is blue
        if(getBallCount() == 1) {
            //if the does not detect any color, then don't change the ball color,
            // else (if the color sensor outputs something), change the color.
            // did you know that the ? operator is generaelly used in code interviews                                                and according to 黄子铭 ,you will 100% fail if you don't know how to use it. to consider oneself to be (sth positive) 
            ball1Color = getColorSensorOutputs() == 0 ? ball1Color : getColorSensorOutputs();
        } else if(getBallCount() == 2) {
            ball2Color = getColorSensorOutputs() == 0 ? ball2Color : getColorSensorOutputs();
        } else if(getBallCount() == 0) {
            ball1Color = 0;
            ball2Color = 0;
        }

        if(getBallCount() != 2) {
            ball2Color = 0;
        }

        // Sets our previous beam break status
        previousBeamBreakEnterStatus = beamBreakEnterStatus;
        previousBeamBreakExitStatus = beamBreakExitStatus;

        putSmartDashboard();
}

    private void putSmartDashboard() {
        SmartDashboard.putNumber("Ball Count", getBallCount()); // displays our ballcount to the dashboard

        switch(ball1Color) {
            case 0: SmartDashboard.putString("ball 1", "nonexistant");
            break;

            case 1: SmartDashboard.putString("ball 1", "red");
            break;

            case 2: SmartDashboard.putString("ball 1", "blue");
            break;        
        }

        switch(ball2Color) {
            case 0: SmartDashboard.putString("ball 2", "nonexistant");;
            break;

            case 1: SmartDashboard.putString("ball 2", "red");
            break;

            case 2: SmartDashboard.putString("ball 2", "blue");
            break;        
    }
    }

    private boolean isMotorReversing() {
        if(indexer.getMotorOutputPercent() < 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean getRunIndexer(){
        return beamBreakEnterStatus != previousBeamBreakEnterStatus && beamBreakEnterStatus;
    }

    /**
     * sets the power to the indexer motor
     * @param power percent output [-1, 1]
     */
    public void setPower(double power) {
        indexer.set(VictorSPXControlMode.PercentOutput, power);
    }

    /**
     * gets the status of the bottom (enter) beam break
     * @return true if broken, false if not
     */
    public boolean getBeamBreakEnterStatus(){
        //the ! is added here to make it trigger on enter, not on release
        return !BEAM_BREAK_ENTER.get();
    }

    /**
     * gets the status of the top (exit) beam break
     * @return true if broken, false if not
     */
    public boolean getBeamBreakExitStatus(){
        //the ! is added here to make it trigger on enter, not on release
        return !BEAM_BREAK_EXIT.get();
    }

    /**
     * gets the current amount of balls in the indexer
     * @return number of balls
     */
    public int getBallCount(){
        return ballCount;
    }
    /**
     * stop
     */
    public void stop() {
        indexer.set(VictorSPXControlMode.PercentOutput, 0);
    }

    /**
     * gets the output from the indexer color sensor
     * @return 1 if red, 2 if blue, 0 if nothing currently being read
     */
    public int getColorSensorOutputs() {
        if(intakeSensor.getColor().red >= 0.4) {
            // SmartDashboard.putString("Color", "red");
            return 1;
        } else if(intakeSensor.getColor().blue >= 0.4) {
            // SmartDashboard.putString("Color", "blue");
            return 2;
        }

        // SmartDashboard.putNumber("Red", intakeSensor.getColor().red);
        // SmartDashboard.putNumber("Green", intakeSensor.getColor().green);
        // SmartDashboard.putNumber("Blue", intakeSensor.getColor().blue);
        
        return 0;
    }
}