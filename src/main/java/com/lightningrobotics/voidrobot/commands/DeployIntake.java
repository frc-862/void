package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.subsystems.Intake;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class DeployIntake extends CommandBase {

    // TODO: get on this when we know the true deployment method of the collector

    private Intake intake;

    public DeployIntake(Intake intake) {
        this.intake = intake;

        addRequirements(intake);
    }    

    @Override
    public void execute() {
        
    }

    @Override
    public void end(boolean interrupted) {
        
    }
}
