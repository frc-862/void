package com.lightningrobotics.voidrobot.commands.auto.paths;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.common.command.core.TimedCommand;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonAutoIndex;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonDeployIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIntake;
import com.lightningrobotics.voidrobot.commands.indexer.AutoIndexCargo;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargo;
import com.lightningrobotics.voidrobot.commands.turret.AimTurret;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class ThreeBallTerminal extends ParallelCommandGroup {

    private static Path start4BallPath = new Path("Start4Ball.path", false);
    private static Path middle4BallPath = new Path("Middle4Ball.path", false);

	
    public ThreeBallTerminal(Drivetrain drivetrain, Indexer indexer, Intake intake, Shooter shooter, Hood hood, Turret turret, Vision vision) throws Exception {
        super(
			
			// new TimedCommand(new AutonDeployIntake(intake), 0.75d),

            new AimTurret(vision, turret, drivetrain),

            new AutonAutoIndex(indexer),

			new SequentialCommandGroup(
				// Set Initial Balls Held To 1
				new InstantCommand(indexer::initializeBallsHeld, indexer),

				start4BallPath.getCommand(drivetrain),
				new AutonShootCargo(shooter, hood, indexer, turret, vision),

				new ParallelCommandGroup(
					new TimedCommand(new AutonIntake(intake), middle4BallPath.getDuration(drivetrain)+1),
					middle4BallPath.getCommand(drivetrain)
				),

				new AutonShootCargo(shooter, hood, indexer, turret, vision)

			)

		);
        
    }
}