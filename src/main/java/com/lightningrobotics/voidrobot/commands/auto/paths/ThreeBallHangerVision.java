// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

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


public class ThreeBallHangerVision extends ParallelCommandGroup {
   
    private static Path start3Ball = new Path("Start3BallHanger.path", false);
	private static Path end3Ball = new Path("End3BallHanger.path", false);

    public ThreeBallHangerVision(Drivetrain drivetrain, Indexer indexer, Intake intake, Shooter shooter, Hood hood, Turret turret, Vision vision) throws Exception {
        super(

			new AutonIntake(intake),
			new TimedCommand(new AutonDeployIntake(intake), 0.75d),

			new SequentialCommandGroup(

				// shoots the first ball
				new ParallelDeadlineGroup(
					start3Ball.getCommand(drivetrain),
					new SequentialCommandGroup(
						new AutonShootCargo(shooter, hood, indexer, turret, 4200d, 0d, -35d), // Tune this shot
						new ParallelDeadlineGroup(
							new AutonIndexeCargo(indexer),
							new AimTurret(vision, turret, drivetrain)
						)
					)
				),

				new ParallelCommandGroup(
					new AimTurret(vision, turret, drivetrain),

					new SequentialCommandGroup(
						new InstantCommand(() -> System.out.println("about to shoot ball two ----------------------------------------")),

						new TimedCommand(new AutonShootCargoVision(shooter, hood, indexer, turret, vision), 2),

						new ParallelDeadlineGroup(
							end3Ball.getCommand(drivetrain),
							new AutonIndexeCargo(indexer)
						),
						
						new InstantCommand(() -> System.out.println("about to shoot ball three ----------------------------------------")),

						new TimedCommand(new AutonShootCargoVision(shooter, hood, indexer, turret, vision), 2),

						new InstantCommand(() -> System.out.println("we have ended ----------------------------------------------------")) // This line was written by Enoch 
					)
				)
			)
		);
    }
}
