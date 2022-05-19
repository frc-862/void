package com.lightningrobotics.voidrobot.commands.demo;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Indexer;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class DemoIndexer extends CommandBase {
    
    private Indexer indexer;
    private DoubleSupplier power;
    
    public DemoIndexer(Indexer indexer, DoubleSupplier pwr) {
        this.indexer = indexer;
        this.power = pwr;

        addRequirements(indexer);
    }

    @Override
    public void initialize() {}
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
        return false;
    }
}
