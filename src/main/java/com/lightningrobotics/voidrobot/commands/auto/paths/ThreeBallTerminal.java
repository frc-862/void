package com.lightningrobotics.voidrobot.commands.auto.paths;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.common.command.core.TimedCommand;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonDeployIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIndexeCargo;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargoVision;
import com.lightningrobotics.voidrobot.commands.turret.AimTurret;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargo;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class ThreeBallTerminal extends ParallelCommandGroup {

	private static Path start3Ball = new Path("Start3Ball.path", false);
	private static Path end3Ball = new Path("End3Ball.path", false);

    public ThreeBallTerminal(Drivetrain drivetrain, Indexer indexer, Intake intake, Shooter shooter, Hood hood, Turret turret, HubTargeting targeting) throws Exception {
		super(

			new AutonIntake(intake),
			new TimedCommand(new AutonDeployIntake(intake), 0.75d),

			new SequentialCommandGroup(

				// shoots the first ball
				new ParallelDeadlineGroup(
					start3Ball.getCommand(drivetrain),
					new SequentialCommandGroup(
						new AutonShootCargo(shooter, hood, indexer, turret, 4000d, 0d, 20d),
						new ParallelDeadlineGroup(
							new AutonIndexeCargo(indexer),
							new AimTurret(turret, targeting)
						)
					)
				),

				new ParallelCommandGroup(
					new AimTurret(turret, targeting),

					new SequentialCommandGroup(
						new InstantCommand(() -> System.out.println("about to shoot ball two ----------------------------------------")),

						new TimedCommand(new AutonShootCargoVision(shooter, hood, indexer, turret, targeting), 2),

						new ParallelDeadlineGroup(
							end3Ball.getCommand(drivetrain),
							new AutonIndexeCargo(indexer)
						),
						
						new InstantCommand(() -> System.out.println("about to shoot ball three ----------------------------------------")),

						new TimedCommand(new AutonShootCargoVision(shooter, hood, indexer, turret, targeting), 2),

						new InstantCommand(() -> System.out.println("we have ended ----------------------------------------------------")) // This line was written by Enoch 
					)
				)
			)
		);
   }
}