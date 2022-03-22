package com.lightningrobotics.voidrobot.commands.auto.paths;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.common.command.core.TimedCommand;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonAutoIndex;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonAutoIndex2;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargoVision;
import com.lightningrobotics.voidrobot.commands.turret.AimTurret;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargo;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class ThreeBallVision extends ParallelCommandGroup {

	private static Path start3Ball = new Path("Start3Ball.path", false);
	private static Path end3Ball = new Path("End3Ball.path", false);

    public ThreeBallVision(Drivetrain drivetrain, Indexer indexer, Intake intake, Shooter shooter, Hood hood, Turret turret, Vision vision) throws Exception {
		super(

			new AutonIntake(intake),

			new SequentialCommandGroup(

				new ParallelDeadlineGroup(
					start3Ball.getCommand(drivetrain),
					new SequentialCommandGroup(
						new AutonShootCargo(shooter, hood, indexer, turret, 3700d, 0d, 10d),
						new AutonShootCargoVision(shooter, hood, indexer, turret, vision)
					)
				),

				new ParallelCommandGroup(
					end3Ball.getCommand(drivetrain), 
					new AutonShootCargoVision(shooter, hood, indexer, turret, vision)
				)
			)
		);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// super(

		// 	// new TimedCommand(new AutonDeployIntake(intake), 0.75d),
		// 	// new TimedCommand(new AutonAutoIndex(indexer), start3Ball.getDuration(drivetrain) + end3Ball.getDuration(drivetrain)+2),
		// 	new InstantCommand(() -> indexer.setPower(0.2)),
		// 	new TimedCommand(new AutonIntake(intake), start3Ball.getDuration(drivetrain) + end3Ball.getDuration(drivetrain)+4),

		// 	new SequentialCommandGroup(

		// 		new InstantCommand(indexer::initializeBallsHeld),

		// 		new ParallelDeadlineGroup(
		// 			start3Ball.getCommand(drivetrain),
		// 			new AutonShootCargo(shooter, hood, indexer, turret, 3700d, 0d, 10d)
		// 		),

		// 		new InstantCommand(() -> indexer.setPower(0.2)),

		// 		new ParallelDeadlineGroup(	
		// 			new SequentialCommandGroup(	
		// 				new TimedCommand(new AutonShootCargoVision(shooter, hood, indexer, turret, vision), 2),
		// 				end3Ball.getCommand(drivetrain),
		// 				new TimedCommand(new AutonShootCargoVision(shooter, hood, indexer, turret, vision), 2)
		// 				),
		// 			new AimTurret(vision, turret, drivetrain)
		// 		)
		// 	)
		// );
   }
}