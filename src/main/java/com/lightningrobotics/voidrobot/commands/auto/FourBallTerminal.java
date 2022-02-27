package com.lightningrobotics.voidrobot.commands.auto;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Intake;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
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
                start4BallPath.getCommand(drivetrain),
                middle4BallPath.getCommand(drivetrain), 
                end4BallPath.getCommand(drivetrain))
                .schedule();
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
