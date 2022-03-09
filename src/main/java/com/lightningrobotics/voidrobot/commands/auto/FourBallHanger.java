package com.lightningrobotics.voidrobot.commands.auto;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.common.command.core.TimedCommand;
import com.lightningrobotics.voidrobot.commands.indexer.RunIndexer;
import com.lightningrobotics.voidrobot.commands.intake.RunIntake;
import com.lightningrobotics.voidrobot.commands.shooter.RunShooter;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class FourBallHanger extends CommandBase {

    
    private final Drivetrain drivetrain;
    private final Indexer indexer;
    private final Intake intake;
    private final Shooter shooter;
    private final Turret turret;

    Path start3BallHanger = new Path("Start3BallHanger.path", false);
    Path end3BallHanger = new Path("End3BallHanger.path", false);  

    public FourBallHanger(Drivetrain drivetrain, Indexer indexer, Intake intake, Shooter shooter, Turret turret) {
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

                            start3BallHanger.getCommand(drivetrain),

                            new SequentialCommandGroup(
                                
                                new AutonShoot1(indexer, shooter, turret, 0.3d, 4100d, 0d, 22.5),
                                new AutonIntake(intake, indexer, 1d, 2d),
                                new AutonShoot2(indexer, shooter, turret, 0, 4100d, 0d, 43.1)
                            )
                        ) 
                    ), 
                    
                        start3BallHanger.getDuration(drivetrain) + 3)
            );



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
