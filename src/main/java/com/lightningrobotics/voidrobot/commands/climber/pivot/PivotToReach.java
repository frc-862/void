package com.lightningrobotics.voidrobot.commands.climber.pivot;

import com.lightningrobotics.voidrobot.subsystems.Climber;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class PivotToReach extends CommandBase {
    Climber climber;

    public PivotToReach(Climber climber) {
        this.climber = climber;
    }

    @Override
    public void initialize() {
        climber.pivotToReach();
    }

    @Override
    public void end(boolean interrupted) {
        climber.stopPivot();
    }

    @Override
    public boolean isFinished() {
        return climber.pivotOnTarget();
    }
}
