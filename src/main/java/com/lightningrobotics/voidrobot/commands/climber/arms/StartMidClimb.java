// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.climber.arms;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.ClimbArms;
import com.lightningrobotics.voidrobot.subsystems.ClimbPivots;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class StartMidClimb extends CommandBase {
    ClimbArms arms;
    ClimbPivots pivots;

    public StartMidClimb(ClimbArms arms, ClimbPivots pivots) {
        this.arms = arms;
        this.pivots = pivots;
        addRequirements(arms, pivots);
    }

    @Override
    public void initialize() {
        pivots.pivotToHold();

        arms.setTarget(Constants.MID_RUNG_VALUE);
    }

    @Override
    public void end(boolean interrupted) {
        pivots.stop();
        arms.stop();
    }

    @Override
    public boolean isFinished() {
        return arms.onTarget() && pivots.onTarget();
    }
}
