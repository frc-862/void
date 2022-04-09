package com.lightningrobotics.voidrobot.commands.intake;

import com.lightningrobotics.voidrobot.constants.Constants;
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
        if (intake.getBumperSensor()) {
            intake.actuateIntake(0d);
        } else{
            intake.actuateIntake(-Constants.DEFAULT_INTAKE_WINCH_POWER);
        } 
    }

    @Override
    public void end(boolean interrupted) {
        intake.actuateIntake(0d);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
