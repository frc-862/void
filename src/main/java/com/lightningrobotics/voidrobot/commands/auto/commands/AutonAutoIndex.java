package com.lightningrobotics.voidrobot.commands.auto.commands;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Indexer;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutonAutoIndex extends CommandBase {

    // Creates our indexer subsystem
    private final Indexer indexer;

    private double indexTimeBall1 = 0.35d; // 0.185d; // The time we want the indexer to index in seconds
    private double startIndexTime = 0d; // Setting a default start time of 0

	private boolean isStopped = false;

    public AutonAutoIndex(Indexer indexer) {
		this.indexer = indexer;

		// addRequirements(indexer); TODO maybe fix
	}

    @Override
    public void initialize() {}

    @Override
    public void execute() {

        if (indexer.getCollectedBall()) { // && indexer.getAutoIndex()
            startIndexTime = Timer.getFPGATimestamp();
        }

        if (indexer.getBallCount() == 0) {
            indexer.setPower(0.2);
        }     
        else if(indexer.getBallCount() == 1 && Timer.getFPGATimestamp() - startIndexTime < indexTimeBall1) {
            indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
        } else {
            indexer.stop();
            isStopped = true;
        }
    }

    @Override
    public void end(boolean interrupted) {
        indexer.stop();
    }

    @Override
    public boolean isFinished() {
        return isStopped;
    }
}