// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.climber;

import com.lightningrobotics.common.command.core.WaitCommand;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;

public class NextRung extends CommandBase {
    Climber climber;
    boolean toEnd = false;

    private enum currentRung {
        mid,
        high,
        traversal,
        climbing
    }

    public NextRung(Climber climber) {
        this.climber = climber;

        addRequirements(climber);
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
        new SequentialCommandGroup(
            // command starts when the passive hooks are engaged on a bar

            // 
            new ParallelCommandGroup(
                new InstantCommand(climber::pivotToReach),
                new InstantCommand(() -> climber.setArmsTarget(Constants.REACH_HEIGHT, 0))
            ),

            new WaitUntilCommand(climber::onTarget),

            //new InstantCommand(climber::pivotToHold).withTimeout(0.25), //make sure we're engaged before we start pulling up

            new InstantCommand(climber::pivotToHold),

            new WaitUntilCommand(climber::pivotOnTarget),

            new InstantCommand(() -> climber.setArmsTarget(Constants.TRIGGER_HEIGHT, 1)),

            new WaitUntilCommand(climber::armsOnTarget),

            new InstantCommand(() -> climber.setArmsTarget(Constants.HOLD_HEIGHT, 0)),

            new WaitUntilCommand(climber::armsOnTarget),

            new InstantCommand(() -> toEnd = true)
        
        ).schedule();

        //repeat for each rung
    }

    @Override
    public void execute() {
        
    }
    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
        System.out.println("STOPPED _______________________________");
        climber.stop();
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return toEnd;
    }
}
