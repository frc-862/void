package com.lightningrobotics.voidrobot.commands.climber;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Climber;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class runClimb extends CommandBase {
    
    public final Climber climber;

    private DoubleSupplier leftClimbPower;
    private DoubleSupplier rightClimbPower;
    private DoubleSupplier rightPivotPower;

    public runClimb(Climber climber, DoubleSupplier leftClimbPower, DoubleSupplier rightClimbPower, DoubleSupplier pivotPower) {
        this.climber = climber;
        this.leftClimbPower = leftClimbPower;
        this.rightClimbPower = rightClimbPower;
        this.rightPivotPower = pivotPower;

        addRequirements(climber);
    }

    @Override
    public void execute() {
        climber.setClimbPower(leftClimbPower.getAsDouble(), rightClimbPower.getAsDouble());
        climber.setPivotPower(rightPivotPower.getAsDouble());
    }

    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
        climber.stop();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
