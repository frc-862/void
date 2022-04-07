package com.lightningrobotics.voidrobot.commands.indexer;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class RunIndexer extends CommandBase {

    // Creates the indexer subsystem
    private final Indexer indexer;
    private final Shooter shooter;
    // The power we want to supply to the indexer
    private DoubleSupplier power;

    

    public RunIndexer(Indexer indexer, Shooter shooter, DoubleSupplier power) {
        this.indexer = indexer;
        this.power = power; // The power that is comming from the controller
        this.shooter = shooter;

        addRequirements(indexer); 
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        if (indexer.getExitStatusNoDebounce() && shooter.getCurrentRPM() < 1000 && power.getAsDouble() >= 0) { 
            indexer.setPower(0);
        }
        else {
            indexer.setPower(power.getAsDouble()); // Gets the supplied power, and sets it to the indexer motor
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