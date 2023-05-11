package com.lightningrobotics.voidrobot.commands.demo;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Intake;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class DemoIntake extends CommandBase {
    
    private Intake intake;
    private DoubleSupplier power;
    
    public DemoIntake(Intake intake, DoubleSupplier pwr) {
        this.intake = intake;
        this.power = pwr;

        addRequirements(intake);
    }

    @Override
    public void initialize() {
        intake.actuateIntake(1d);
    }
    @Override
    public void execute() {
        intake.setPower(power.getAsDouble());
    }

    @Override
    public void end(boolean interrupted) {
        intake.stop();
        intake.actuateIntake(-1d);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
