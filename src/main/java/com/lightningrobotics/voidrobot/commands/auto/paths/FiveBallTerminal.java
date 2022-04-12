package com.lightningrobotics.voidrobot.commands.auto.paths;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.common.command.core.TimedCommand;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIndexeCargo;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonVisionShooting;
import com.lightningrobotics.voidrobot.commands.climber.pivot.PivotToReach;
import com.lightningrobotics.voidrobot.commands.intake.DeployIntake;
import com.lightningrobotics.voidrobot.commands.turret.AimTurret;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class FiveBallTerminal extends ParallelCommandGroup {


	private static Path start5Ball = new Path("Start5Ball.path", false);
	private static Path middle5Ball = new Path("Middle5Ball.path", false);
	private static Path end5Ball = new Path("End5Ball.path", true);

	// private static Path oneMeter = new Path("1Meter.path", false);

    public FiveBallTerminal(Drivetrain drivetrain, Indexer indexer, Intake intake, Shooter shooter, Hood hood, Turret turret, ClimbPivots pivots, HubTargeting targeting) throws Exception {
		super(
			new InstantCommand(() -> targeting.setState(0)),
			new InstantCommand(() -> turret.setConstraint(0, 25)),
			new AimTurret(turret, targeting),
			
			new PivotToReach(pivots),
			new AutonIntake(intake),

			new SequentialCommandGroup(
				new InstantCommand(indexer::initializeBallsHeld),
				new InstantCommand(drivetrain::setMotorBreakMode),

				// first shot
				new ParallelDeadlineGroup(
					new TimedCommand(start5Ball.getCommand(drivetrain), start5Ball.getDuration(drivetrain) - 0.7d),
					new SequentialCommandGroup(
						new AutonVisionShooting(shooter, hood, indexer, targeting, 3d, 0d, 200d),
						new InstantCommand(() -> turret.setConstraint(40, 25)),
						new InstantCommand(() -> targeting.setState(1)),
							new TimedCommand(new AutonIndexeCargo(indexer, 1), start5Ball.getDuration(drivetrain) - (start5Ball.getDuration(drivetrain) / 2)),
							new InstantCommand(() -> shooter.setRPM(4200)),
							new InstantCommand(() -> targeting.setState(1.5)),
							new TimedCommand(new AutonIndexeCargo(indexer, 2), start5Ball.getDuration(drivetrain) - (start5Ball.getDuration(drivetrain) / 2))			
					)
				),
				new InstantCommand(() -> targeting.setState(1.75)),
				new InstantCommand(drivetrain::stop),

				// balls 2 and 3
				new InstantCommand(() -> targeting.setState(2)),
				new TimedCommand(new AutonVisionShooting(shooter, hood, indexer, targeting, 0d, 0d, 0d), 2),
				new InstantCommand(() -> targeting.setState(2.5)),

				new InstantCommand(() -> turret.setConstraint(0, 25)),

				// drives to collect 4 and 5
				new InstantCommand(() -> targeting.setState(3)),
				new ParallelDeadlineGroup (
					new SequentialCommandGroup(
						new TimedCommand(new AutonIndexeCargo(indexer, 2), middle5Ball.getDuration(drivetrain) + 0.5),
						new InstantCommand(() -> targeting.setState(5))
					),
					new SequentialCommandGroup(
						middle5Ball.getCommand(drivetrain, 6d, 4d),
						new InstantCommand(() -> targeting.setState(4))
					)
				),

				// drives to shoot 4 and 5
				new InstantCommand(() -> targeting.setState(6)),
				end5Ball.getCommand(drivetrain, 6d, 4d),
				new InstantCommand(() -> indexer.setBallCount(2)),
				new InstantCommand(drivetrain::setMotorCoastMode),
				new InstantCommand(drivetrain::stop),
				
				// shoots 4 and 5
				new InstantCommand(() -> targeting.setState(7)),
				new AutonVisionShooting(shooter, hood, indexer, targeting, 0d, 0d, 0d),

				new InstantCommand(() -> turret.resetConstraint()),
				new InstantCommand(() -> targeting.setBiasDistance(-0.15d))

			)
		
		);
   }

}