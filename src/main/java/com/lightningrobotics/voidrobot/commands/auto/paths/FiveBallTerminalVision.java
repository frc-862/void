package com.lightningrobotics.voidrobot.commands.auto.paths;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargo;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class FiveBallTerminalVision extends ParallelCommandGroup {

	private static Path terminal5Ball = new Path("4-5BallTerminal.path", false);

    public FiveBallTerminalVision(Drivetrain drivetrain, Indexer indexer, Intake intake, Shooter shooter, Hood hood, Turret turret, HubTargeting targeting) throws Exception {
		super(
			new AutonIntake(intake),
			// new TimedCommand(new AutonDeployIntake(intake), 0.75d),
			new InstantCommand(indexer::initializeBallsHeld),

			terminal5Ball.getCommand(drivetrain),

			new SequentialCommandGroup(
				new AutonShootCargo(shooter, hood, indexer, turret, 4000d, 0d, 20d)

				// new AutonVisionShooting(shooter, hood, indexer, turret, vision)
			)

		);
   }
}