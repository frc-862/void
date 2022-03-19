package com.lightningrobotics.voidrobot.commands.auto.paths;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.common.command.core.TimedCommand;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonAutoIndex;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonAutoIndex2;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonIntake;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargoVision;
import com.lightningrobotics.voidrobot.commands.auto.commands.AutonShootCargo;
import com.lightningrobotics.voidrobot.subsystems.*;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class FiveBallTerminalVision extends ParallelCommandGroup {

	private static Path main5Ball = new Path("5Ball.json", false);

    public FiveBallTerminalVision(Drivetrain drivetrain, Indexer indexer, Intake intake, Shooter shooter, Hood hood, Turret turret, Vision vision) throws Exception {
        super(

			// new TimedCommand(new AutonDeployIntake(intake), 0.75d),
			main5Ball.getCommand(drivetrain),

			new SequentialCommandGroup(

				// shoot preload
				new AutonShootCargo(shooter, hood, indexer, turret, 3700d, 0d, 10d),

				// shoots ball 2 and 3
				new ParallelDeadlineGroup(
					new TimedCommand(new AutonShootCargoVision(shooter, hood, indexer, turret, vision), 3.5), // tune the time here
					new AutonAutoIndex(indexer), 
					new AutonIntake(intake)
				), 

				// collect ball 4 and 5
				new TimedCommand(new AutonAutoIndex2(indexer), 2), // tune the time here

				// wait till closer to target
				new TimedCommand(new CommandBase() {} , 2), // tune the time here 
				// shoots ball 4 and 5
				new AutonShootCargoVision(shooter, hood, indexer, turret, vision)
				
			)
		);
   }
}