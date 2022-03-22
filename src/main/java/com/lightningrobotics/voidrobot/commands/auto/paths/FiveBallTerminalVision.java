package com.lightningrobotics.voidrobot.commands.auto.paths;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.common.command.core.TimedCommand;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargoVision;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargo;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class FiveBallTerminalVision extends ParallelCommandGroup {

	private static Path start5Ball = new Path("Start5Ball.path", false);
	private static Path middle5Ball = new Path("Middle5Ball.path", false);
	private static Path end5Ball = new Path("End5Ball.path", true);

    public FiveBallTerminalVision(Drivetrain drivetrain, Indexer indexer, Intake intake, Shooter shooter, Hood hood, Turret turret, Vision vision) throws Exception {
        super(

			// new TimedCommand(new AutonDeployIntake(intake), 0.75d),

			// new TimedCommand(new AutonAutoIndex(indexer), start5Ball.getDuration(drivetrain) + middle5Ball.getDuration(drivetrain)),
			new AutonIntake(intake),

			new SequentialCommandGroup(

				new InstantCommand(indexer::initializeBallsHeld),

				new InstantCommand(() -> turret.setAngle(10d)),

				new ParallelCommandGroup(
					// shoot preload
					start5Ball.getCommand(drivetrain),

					new SequentialCommandGroup(
						new AutonShootCargo(shooter, hood, indexer, turret, 3700d, 0d, 10d),
						new TimedCommand(new AutonShootCargoVision(shooter, hood, indexer, turret, vision), start5Ball.getDuration(drivetrain)) // tune the time here
					)
				),

				middle5Ball.getCommand(drivetrain),

				// collect ball 4 and 5
				// new TimedCommand(new AutonAutoIndex2(indexer), 2), // tune the time here

				end5Ball.getCommand(drivetrain),

				// shoots ball 4 and 5
				new AutonShootCargoVision(shooter, hood, indexer, turret, vision)
				
			)
		);
   }
}