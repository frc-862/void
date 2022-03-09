package com.lightningrobotics.voidrobot.commands.auto.commands;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Intake;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutonIntake extends CommandBase {
    private final Intake intake;
    private double power;
    private Indexer indexer;
    private double ballsWanted;

    public AutonIntake(Intake intake, Indexer indexer, double power, double ballsWanted) {
        this.intake = intake;
        this.indexer = indexer;
        this.power = power;
        this.ballsWanted = ballsWanted;

        addRequirements(intake, indexer);
    }    

    @Override
    public void execute() {
        intake.setPower(power);
        indexer.setPower(power);
    }

    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
        intake.stop();
        indexer.stop();
    }

    @Override
    public boolean isFinished(){
        return indexer.getBallCount() == ballsWanted;
    }
}
