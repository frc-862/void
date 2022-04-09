package com.lightningrobotics.voidrobot.commands.intake;

import com.lightningrobotics.voidrobot.subsystems.Intake;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class SafeRetrackIntake extends CommandBase {

    private final Intake intake;

    public SafeRetrackIntake(Intake intake) {
        this.intake = intake;

        addRequirements(intake);
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        intake.actuateIntake(-1d);
    }

    @Override
    public void end(boolean interrupted) {
        intake.actuateIntake(0d);
    }

    @Override
    public boolean isFinished() {
        return intake.getBumperSensor();
    }
}
