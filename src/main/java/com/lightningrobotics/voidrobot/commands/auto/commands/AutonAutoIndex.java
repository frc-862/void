package com.lightningrobotics.voidrobot.commands.auto.commands;

import com.lightningrobotics.voidrobot.subsystems.Indexer;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutonAutoIndex extends CommandBase {

    // Creates our indexer subsystem
    private final Indexer indexer;

    private double indexTimeBall1 = 0.35d; // 0.185d; // The time we want the indexer to index in seconds
    private double indexTimeBall2 = 0.275d; // The time we want the indexer to index in seconds
    private double startIndexTime = 0d; // Setting a default start time of 0

    private double power = 1; // the power we want the indexer to run at

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

        if(indexer.getBallCount() == 1 && Timer.getFPGATimestamp() - startIndexTime < indexTimeBall1) {
            indexer.setPower(power);
			isStopped = false;
        } 
        else if(indexer.getBallCount() == 2 && Timer.getFPGATimestamp() - startIndexTime < indexTimeBall2 && indexer.getBallCount() != 2) { // Checks to see if we have reached the amount of time we want to index, then stops
            indexer.setPower(power);
			isStopped = false;
        } 
		else if(!isStopped){
            indexer.setPower(0);
			isStopped = true;
        } 
    }

    @Override
    public void end(boolean interrupted) {
        indexer.stop();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}