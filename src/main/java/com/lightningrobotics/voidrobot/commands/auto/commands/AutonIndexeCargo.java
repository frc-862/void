package com.lightningrobotics.voidrobot.commands.auto.commands;

import java.lang.invoke.ConstantBootstraps;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Indexer;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutonIndexeCargo extends CommandBase {

    private final Indexer indexer;
    private int ballsWanted = 0;

    public AutonIndexeCargo(Indexer indexer, int ballsWanted) {
        this.indexer = indexer;
        this.ballsWanted = ballsWanted;

        addRequirements(indexer);

    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        indexer.setPower(Constants.AUTON_CENTER_POWER);
    }

    @Override
    public void end(boolean interrupted) {
        indexer.stop();
    }

    @Override
    public boolean isFinished() {
        return indexer.getBallCount() == ballsWanted; // indexer.getColorSensor().getProximity() > 350 && 
    }
}