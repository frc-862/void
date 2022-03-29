// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.climber;

import java.util.function.BooleanSupplier;

import com.lightningrobotics.common.command.core.WaitCommand;
import com.lightningrobotics.voidrobot.commands.climber.arms.ArmsEngageHooks;
import com.lightningrobotics.voidrobot.commands.climber.arms.ArmsRelease;
import com.lightningrobotics.voidrobot.commands.climber.arms.ArmsToReach;
import com.lightningrobotics.voidrobot.commands.climber.pivot.PivotToHold;
import com.lightningrobotics.voidrobot.commands.climber.pivot.PivotToReach;
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
    BooleanSupplier cancelButton;

    private enum currentRung {
        mid,
        high,
        traversal,
        climbing
    }

    public NextRung(Climber climber, BooleanSupplier cancelButton) {
        this.climber = climber;
        this.cancelButton = cancelButton;

        addRequirements(climber);
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
        new SequentialCommandGroup(
            // command starts when the passive hooks are engaged on a bar

            new InstantCommand(climber::toggleManual, climber),

            new ParallelCommandGroup(
                new PivotToReach(climber),
                new ArmsToReach(climber, 0)
            ),

            //new InstantCommand(climber::pivotToHold).withTimeout(0.25), //make sure we're engaged before we start pulling up

            new PivotToHold(climber),

            new ArmsEngageHooks(climber, 1),

            new ArmsRelease(climber, 0),

            new InstantCommand(() -> toEnd = true)
        ).schedule();

        //repeat for each rung
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
        System.out.println("STOPPED _______________________________");
        climber.stop();
        climber.toggleManual();
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return toEnd | cancelButton.getAsBoolean();
    }
}
