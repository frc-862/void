// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.climber.arms;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.ClimbArms;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class ArmsEngageHooks extends CommandBase {
    ClimbArms arms;

    boolean toEnd = false;

    public ArmsEngageHooks(ClimbArms arms) {
        this.arms = arms;
        addRequirements(arms);
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        arms.setTarget(Constants.TRIGGER_HEIGHT);
    }

    @Override
    public void end(boolean interrupted) {
        arms.stop();
    }

    @Override
    public boolean isFinished() {
        return arms.onTarget();
    }
}
