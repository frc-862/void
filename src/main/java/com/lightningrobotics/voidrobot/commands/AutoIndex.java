package com.lightningrobotics.voidrobot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Indexer;

public class AutoIndex extends CommandBase {
    // TODO: refacter this command to run in parallel with vision alin (im assumming that is the main functionality of this class)

    // Creates our indexer subsystem
    private final Indexer indexer;

    // The power that will be set to the motor
    private final DoubleSupplier power;


    public AutoIndex(Indexer indexer, DoubleSupplier Power) {
        this.indexer = indexer;
        this.power = Power;


        addRequirements(indexer);
    }    

    @Override
    public void execute() {
        indexer.setPower(power.getAsDouble());
    }

    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
        indexer.stop();
    }

}