package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.subsystems.Intake;

import edu.wpi.first.wpilibj2.command.CommandBase;

import edu.wpi.first.wpilibj.Timer;

public class RunIntake extends CommandBase {
    final Intake intake;

    double indexTimer = 0d;

    public RunIntake(Intake intake) {
        this.intake = intake;

        addRequirements(intake);
    }    

    @Override
    public void execute() {
        
        if((Timer.getFPGATimestamp() - indexTimer) < 0.19d) {
            intake.runIntake(1d);
        } else {
            intake.stopIntake();

        }

    }

    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
        intake.stopIntake();
    }
}
