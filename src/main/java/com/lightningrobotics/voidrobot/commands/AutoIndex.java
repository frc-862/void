package com.lightningrobotics.voidrobot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import com.lightningrobotics.voidrobot.subsystems.Indexer;

public class AutoIndex extends CommandBase {
    // TODO: refacter this command to run in parallel with vision alin (im assumming that is the main functionality of this class)

    private double indexTimer = 0d;

    private boolean ballPassed = false;

    private final double power;

    private final Indexer indexer;

    public AutoIndex(Indexer indexer, double power) {
        this.indexer = indexer;
        this.power = power;
        addRequirements(indexer);
    }    

    @Override
    public void execute() {

        indexer.setPower(power);

        // TODO: I dont think this block achives the true functionality of this command (QueBalls does this)
        // if((Timer.getFPGATimestamp() - indexTimer) < 0.19d) {
        //     indexer.setPower(1d);
        // } else {
        //     indexer.stop();
        // }

    }

    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
        indexer.stop();
    }

}