package com.lightningrobotics.voidrobot.commands.climber;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Climber;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ManualClimb extends CommandBase {

    public final Climber climber;

    private DoubleSupplier leftClimbPower;
    private DoubleSupplier rightClimbPower;
    private DoubleSupplier rightPivotPower;
    private DoubleSupplier leftPivotPower;

    private boolean zeroBool = true;

    public ManualClimb(Climber climber, DoubleSupplier leftClimbPower, DoubleSupplier rightClimbPower, DoubleSupplier leftPivotPower, DoubleSupplier rightPivotPower) {
        this.climber = climber;
        this.leftClimbPower = leftClimbPower;
        this.rightClimbPower = rightClimbPower;
        this.rightPivotPower = rightPivotPower;
        this.leftPivotPower = leftPivotPower;

        addRequirements(climber);
    }

    @Override
    public void execute() {
        climber.setClimbPower(leftClimbPower.getAsDouble(), rightClimbPower.getAsDouble());
        if(rightPivotPower.getAsDouble() != 0 || leftPivotPower.getAsDouble() !=0) {
            // climber.setPivotPower(rightPivotPower.getAsDouble(), leftPivotPower.getAsDouble());
            zeroBool = true;
        } else if(zeroBool) {
            // climber.setPivotPower(0, 0);
            zeroBool = false;
        }

        // climber.setPivotPower(leftPivotPower.getAsDouble(), rightPivotPower.getAsDouble());
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
