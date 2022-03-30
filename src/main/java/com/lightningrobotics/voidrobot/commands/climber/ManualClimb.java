package com.lightningrobotics.voidrobot.commands.climber;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Climber;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ManualClimb extends CommandBase {

    public final Climber climber;

    private DoubleSupplier leftClimbPower;
    private DoubleSupplier rightClimbPower;

    private boolean zeroBool = true;

    public ManualClimb(Climber climber, DoubleSupplier leftClimbPower, DoubleSupplier rightClimbPower) {
        this.climber = climber;
        this.leftClimbPower = leftClimbPower;
        this.rightClimbPower = rightClimbPower;

        addRequirements(climber);
    }

    @Override
    public void execute() {
        climber.setClimbPower(
            MathUtil.applyDeadband(leftClimbPower.getAsDouble(), 0.1),
            MathUtil.applyDeadband(rightClimbPower.getAsDouble(), 0.1));
    }

    @Override
    public void end(boolean interrupted) {
        climber.stop();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
