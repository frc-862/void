package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.lightningrobotics.voidrobot.Constants;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Indexer extends SubsystemBase {
    
    // VictorSPX to run

    private static final DigitalInput BEAM_BREAK_ENTER = new DigitalInput(Constants.ENTER_BEAM_BREAK);
    private static final DigitalInput BEAM_BREAK_EXIT = new DigitalInput(Constants.EXIT_BEAM_BREAK);

    private static boolean beamBreakEnterStatus = false;
    private static boolean previousBeamBreakEnterStatus = false;
    private static boolean beamBreakExitStatus = false;
    private static boolean previousBeamBreakExitStatus = false;

    private static boolean runIndexer = false;

    public static int ballCount = 0;

    private final VictorSPX indexer;

    private final I2C.Port i2cPort = I2C.Port.kMXP;
    private final ColorSensorV3 intakeSensor;

    public Indexer() {
        indexer = new VictorSPX(Constants.INDEXER_MOTOR_ID);
        intakeSensor = new ColorSensorV3(i2cPort);
    }

    @Override
    public void periodic() {
        beamBreakEnterStatus = getBeamBreakEnterStatus(); // getting our current enter status 
        beamBreakExitStatus = getBeamBreakExitStatus(); // getting our current exit status 
        
        if (beamBreakEnterStatus != previousBeamBreakEnterStatus && beamBreakEnterStatus){ // checks to see of the beam break has seen a ball
            runIndexer = true; 
        }
        // if (beamBreakExitStatus != previousBeamBreakExitStatus && beamBreakExitStatus){ // checks to see of the beam break has seen a ball
        //  ballCount--;
        // }

        if(previousBeamBreakEnterStatus && !beamBreakEnterStatus) {
            ballCount++;
        } 
        if (previousBeamBreakExitStatus && !beamBreakExitStatus) {
            ballCount--;
        }
        
        previousBeamBreakEnterStatus = beamBreakEnterStatus;
        previousBeamBreakExitStatus = beamBreakExitStatus;
    
        SmartDashboard.putNumber("Ball Count", getBallCount()); // displays our ballcount to the dashboard

        // for the top todo
        SmartDashboard.putBoolean("enter beam break", BEAM_BREAK_ENTER.get());

    }

    public boolean getRunIndexer(){
        return runIndexer;
    }

    public void setRunIndexer(boolean running){
        runIndexer = running;
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

    public void closeBeamBreaks(){
        BEAM_BREAK_ENTER.close();
        BEAM_BREAK_EXIT.close();
    }

    public int getBallCount(){
        return ballCount;
    }
    
    public void stop() {
        indexer.set(VictorSPXControlMode.PercentOutput, 0);
    }

    public int getColorSensorOutputs() {
        //TODO: make 255
        if(intakeSensor.getColor().red >= 0.4) {
            SmartDashboard.putString("Color", "red");
            return 1;
        } else if(intakeSensor.getColor().blue >= 0.4) {
            SmartDashboard.putString("Color", "blue");
            return 2;
        }

        // SmartDashboard.putNumber("Red", intakeSensor.getColor().red);
        // SmartDashboard.putNumber("Green", intakeSensor.getColor().green);
        // SmartDashboard.putNumber("Blue", intakeSensor.getColor().blue);
        
        return 0;
    }
}