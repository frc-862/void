package com.lightningrobotics.voidrobot.commands;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Indexer;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class QueueBalls extends CommandBase {

    // Creates our indexer subsystem
    private Indexer indexer;
    private static double indexTimeBall1 = 0.185d; // The time we want the indexer to index in seconds
    private static double indexTimeBall2 = 0.2d; // The time we want the indexer to index in seconds
    private static double startIndexTime = 0d; // Setting a default start time of 0

    private static double power = 0.75; // the power we want the indexer to run at

    public QueueBalls(Indexer indexer) {
            this.indexer = indexer;

            addRequirements(indexer);
        }

    @Override
    public void initialize() {
        startIndexTime = Timer.getFPGATimestamp(); // Gets the current run time of the robot
    }

    @Override
    public void execute() {
        indexer.setPower(power);
    }

    @Override
    public void end(boolean interrupted) {
        indexer.stop();
    }

    @Override
    public boolean isFinished() {
        if(indexer.getBallCount() == 0) {
            return Timer.getFPGATimestamp() - startIndexTime > indexTimeBall1;
        }
         else if(indexer.getBallCount() == 1) {
            return /*indexer.getBeamBreakExitStatus();*/Timer.getFPGATimestamp() - startIndexTime > indexTimeBall2; // Checks to see if we have reached the amount of time we want to index, then stops
        } 
        else {
            return true;
        }
    }
}