package com.lightningrobotics.voidrobot.commands.auto.paths;

import javax.sound.midi.MidiEvent;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.common.command.core.TimedCommand;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIndexeCargo;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonVisionShooting;
import com.lightningrobotics.voidrobot.commands.turret.AimTurret;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargo;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class FiveBallTerminalStopping extends ParallelCommandGroup {


	private static Path start5Ball = new Path("Start5Ball.path", false);
	private static Path middle5Ball = new Path("Middle5Ball.path", false);
	private static Path end5Ball = new Path("End5Ball.path", true);

	// private static Path oneMeter = new Path("1Meter.path", false);

    public FiveBallTerminalStopping(Drivetrain drivetrain, Indexer indexer, Intake intake, Shooter shooter, Hood hood, Turret turret, HubTargeting targeting) throws Exception {
		super(
			new InstantCommand(() -> targeting.setState(0)),
			new AimTurret(turret, targeting),
			new AutonIntake(intake),

			new SequentialCommandGroup(
				new InstantCommand(indexer::initializeBallsHeld),

				// first shot
				new ParallelDeadlineGroup(
					new TimedCommand(start5Ball.getCommand(drivetrain), start5Ball.getDuration(drivetrain) - 0.7d),
					new SequentialCommandGroup(
						new AutonVisionShooting(shooter, hood, indexer, targeting, 0d, 0.2d, 200d),
						new InstantCommand(() -> targeting.setState(1)),
						new TimedCommand(new AutonIndexeCargo(indexer, 2), start5Ball.getDuration(drivetrain))
					)
				),

				// balls 2 and 3
				new InstantCommand(() -> targeting.setState(2)),
				new AutonVisionShooting(shooter, hood, indexer, targeting, 0d, 0d, 0d),

				// drives to collect 4 and 5
				new InstantCommand(() -> targeting.setState(3)),
				new ParallelRaceGroup(
					new SequentialCommandGroup(
						middle5Ball.getCommand(drivetrain, 4d, 1d),
						new InstantCommand(() -> targeting.setState(4))
					),
					new SequentialCommandGroup(
						new TimedCommand(new AutonIndexeCargo(indexer, 2), middle5Ball.getDuration(drivetrain)),
						new InstantCommand(() -> targeting.setState(5))
					)
				),

				// drives to shoot 4 and 5
				new InstantCommand(() -> targeting.setState(6)),
				end5Ball.getCommand(drivetrain, 4d, 1d),
				
				// shoots 4 and 5
				new InstantCommand(() -> targeting.setState(7)),
				new AutonVisionShooting(shooter, hood, indexer, targeting, 0d, 0d, 0d)



				// middle5Ball.getCommand(drivetrain, 5, 1.5),
				// end5Ball.getCommand(drivetrain, 7, 1.5),
				// oneMeter.getCommand(drivetrain, 0.1, 0.1)
			)
		
		);
   }

}