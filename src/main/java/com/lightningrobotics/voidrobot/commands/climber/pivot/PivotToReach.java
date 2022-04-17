package com.lightningrobotics.voidrobot.commands.climber.pivot;

import com.lightningrobotics.voidrobot.subsystems.ClimbArms;
import com.lightningrobotics.voidrobot.subsystems.ClimbPivots;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class PivotToReach extends CommandBase {
    ClimbPivots pivots;

    public PivotToReach(ClimbPivots pivots) {
        this.pivots = pivots;
        addRequirements(pivots);
    }

    @Override
    public void initialize() {
        pivots.pivotToReach();
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
