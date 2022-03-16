package com.lightningrobotics.voidrobot.commands.indexer;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Indexer;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class RunIndexer extends CommandBase {

    // Creates the indexer subsystem
    private final Indexer indexer;

    // The power we want to supply to the indexer
    private DoubleSupplier power;

    public RunIndexer(Indexer indexer, DoubleSupplier power) {
        this.indexer = indexer;
        this.power = power; // The power that is comming from the controller

        addRequirements(indexer);
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        indexer.setPower(power.getAsDouble()); // Gets the supplied power, and sets it to the indexer motor
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