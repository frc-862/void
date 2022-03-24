package com.lightningrobotics.voidrobot.commands.auto.paths;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.common.command.core.TimedCommand;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonDeployIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargoVision;
import com.lightningrobotics.voidrobot.subsystems.*;


import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class FourBallHanger extends ParallelCommandGroup {

    private static Path start3BallHanger = new Path("Start3BallHanger.path", false);
    private static Path end3BallHanger = new Path("End3BallHanger.path", false);  

    public FourBallHanger(Drivetrain drivetrain, Indexer indexer, Intake intake, Shooter shooter, Hood hood, Turret turret, HubTargeting targeting) throws Exception{
		super(
			
			new TimedCommand(new AutonDeployIntake(intake), 0.75d),

      		// new AimTurret(vision, turret, drivetrain),

			// new AutonAutoIndex(indexer),

			new SequentialCommandGroup(

				// Set Initial Balls Held To 1
				new InstantCommand(indexer::initializeBallsHeld, indexer),

				// new TimedCommand(new AutonDeployIntake(intake), 0.75d),

				new ParallelCommandGroup(
					new TimedCommand(new AutonIntake(intake), start3BallHanger.getDuration(drivetrain)+1),
					start3BallHanger.getCommand(drivetrain)
				),

				new AutonShootCargoVision(shooter, hood, indexer, turret, targeting),

				new ParallelCommandGroup(
					new TimedCommand(new AutonIntake(intake), end3BallHanger.getDuration(drivetrain)+1),
					end3BallHanger.getCommand(drivetrain)
				), 

				new AutonShootCargoVision(shooter, hood, indexer, turret, targeting)

			)

		);
		
    }
}

    // @Override
    // public void initialize() {
    //     try {
    //         new TimedCommand(
                
    //             new ParallelCommandGroup(

    //                 new TimedCommand(
    //                     new AutonDeployIntake(intake), 
    //                     3),

    //                 new AutonVisionAim(vision, turret),

    //                 // all paths sequences
    //                 new SequentialCommandGroup(

    //                     // first path sequence
    //                     new TimedCommand(

    //                         new SequentialCommandGroup(
                                
    //                             new ParallelCommandGroup(
    //                                 start3BallHanger.getCommand(drivetrain)//,
    //                                 // new AutonIntake(intake, indexer, 1d, 2d)
                                    
    //                         ),

    //                         new AutonShootCargo(shooter, indexer, turret, vision)

    //                     ),  start3BallHanger.getDuration(drivetrain) + 3),

    //                     new TimedCommand(
                        
    //                         new SequentialCommandGroup(

    //                             new ParallelCommandGroup(
    //                                 end3BallHanger.getCommand(drivetrain)//,
    //                                 // new AutonIntake(intake, indexer, 1d, 1)
    //                             ), 

    //                             new AutonShootCargo(shooter, indexer, turret, vision)
                            
    //                         ), end3BallHanger.getDuration(drivetrain) + 3)

    //                 )
    //             ), start3BallHanger.getDuration(drivetrain) + end3BallHanger.getDuration(drivetrain) + 6);


    //     } catch (Exception e) {
    //         System.err.println("Unexpected Error: " + e.getMessage());
    //     }
    // }

    // @Override
    // public void execute() {}

    // @Override
    // public void end(boolean interrupted) {
    //     indexer.stop();
    //     intake.stop();
    //     shooter.stop();
    // }

    // @Override
    // public boolean isFinished() {
    //     return false;
    // }
	
// }
