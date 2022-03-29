// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.climber.arms;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Climber;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ArmsEngageHooks extends CommandBase {
    Climber climber;
    // int climbMode;
    double leftPower;
    double rightPower;

    double thing1 = 10000; //temporary
    double thing2 = -1; //also temporary

    boolean toEnd = false;

    public ArmsEngageHooks(Climber climber) {//, int climbMode) {
        this.climber = climber;
        // this.climbMode = climbMode;
        // addRequirements(climber);
    }

    @Override
    public void initialize() {
        // climber.setArmsTarget(Constants.TRIGGER_HEIGHT, climbMode);

        climber.setClimbPower(thing2, thing2);
    }

    @Override
    public void execute() {
        if(climber.getleftEncoder() <= thing1) {
            leftPower = 0;
        } else {
            leftPower = thing2;
        }

        if(climber.getRightEncoder() <= thing1) {
            rightPower = 0;
        } else {
            rightPower = thing2;
        }

        climber.setClimbPowerManual(leftPower, rightPower);
    }

    @Override
    public void end(boolean interrupted) {
        climber.stopArms();
    }

    @Override
    public boolean isFinished() {
        return climber.getleftEncoder() <= thing1 && climber.getRightEncoder() <= thing1;
    }
}
