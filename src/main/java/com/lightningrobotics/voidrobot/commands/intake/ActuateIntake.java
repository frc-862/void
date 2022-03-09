// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.intake;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Intake;

import edu.wpi.first.hal.simulation.ConstBufferCallback;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class ActuateIntake extends CommandBase {

    private Intake intake;
    private Indexer indexer;
    private double power;


    public ActuateIntake(Intake intake, Indexer indexer, double power) {
        this.intake = intake;
        this.indexer = indexer;
        this.power = power;

        addRequirements(intake);

    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        indexer.setAutoIndex(false);
        if(indexer.getLowerStatus() && power < 0){
            end(false);
        }
        else {
            intake.actuateIntake(power);
        }
    }

    @Override
    public void end(boolean interrupted) {
        intake.stopDeploy();
        indexer.setAutoIndex(true);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
