package com.lightningrobotics.voidrobot.commands.auto.paths;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.common.command.core.TimedCommand;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonDeployIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIntake;
import com.lightningrobotics.voidrobot.commands.indexer.AutoIndexCargo;
import com.lightningrobotics.voidrobot.commands.indexer.RunIndexer;
import com.lightningrobotics.voidrobot.commands.intake.RunIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargo;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonVisionAim;
import com.lightningrobotics.voidrobot.commands.shooter.RunShooter;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class ThreeBallTerminal extends ParallelCommandGroup {

    private static Path start4BallPath = new Path("Start4Ball.path", false);
    private static Path middle4BallPath = new Path("Middle4Ball.path", false);
    private static Path end4BallPath = new Path("End4Ball.path", false);   

	
    public ThreeBallTerminal(Drivetrain drivetrain, Indexer indexer, Intake intake, Shooter shooter, Turret turret, Vision vision) throws Exception {
        super(
			
			new TimedCommand(new AutonDeployIntake(intake), 0.75d),

			new AutonVisionAim(vision, turret),

			new AutoIndexCargo(indexer),

			new SequentialCommandGroup(
				
				// Turn On Lights
				new InstantCommand(vision::turnOnVisionLight, vision),

				// Set Initial Balls Held To 1
				new InstantCommand(indexer::initializeBallsHeld, indexer),
    
				// new TimedCommand(new AutonDeployIntake(intake), 0.75d),

				start4BallPath.getCommand(drivetrain),
				new AutonShootCargo(shooter, indexer, turret, vision),

				new ParallelCommandGroup(
					new TimedCommand(new AutonIntake(intake), middle4BallPath.getDuration(drivetrain)+1),
					middle4BallPath.getCommand(drivetrain)
				),

				new AutonShootCargo(shooter, indexer, turret, vision)

			)

		);
        
    }
}

    // @Override
    // public void initialize() {
        // vision.turnOnVisionLight();
        // try {

        //     new TimedCommand(

        //         new ParallelCommandGroup(

        //             // deploy intake
        //             new TimedCommand(
        //                 new AutonDeployIntake(intake), 
        //                     1),

        //             // run the aimt the whole time
        //             new AutonVisionAim(vision, turret),
        
        //             // all the paths
        //             new SequentialCommandGroup(

        //                 // start path sequence
        //                 new TimedCommand(

        //                         new SequentialCommandGroup(

        //                             start4BallPath.getCommand(drivetrain),
        //                             new AutonShootCargo(shooter, indexer, turret, vision)

        //                         ),  start4BallPath.getDuration(drivetrain) + 3

        //                 ), 

        //                 // middle path sequence
        //                 new TimedCommand(

        //                     new SequentialCommandGroup(
        //                         new ParallelCommandGroup(
        //                             middle4BallPath.getCommand(drivetrain)//,
        //                             // new AutonIntake(intake, indexer, 2d)
        //                         ),

        //                         new AutonShootCargo(shooter, indexer, turret, vision)


        //                     ),  middle4BallPath.getDuration(drivetrain) + 3

        //                     ),



        //                 // end path sequence
        //                 new TimedCommand(

        //                     new SequentialCommandGroup(

        //                     new ParallelCommandGroup(
        //                         end4BallPath.getCommand(drivetrain)//,
        //                         // new AutonIntake(intake, indexer, 1d)
        //                     ),
            
        //                         new AutonShootCargo(shooter, indexer, turret, vision)

        //                     ),  end4BallPath.getDuration(drivetrain) + 3

        //                 )
                
        //             )
        //         ), 
        //         start4BallPath.getDuration(drivetrain) + middle4BallPath.getDuration(drivetrain) + end4BallPath.getDuration(drivetrain) + 9).schedule();
            
//         } catch (Exception e) {
//             System.err.println("Unexpected Error: " + e.getMessage());
//         }

//     }

//     @Override
//     public void execute() {
//     }

//     @Override
//     public void end(boolean interrupted) {
//         indexer.stop();
//         intake.stop();
//         shooter.stop();
//         turret.stop();
//     }

//     @Override
//     public boolean isFinished() {
//         return false;
//     }
	
// }
