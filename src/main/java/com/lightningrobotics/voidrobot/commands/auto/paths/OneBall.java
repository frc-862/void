package com.lightningrobotics.voidrobot.commands.auto.paths;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.common.command.core.TimedCommand;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonDeployIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargoVision;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class OneBall extends ParallelCommandGroup {

	private static Path path = new Path("1-2Ball.path", false);

	public OneBall(Drivetrain drivetrain, Shooter shooter, Hood hood, Turret turret, Indexer indexer, Intake intake, HubTargeting targeting) throws Exception {
		super(
		new SequentialCommandGroup(

			new InstantCommand(() -> shooter.setPower(0.4)),

			// Set Initial Balls Held To 1
			new InstantCommand(indexer::initializeBallsHeld, indexer),

			// Deploy Intake
			new TimedCommand(new AutonDeployIntake(intake), 0.75d),

			path.getCommand(drivetrain),

			// Shoot 2 (Preload & Collected)
			new AutonShootCargoVision(shooter, hood, indexer, targeting)

		));
	}
	
}
