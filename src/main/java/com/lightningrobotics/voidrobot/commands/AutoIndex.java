/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.lightningrobotics.voidrobot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import com.lightningrobotics.voidrobot.subsystems.Indexer;

public class AutoIndex extends CommandBase {

    double indexTimer = 0d;

    boolean ballPassed = false;

    final Indexer indexer;

    /**
     * Creates a new Collect_Eject.
     */
    public AutoIndex(Indexer indexer) {
        this.indexer = indexer;

        addRequirements(indexer);
    }    

    @Override
    public void execute() {
        
        if((Timer.getFPGATimestamp() - indexTimer) < 0.19d) {
            indexer.setPower(1d);
        } else {
            indexer.stop();
        }

    }

    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
        indexer.stop();
    }

}