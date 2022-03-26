package com.lightningrobotics.voidrobot.commands.auto.paths;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.common.command.core.TimedCommand;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIndexeCargo;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonVisionShooting;
import com.lightningrobotics.voidrobot.commands.turret.AimTurret;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargo;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class FiveBallTerminalVision extends ParallelCommandGroup {

	// private static Path terminal3Ball = new Path("3BallTerminal.path", false);
	private static Path terminal5Ball = new Path("4-5BallTerminal.path", false);
	private static Path endTerminal5Ball = new Path("End5Ball.path", true);

    public FiveBallTerminalVision(Drivetrain drivetrain, Indexer indexer, Intake intake, Shooter shooter, Hood hood, Turret turret, HubTargeting targeting) throws Exception {
		super(

			new AimTurret(turret, targeting),

			new SequentialCommandGroup(

				new ParallelDeadlineGroup(
					terminal5Ball.getCommand(drivetrain),
					new AutonIntake(intake),
					// new TimedCommand(new AutonDeployIntake(intake), 0.75d),
					new InstantCommand(indexer::initializeBallsHeld),
					new InstantCommand(hood::zero),

					new SequentialCommandGroup(
						new AutonVisionShooting(shooter, hood, indexer, targeting, 0d, 0.2d, 200d),

						new AutonVisionShooting(shooter, hood, indexer, targeting, 10d, 1d, 0d), 
						new AutonVisionShooting(shooter, hood, indexer, targeting, 10d, 3d, 0d),

						new AutonIndexeCargo(indexer)
					)
				),

				endTerminal5Ball.getCommand(drivetrain),

				new AutonVisionShooting(shooter, hood, indexer, targeting, 0d, 0.23d, 0d),
				
				new InstantCommand(indexer::stop),
				new InstantCommand(shooter::coast),
				new InstantCommand(hood::stop),
				new InstantCommand(turret::stop)

			)
		);
   }
}