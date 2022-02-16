package com.lightningrobotics.voidrobot.commands;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Indexer;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class RunIndexer extends CommandBase {

    private Indexer indexer;
    private static double indexTime = 0.5d; // in seconds
    private static double startIndexTime = 0d;

    private DoubleSupplier power;

    public RunIndexer(Indexer indexer) {
            this.indexer = indexer;

            addRequirements(indexer);
        }

    public RunIndexer(Indexer indexer, DoubleSupplier power) {
        this.indexer = indexer;
        this.power = power;

        addRequirements(indexer);
    }

    @Override
    public void initialize() {
        startIndexTime = Timer.getFPGATimestamp();
    }

    @Override
    public void execute() {
        indexer.setPower(power.getAsDouble());
    }

    @Override
    public void end(boolean interrupted) {
        indexer.stop();
    }

    @Override
    public boolean isFinished() {
        return false; //Timer.getFPGATimestamp() - startIndexTime > indexTime;
    }
}