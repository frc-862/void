package com.lightningrobotics.voidrobot.commands.auto.paths;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargo;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class ThreeBallTerminal extends ParallelCommandGroup {

	private static Path mainPath = new Path("3BallTerminal.path", false);

    public ThreeBallTerminal(Drivetrain drivetrain, Indexer indexer, Intake intake, Shooter shooter, Hood hood, Turret turret) throws Exception {
        super(
			
			// new TimedCommand(new AutonDeployIntake(intake), 0.75d),
			new AutonIntake(intake),

			mainPath.getCommand(drivetrain),

			new SequentialCommandGroup(
				// Set Initial Balls Held To 1
				new InstantCommand(indexer::initializeBallsHeld, indexer),
				
				new AutonShootCargo(shooter, hood, indexer, turret, 3700d, 0d, 10d),

				new AutonShootCargo(shooter, hood, indexer, turret, 4500d, 1.7d, 70d),
				
				new AutonShootCargo(shooter, hood, indexer, turret, 4400d, 2.5d, 40d)

			)
		);
   }
}