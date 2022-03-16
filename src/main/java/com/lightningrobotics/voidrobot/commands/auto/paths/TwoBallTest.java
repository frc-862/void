package com.lightningrobotics.voidrobot.commands.auto.paths;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.common.command.core.TimedCommand;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonDeployIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargo;
import com.lightningrobotics.voidrobot.commands.indexer.AutoIndexCargo;
import com.lightningrobotics.voidrobot.commands.turret.AimTurret;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class TwoBallTest extends ParallelCommandGroup {

	private static Path path = new Path("1-2Ball.path", false);

	public TwoBallTest(Drivetrain drivetrain, Shooter shooter, Hood hood, Turret turret, Indexer indexer, Intake intake, Vision vision) throws Exception {
		super(

		// Aim Turret
		new AimTurret(vision, turret, drivetrain),

		new AutoIndexCargo(indexer), // TODO test this

		new SequentialCommandGroup(

			// Set Initial Balls Held To 1
			new InstantCommand(indexer::initializeBallsHeld, indexer),

			// Turn On Lights
			new InstantCommand(vision::turnOnVisionLight, vision),

			// Deploy Intake
			new TimedCommand(new AutonDeployIntake(intake), 0.75d),

			// Run Path & Collect 1
			new ParallelCommandGroup(
				new TimedCommand(new AutonIntake(intake), path.getDuration(drivetrain)+1),
				path.getCommand(drivetrain)
			),
			
			// Shoot 2 (Preload & Collected)
			new AutonShootCargo(shooter, hood, indexer, turret, vision)

		));
	}
	
}
