package com.lightningrobotics.voidrobot.commands.auto.paths;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.common.command.core.TimedCommand;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonDeployIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIndexeCargo;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargoVision;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonVisionShooting;
import com.lightningrobotics.voidrobot.commands.turret.AimTurret;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargo;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class ThreeBallTerminalVision extends ParallelCommandGroup {

	private static Path terminal3Ball = new Path("3BallTerminal.path", false);

    public ThreeBallTerminalVision(Drivetrain drivetrain, Indexer indexer, Intake intake, Shooter shooter, Hood hood, Turret turret, Vision vision) throws Exception {
		super(

			new AutonIntake(intake),
			// new TimedCommand(new AutonDeployIntake(intake), 0.75d),
			new InstantCommand(indexer::initializeBallsHeld),
			new InstantCommand(hood::zero),

			terminal3Ball.getCommand(drivetrain),

			new SequentialCommandGroup(
				new AutonShootCargo(shooter, hood, indexer, turret, 4000d, 0d, 20d),

				new AutonVisionShooting(shooter, hood, indexer, turret, vision, 20d, 0d), 
				new AutonVisionShooting(shooter, hood, indexer, turret, vision, 15d, 0.5d)
				
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