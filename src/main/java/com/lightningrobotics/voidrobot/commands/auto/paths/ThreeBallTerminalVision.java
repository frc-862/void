package com.lightningrobotics.voidrobot.commands.auto.paths;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonVisionShooting;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargo;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class ThreeBallTerminalVision extends ParallelCommandGroup {

	private static Path terminal3Ball = new Path("3BallTerminal.path", false);

    public ThreeBallTerminalVision(Drivetrain drivetrain, Indexer indexer, Intake intake, Shooter shooter, Hood hood, Turret turret, HubTargeting targeting) throws Exception {
		super(

			new AutonIntake(intake),
			// new TimedCommand(new AutonDeployIntake(intake), 0.75d),
			new InstantCommand(indexer::initializeBallsHeld),
			new InstantCommand(hood::zero),

			terminal3Ball.getCommand(drivetrain),

			new SequentialCommandGroup(
				new AutonShootCargo(shooter, hood, indexer, turret, targeting, 4000d, 0d, 20d),

				new AutonVisionShooting(shooter, hood, indexer, turret, targeting, 20d, 0d), 
				new AutonVisionShooting(shooter, hood, indexer, turret, targeting, 15d, 0.5d)
				
				// new SequentialCommandGroup(
				// 	new InstantCommand(indexer::stop),
				// 	new InstantCommand(shooter::coast),
				// 	new InstantCommand(hood::stop),
				// 	new InstantCommand(turret::stop)
				// )
			)
		);
   }
}