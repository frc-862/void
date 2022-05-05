package com.lightningrobotics.voidrobot.commands.auto.paths;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.common.command.core.TimedCommand;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonDeployIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIndexeCargo;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonVisionShooting;
import com.lightningrobotics.voidrobot.commands.climber.pivot.PivotToReach;
import com.lightningrobotics.voidrobot.commands.intake.DeployIntake;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class TwoBall extends ParallelCommandGroup {

	private static Path twoBallPath = new Path("1-2Ball.path", false);

	public TwoBall(Drivetrain drivetrain, Shooter shooter, Hood hood, Turret turret, Indexer indexer, Intake intake, ClimbPivots pivots, HubTargeting targeting) throws Exception {
		super(
			
			new PivotToReach(pivots),
			new AutonIntake(intake),

			new SequentialCommandGroup(
				new InstantCommand(indexer::initializeBallsHeld),
				
				new ParallelCommandGroup(
					twoBallPath.getCommand(drivetrain),
					new SequentialCommandGroup(
						new AutonVisionShooting(shooter, hood, indexer, targeting, 0, 0.4d, 0),
						new AutonIndexeCargo(indexer, 1),
						new AutonVisionShooting(shooter, hood, indexer, targeting, 0, 0, 0)

					)
				),
				
				new InstantCommand(() -> turret.resetConstraint()),
				new InstantCommand(() -> targeting.setBiasDistance(Constants.DEFAULT_DISTANCE_BIAS))
			)
		);

		// super(
			
		// 	new PivotToReach(pivots),
		// 	new AutonIntake(intake),

		// 	new SequentialCommandGroup(
		// 		new InstantCommand(indexer::initializeBallsHeld),
		// 		new ParallelDeadlineGroup(
		// 			new TimedCommand(twoBallPath.getCommand(drivetrain), twoBallPath.getDuration(drivetrain) + 2),
		// 			new AutonIndexeCargo(indexer, 2)
		// 		),
		// 		new AutonVisionShooting(shooter, hood, indexer, targeting, 0d, 0d, 0d),

		// 		new InstantCommand(() -> turret.resetConstraint()),
		// 		new InstantCommand(() -> targeting.setBiasDistance(Constants.DEFAULT_DISTANCE_BIAS))
		// 	)
		// );	
	}
	
}
