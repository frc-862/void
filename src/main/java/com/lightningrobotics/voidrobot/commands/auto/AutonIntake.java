package com.lightningrobotics.voidrobot.commands.auto;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Intake;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutonIntake extends CommandBase {
    private final Intake intake;
    private double power;
    private Indexer indexer;

    public AutonIntake(Intake intake, Indexer indexer, double power) {
        this.intake = intake;
        this.indexer = indexer;
        this.power = power;

        addRequirements(intake);
    }    

    @Override
    public void execute() {
        intake.setPower(power);
    }

    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
        intake.stop();
    }

    @Override
    public boolean isFinished(){
        return indexer.getCollectedBall();
    }
}
