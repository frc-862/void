package com.lightningrobotics.voidrobot.commands;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Intake;

import edu.wpi.first.wpilibj2.command.CommandBase;

import edu.wpi.first.wpilibj.Timer;

public class RunIntake extends CommandBase {
    
    // Creates the intake subsystem
    private final Intake intake;
    // Sets a constant power that we want to supply to the intake motor
    private static final double power = 0.5;

    public RunIntake(Intake intake) {
        this.intake = intake;

        addRequirements(intake);
    }    

    @Override
    public void execute() {
        intake.setPower(power); // Sets the power to the intake motor
    }

    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
        intake.stop();
    }
}
