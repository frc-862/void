package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.subsystems.Indexer;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class BallsToShooter extends CommandBase {

    // Creates our indexer subsystem
    private Indexer indexer;
    private static double indexTime1Ball = 1d; // 0.185d; // The time we want the indexer to index in seconds
    private static double indexTime2Balls = 1d; // The time we want the indexer to index in seconds
    //TODO: tune these ^
    private static double startIndexTime = 0d; // Setting a default start time of 0

    private static double power = 0.75; // the power we want the indexer to run at

    public BallsToShooter(Indexer indexer) {
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
        if(indexer.getBallCount() == 1) {
            return Timer.getFPGATimestamp() - startIndexTime > indexTime1Ball;
        }
         else if(indexer.getBallCount() == 2) {
            return Timer.getFPGATimestamp() - startIndexTime > indexTime2Balls; // Checks to see if we have reached the amount of time we want to index, then stops
        } 
        else {
            return true;
        }
    }
}