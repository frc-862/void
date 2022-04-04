// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.climber.arms;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Climber;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class StartMidClimb extends CommandBase {
    Climber climber;

    public StartMidClimb(Climber climber) {
        this.climber = climber;
        addRequirements(climber);
    }

    @Override
    public void initialize() {
        climber.pivotToHold();

        climber.setArmsTarget(Constants.MID_RUNG_VALUE);
    }

    @Override
    public void end(boolean interrupted) {
        climber.stop();
    }

    @Override
    public boolean isFinished() {
        return climber.onTarget();
    }
}
