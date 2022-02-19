package com.lightningrobotics.voidrobot.commands;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Indexer;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class QueueBalls extends CommandBase {

    private Indexer indexer;
    private static double indexTime = 0.5d; // in seconds
    private static double startIndexTime = 0d;

    public QueueBalls(Indexer indexer) {
        this.indexer = indexer;
        addRequirements(indexer);
    }

    @Override
    public void initialize() {
        startIndexTime = Timer.getFPGATimestamp();
    }

    @Override
    public void execute() {
        indexer.setPower(0.2);
    }

    @Override
    public void end(boolean interrupted) {
        indexer.stop();
    }

    @Override
    public boolean isFinished() {
        return Timer.getFPGATimestamp() - startIndexTime > indexTime;
    }
}