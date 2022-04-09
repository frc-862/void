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

    double staticHookHeight = 0d; //temporary
    double armPower = -1; //also temporary

    boolean toEnd = false;

    public ArmsEngageHooks(Climber climber) {//, int climbMode) {
        this.climber = climber;
        addRequirements(climber);
    }

    @Override
    public void initialize() {
        // climber.setClimbPower(armPower, armPower);
    }

    @Override
    public void execute() {
        // if(climber.getleftEncoder() <= staticHookHeight) {
        //     leftPower = 0;
        // } else {
        //     leftPower = armPower;
        // }

        // if(climber.getRightEncoder() <= staticHookHeight) {
        //     rightPower = 0;
        // } else {
        //     rightPower = armPower;
        // }

        climber.setClimbPower(leftPower, rightPower);

        climber.setArmsTarget(Constants.TRIGGER_HEIGHT);
    }

    @Override
    public void end(boolean interrupted) {
        climber.stopArms();
    }

    @Override
    public boolean isFinished() {
        return climber.armsOnTarget();
    }
}
