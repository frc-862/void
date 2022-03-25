// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.climber;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Climber;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class NextRung extends CommandBase {
    Climber climber;

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
            new ParallelCommandGroup(
                new RunCommand(climber::pivotToReach),
                new RunCommand(() -> climber.setArmsTarget(Constants.REACH_HEIGHT, 0))
            ).until(climber::onTarget),

            new ParallelCommandGroup(
                new InstantCommand(climber::pivotToHold),
                new InstantCommand(() -> climber.setArmsTarget(Constants.HOLD_HEIGHT, 1))
            ).until(climber::onTarget)
        );

        //repeat for each rung
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
        climber.stop();
        super.end(interrupted);
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return false;
    }
}
