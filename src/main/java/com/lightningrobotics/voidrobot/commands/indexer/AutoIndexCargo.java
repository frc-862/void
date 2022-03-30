package com.lightningrobotics.voidrobot.commands.indexer;

import javax.swing.plaf.basic.BasicTreeUI.TreeCancelEditingAction;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Indexer;

import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutoIndexCargo extends CommandBase {

    // Creates our indexer subsystem
    private final Indexer indexer;
    private boolean colorProximity = false;

    public enum State{
        WAITING0,
        COLLECT1,
        WAITING1,
        WAITING2,
        COLLECT2PART1,
        COLLECT2PART2,
    }

    State state = State.WAITING1;

    public AutoIndexCargo(Indexer indexer) {
		this.indexer = indexer;

		addRequirements(indexer);
	}

    @Override
    public void initialize() {
        state = indexer.getBallCount() == 0 ? State.WAITING0 : (indexer.getBallCount() == 1 ? State.WAITING1 : State.WAITING2);
    }

    @Override
    public void execute() {
        colorProximity = indexer.getColorSensor().getProximity() > 350;

        switch (state) {
            case WAITING0:
                indexer.stop();
                if (indexer.getCollectedBall()) {
                    state = State.COLLECT1;
                } 
            break;

            case COLLECT1:
                indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
                if (colorProximity) {
                    state = State.WAITING1;
                }
            break;

            case COLLECT2PART1:
                indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
                if (!colorProximity) {
                    state = State.COLLECT2PART2;
                }
            break;

            case COLLECT2PART2:
                indexer.setPower(Constants.DEFAULT_INDEXER_POWER);
                if (colorProximity) {
                    state = State.WAITING2;
                }
            break;

            case WAITING1:
                indexer.stop();
                if (indexer.getCollectedBall()) {
                    state = State.COLLECT2PART1;
                } else if (indexer.getBallCount() == 0) {
                    state = State.WAITING0;
                }
            break;

            case WAITING2:
                indexer.stop();
                if (indexer.getBallCount() == 1) {
                    state = State.WAITING1;
                } else if (indexer.getBallCount() == 0) {
                    state = State.WAITING0;
                }
            break;
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