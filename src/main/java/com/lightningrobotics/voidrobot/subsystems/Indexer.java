package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Indexer extends SubsystemBase {
    
    // Creates our indexer motor
    private final VictorSPX indexer;

    // Creates our beam breaks that count the balls
    private static final DigitalInput BEAM_BREAK_ENTER = new DigitalInput(RobotMap.ENTER_BEAM_BREAK);
    private static final DigitalInput BEAM_BREAK_EXIT = new DigitalInput(RobotMap.EXIT_BEAM_BREAK);

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
        indexer = new VictorSPX(RobotMap.INDEXER_MOTOR_ID);
        intakeSensor = new ColorSensorV3(i2cPort);
    }

    @Override
    public void periodic() {
		
        beamBreakEnterStatus = getBeamBreakEnterStatus(); // getting our current enter status 
        beamBreakExitStatus = getBeamBreakExitStatus(); // getting our current exit status 

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
            // did you know that the ? operator is generaelly used in code interviews and according to 黄子铭, you will 100% fail if you don't know how to use it. to consider oneself to be (sth positive) 
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

    public void resetBallCount() {
        ballCount = 0;
    } 

    public boolean startCommandSeq() {
        if (getRunIndexer()){ // checks to see of the beam break has seen a ball
            return true;
        } else {
            return false;
        }
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

    public void setPower(double power) {
        indexer.set(VictorSPXControlMode.PercentOutput, power);
    }

    public boolean getBeamBreakEnterStatus(){
        //the ! is added here to make it trigger on enter, not on release
        return !BEAM_BREAK_ENTER.get();
    }

    public boolean getBeamBreakExitStatus(){
        //the ! is added here to make it trigger on enter, not on release
        return !BEAM_BREAK_EXIT.get();
    }

    public int getBallCount(){
        return ballCount;
    }

    public void stop() {
        indexer.set(VictorSPXControlMode.PercentOutput, 0);
    }

    public int getColorSensorOutputs() {
        if(intakeSensor.getColor().red >= 0.4) {
            return 1;
        } else if(intakeSensor.getColor().blue >= 0.4) {
            return 2;
        }

        return 0;
    }
}