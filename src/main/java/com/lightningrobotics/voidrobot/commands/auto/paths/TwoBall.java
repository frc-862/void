package com.lightningrobotics.voidrobot.commands.auto.paths;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.common.command.core.TimedCommand;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonDeployIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIndexeCargo;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonVisionShooting;
import com.lightningrobotics.voidrobot.commands.climber.pivot.PivotToReach;
import com.lightningrobotics.voidrobot.commands.intake.DeployIntake;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class TwoBall extends ParallelCommandGroup {

	private static Path twoBallPath = new Path("1-2Ball.path", false);

	public TwoBall(Drivetrain drivetrain, Shooter shooter, Hood hood, Turret turret, Indexer indexer, Intake intake, Climber climber, HubTargeting targeting) throws Exception {
		super(
			new TimedCommand(new AutonDeployIntake(intake), 0.75d),
			
			new PivotToReach(climber),
			new SequentialCommandGroup(
				new DeployIntake(intake),
				new AutonIntake(intake)
			),

			new SequentialCommandGroup(
				new InstantCommand(indexer::initializeBallsHeld),
				new ParallelDeadlineGroup(
					twoBallPath.getCommand(drivetrain),
					new AutonIndexeCargo(indexer, 2)
				),
				new AutonVisionShooting(shooter, hood, indexer, targeting, 0d, 0d, 0d)
			)
		);	
	}
	
}
