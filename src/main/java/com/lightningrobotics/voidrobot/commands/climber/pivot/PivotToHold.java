// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.climber.pivot;

import com.lightningrobotics.voidrobot.subsystems.Climber;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class PivotToHold extends CommandBase {
    Climber climber;

    public PivotToHold(Climber climber) {
        this.climber = climber;
        //addRequirements(climber); //TODO: make a pivot subsystem
    }

    @Override
    public void initialize() {
        climber.pivotToHold();
    }

    @Override
    public void end(boolean interrupted) {
        //climber.stopPivot(); //Not sure if this should be here or not????
    }

    @Override
    public boolean isFinished() {
        return climber.pivotOnTarget();
    }
}
