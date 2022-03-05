package com.lightningrobotics.voidrobot.commands.auto;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.common.command.core.TimedCommand;
import com.lightningrobotics.voidrobot.commands.indexer.RunIndexer;
import com.lightningrobotics.voidrobot.commands.intake.RunIntake;
import com.lightningrobotics.voidrobot.commands.shooter.RunShooter;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class FourBallTerminal extends CommandBase {

    private final Drivetrain drivetrain;
    private final Indexer indexer;
    private final Intake intake;
    private final Shooter shooter;
    private final Turret turret;

    Path start4BallPath = new Path("Start4Ball.path", false);
    Path end4BallPath = new Path("End4Ball.path", false);   

    public FourBallTerminal(Drivetrain drivetrain, Indexer indexer, Intake intake, Shooter shooter, Turret turret) {
        this.drivetrain = drivetrain;
        this.indexer = indexer;
        this.intake = intake;
        this.shooter = shooter;
        this.turret = turret;

        addRequirements(drivetrain, indexer, intake, shooter, turret);
        
    }

    @Override
    public void initialize() {
        try {

            new SequentialCommandGroup(

                new TimedCommand(

                    new SequentialCommandGroup(
                        new ParallelCommandGroup(

                            start4BallPath.getCommand(drivetrain),

                            new SequentialCommandGroup(
                                
                                new AutonShoot1(indexer, shooter, turret, 0.3d, 4100d, 0d, 22.5),
                                new AutonIntake(intake, indexer, 1d, 2d),
                                new AutonShoot2(indexer, shooter, turret, 0, 4100d, 0d, 43.1)
                            )
                        )

                    ), 

                        start4BallPath.getDuration(drivetrain) + 3

                ), 

                new TimedCommand(
                    new ParallelCommandGroup(
                        end4BallPath.getCommand(drivetrain),
                        new RunIndexer(indexer, () -> 1d),
                        new RunIntake(intake, () -> 1d),
                        new RunShooter(shooter, 4100d) // TODO use vision shoot function instead
                    ), 
                    end4BallPath.getDuration(drivetrain) + 5
                )
        
            ).schedule();

            // new SequentialCommandGroup(
            //     // Shoot on the fly to first ball
            //     new TimedCommand(
            //         new ParallelCommandGroup(
            //         new SequentialCommandGroup(
            //             new AutonShoot(indexer, shooter, turret, 4100d, 0d, 22.5),
            //             new ParallelCommandGroup(
            //                 new RunIndexer(indexer, () -> 1d),
            //                 new RunIntake(intake, () -> 1d),
            //                 new RunShooter(shooter, 4100d) // TODO use vision shoot function instead
            //             )
            //         ),
            //         start4BallPath.getCommand(drivetrain), 
            //     // ) {
            //     //     Timer timer;
            //     //     @Override
            //     //     public void initialize() {
            //     //         super.initialize();
            //     //         timer = new Timer();
            //     //         timer.reset();
            //     //         timer.start();
            //     //     }
            //     //     @Override
            //     //     public boolean isFinished() {
            //     //         return timer.hasElapsed(start4BallPath.getDuration(drivetrain));
            //     //     }
            //     // }, 
            //     new AutonShoot(indexer, shooter, turret, 5000, 0d, 22.5)                
            // ), start4BallPath.getDuration(drivetrain))
            // ).schedule();
            
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
