package com.lightningrobotics.voidrobot.commands.climber;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.ClimbArms;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ManualClimb extends CommandBase {

    public final ClimbArms arms;

    private DoubleSupplier leftClimbPower;
    private DoubleSupplier rightClimbPower;

    private boolean zeroBool = true;

    public ManualClimb(ClimbArms arms, DoubleSupplier leftClimbPower, DoubleSupplier rightClimbPower) {
        this.arms = arms;
        this.leftClimbPower = leftClimbPower;
        this.rightClimbPower = rightClimbPower;

        addRequirements(arms);
    }

    @Override
    public void execute() {
        arms.setPower(
            MathUtil.applyDeadband(leftClimbPower.getAsDouble(), 0.1),
            MathUtil.applyDeadband(rightClimbPower.getAsDouble(), 0.1));
    }

    @Override
    public void end(boolean interrupted) {
        arms.stop();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
