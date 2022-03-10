package com.lightningrobotics.voidrobot.commands.auto.paths;

import com.ctre.phoenix.led.ColorFlowAnimation.Direction;
import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.common.command.core.TimedCommand;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonDeployIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargo;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonVisionAim;
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
    private final Vision vision;

    Path start3BallHanger = new Path("Start3BallHanger.path", false);
    Path end3BallHanger = new Path("End3BallHanger.path", false);  

    public FourBallHanger(Drivetrain drivetrain, Indexer indexer, Intake intake, Shooter shooter, Turret turret, Vision vision) {
        this.drivetrain = drivetrain;
        this.indexer = indexer;
        this.intake = intake;
        this.shooter = shooter;
        this.turret = turret;
        this.vision = vision;

        addRequirements(drivetrain, indexer, intake, shooter, turret);
        
    }

    @Override
    public void initialize() {
        try {
            new TimedCommand(
                
                new ParallelCommandGroup(

                    new TimedCommand(
                        new AutonDeployIntake(intake), 
                        3),

                    new AutonVisionAim(vision, turret),

                    // all paths sequences
                    new SequentialCommandGroup(

                        // first path sequence
                        new TimedCommand(

                            new SequentialCommandGroup(
                                
                                new ParallelCommandGroup(
                                    start3BallHanger.getCommand(drivetrain),
                                    new AutonIntake(intake, indexer, 1d, 2d)
                                    
                            ),

                            new AutonShootCargo(shooter, indexer, turret, vision)

                        ),  start3BallHanger.getDuration(drivetrain) + 3),

                        new TimedCommand(
                        
                            new SequentialCommandGroup(

                                new ParallelCommandGroup(
                                    end3BallHanger.getCommand(drivetrain),
                                    new AutonIntake(intake, indexer, 1d, 1)
                                ), 

                                new AutonShootCargo(shooter, indexer, turret, vision)
                            
                            ), end3BallHanger.getDuration(drivetrain) + 3)

                    )
                ), start3BallHanger.getDuration(drivetrain) + end3BallHanger.getDuration(drivetrain) + 6);


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
