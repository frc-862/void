package com.lightningrobotics.voidrobot.commands.auto.paths;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonAutoIndex;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargo;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargo2;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class ThreeBallTerminalVision extends ParallelCommandGroup {

	private static Path start4Ball = new Path("Start4Ball.path", false);
	private static Path middle4Ball = new Path("Middle4Ball.path", false);

    public ThreeBallTerminalVision(Drivetrain drivetrain, Indexer indexer, Intake intake, Shooter shooter, Hood hood, Turret turret, Vision vision) throws Exception {
        super(

			// new TimedCommand(new AutonDeployIntake(intake), 0.75d),
			new AutonAutoIndex(indexer), 
			new AutonIntake(intake),

			new SequentialCommandGroup(

				new ParallelCommandGroup(
					start4Ball.getCommand(drivetrain),
					new AutonShootCargo2(shooter, hood, indexer, turret, 3700d, 0d, 10d)
				),

				new ParallelCommandGroup(
					middle4Ball.getCommand(drivetrain),
					new AutonShootCargo(shooter, hood, indexer, turret, vision)
				)
			)
		);
   }
}