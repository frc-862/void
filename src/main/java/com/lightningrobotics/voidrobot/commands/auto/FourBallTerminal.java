package com.lightningrobotics.voidrobot.commands.auto;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.voidrobot.commands.indexer.RunIndexer;
import com.lightningrobotics.voidrobot.commands.intake.RunIntake;
import com.lightningrobotics.voidrobot.commands.shooter.RunShooterDashboard;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class FourBallTerminal extends CommandBase {

    private final Drivetrain drivetrain;
    private final Indexer indexer;
    private final Intake intake;
    private final Shooter shooter;

    Path start4BallPath = new Path("Start4Ball.path", false);
    Path middle4BallPath = new Path("Middle4Ball.path", false);
    Path end4BallPath = new Path("End6Ball.path", false);   

    public FourBallTerminal(Drivetrain drivetrain, Indexer indexer, Intake intake, Shooter shooter) {
        this.drivetrain = drivetrain;
        this.indexer = indexer;
        this.intake = intake;
        this.shooter = shooter;

        addRequirements(drivetrain, indexer, intake);
        
    }

    @Override
    public void initialize() {
        try {
            new SequentialCommandGroup(
                new ParallelCommandGroup(
                    start4BallPath.getCommand(drivetrain),  
                    new RunIntake(intake, () -> 1d)), 
                new ParallelCommandGroup(
                    new RunIndexer(indexer, () -> 1d),  
                    new RunShooterDashboard(shooter, 3000))
                ).schedule();
            
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
        }

    }

    @Override
    public void execute() {
    }

    @Override
    public void end(boolean interrupted) {
        indexer.stop();
        intake.stop();
        shooter.stop();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
	
}
