// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.climber.pivot;

import com.lightningrobotics.voidrobot.subsystems.ClimbArms;
import com.lightningrobotics.voidrobot.subsystems.ClimbPivots;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class PivotToHold extends CommandBase {
    ClimbPivots pivots;

    public PivotToHold(ClimbPivots pivots) {
        this.pivots = pivots;
        addRequirements(pivots);
    }

    @Override
    public void initialize() {
        pivots.pivotToHold();
    }

    @Override
    public void end(boolean interrupted) {
        pivots.stop();
    }

    @Override
    public boolean isFinished() {
        return pivots.onTarget();
    }
}
