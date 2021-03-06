package com.lightningrobotics.voidrobot.commands.intake;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Intake;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class MoveIntake extends CommandBase {

    private final Intake intake;
    
    private DoubleSupplier power;

    public MoveIntake(Intake intake, DoubleSupplier power) {
        this.intake = intake;
        this.power = power;

        addRequirements(intake);
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        intake.actuateIntake(power.getAsDouble());
    }

    @Override
    public void end(boolean interrupted) {
        intake.stopDeploy();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
