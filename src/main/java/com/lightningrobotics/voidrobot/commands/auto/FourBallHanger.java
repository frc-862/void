package com.lightningrobotics.voidrobot.commands.auto;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class FourBallHanger extends CommandBase {

    
    private final Indexer indexer;
    private final Intake intake;
    private final Shooter shooter;
    private final Drivetrain drivetrain;

    Path start3BallHanger = new Path("Start3BallHanger.path", false);
    Path end3BallHanger = new Path("End3BallHanger.path", false);

    public FourBallHanger(Drivetrain drivetrain, Indexer indexer, Intake intake, Shooter shooter) {
        this.drivetrain = drivetrain;
        this.indexer = indexer;
        this.intake = intake;
        this.shooter = shooter;

        addRequirements(drivetrain, indexer, intake);
        
    }

    @Override
    public void initialize() {
        try {
            new SequentialCommandGroup(start3BallHanger.getCommand(drivetrain), end3BallHanger.getCommand(drivetrain)).schedule();;
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
        }
    }

    @Override
    public void execute() {}

    @Override
    public void end(boolean interrupted) {
        indexer.stop();
        intake.stop();
        shooter.stop();
        indexer.setAutoIndex(true);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
	
}
