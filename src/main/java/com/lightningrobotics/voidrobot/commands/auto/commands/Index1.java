// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.auto.commands;

import com.lightningrobotics.voidrobot.subsystems.Indexer;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class Index1 extends CommandBase {

    private final Indexer indexer;

    private double startTime;

    public Index1(Indexer indexer) {
        this.indexer = indexer;

        addRequirements(indexer);
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        if(indexer.getCollectedBall()) {
            startTime = Timer.getFPGATimestamp();
        }
    }

    @Override
    public void end(boolean interrupted) {
        indexer.stop();
    }

    @Override
    public boolean isFinished() {
        return false; //time = 0;
    }
}
