// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.climber;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Climber;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class runClimb extends CommandBase {
    public final Climber climber;
    private DoubleSupplier leftPower;
    private DoubleSupplier rightPower;

    public runClimb(Climber climber, DoubleSupplier leftPower, DoubleSupplier rightPower) {
        this.climber = climber;
        this.leftPower = leftPower;
        this.rightPower = rightPower;

        addRequirements(climber);
    }

    @Override
    public void execute() {
        climber.setPower(leftPower.getAsDouble(), rightPower.getAsDouble());
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
