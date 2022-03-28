package com.lightningrobotics.voidrobot.commands.auto.commands;

import com.lightningrobotics.voidrobot.subsystems.Indexer;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutonIndexeCargo extends CommandBase {

private final Indexer indexer;

    public AutonIndexeCargo(Indexer indexer) {
        this.indexer = indexer;

        addRequirements(indexer);

    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        indexer.setPower(0.5d);
    }

    @Override
    public void end(boolean interrupted) {
        indexer.stop();
    }

    @Override
    public boolean isFinished() {
        return indexer.getColorSensor().getProximity() > 350; // indexer.getColorSensor().getProximity() > 350 && indexer.getBallCount = 2;
    }
}