package com.lightningrobotics.voidrobot;

import com.lightningrobotics.common.LightningContainer;
import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.common.subsystem.drivetrain.LightningDrivetrain;
import com.lightningrobotics.common.util.filter.JoystickFilter;
import com.lightningrobotics.common.util.filter.JoystickFilter.Mode;
import com.lightningrobotics.common.util.operator.trigger.TwoButtonTrigger;
import com.lightningrobotics.voidrobot.commands.auto.*;
import com.lightningrobotics.voidrobot.commands.climber.runClimb;
import com.lightningrobotics.voidrobot.commands.indexer.*;
import com.lightningrobotics.voidrobot.commands.intake.*;

import com.lightningrobotics.voidrobot.commands.shooter.*;
import com.lightningrobotics.voidrobot.commands.turret.*;
import com.lightningrobotics.voidrobot.constants.*;
import com.lightningrobotics.voidrobot.subsystems.*;
import com.lightningrobotics.common.auto.*;
import com.lightningrobotics.common.command.drivetrain.differential.DifferentialTankDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class RobotContainer extends LightningContainer{

	public static final boolean TESTING = true;

    // Subsystems
	private static final LightningIMU imu = LightningIMU.navX();
    private static final Climber climber = new Climber();
	private static final Drivetrain drivetrain = new Drivetrain(imu);
    private static final Turret turret = new Turret();
	private static final Shooter shooter = new Shooter();
	private static final Indexer indexer = new Indexer();
	private static final Intake intake = new Intake();
    private static final Vision vision = new Vision();
	private static final LEDs leds = new LEDs();
	
	// Joysticks
	private static final Joystick driverLeft = new Joystick(JoystickConstants.DRIVER_LEFT_PORT);
	private static final Joystick driverRight = new Joystick(JoystickConstants.DRIVER_RIGHT_PORT);
	private static final XboxController copilot = new XboxController(JoystickConstants.COPILOT_PORT);
	private static final XboxController climb = new XboxController(JoystickConstants.CLIMB_PORT);

	// Joystick Filters
	private static final JoystickFilter driverFilter = new JoystickFilter(0.13, 0.1, 1, Mode.CUBED);
    private static final JoystickFilter copilotFilter = new JoystickFilter(0.13, 0.1, 1, Mode.LINEAR);

    @Override
    protected void configureAutonomousCommands() {
		Autonomous.register("4 Ball Terminal", new FourBallTerminal(drivetrain, indexer, intake, shooter, turret));
		Autonomous.register("4 Ball Hanger", new FourBallHanger(drivetrain, indexer, intake, shooter));
        if(TESTING) registerTestPaths();        
    }

    @Override
    protected void configureButtonBindings() {
		
        // DRIVER
        (new JoystickButton(driverRight, 1)).whileHeld(new ShootCargo(shooter, indexer, turret, vision)); // Auto shoot
        (new JoystickButton(driverRight, 2)).whenActive(new ShootClose(shooter, indexer, turret)); // Shoot close no vision
		(new JoystickButton(driverLeft, 1)).whenPressed(new InstantCommand(vision::toggleVisionLights, vision)); // toggle vision LEDs
        
        // COPILOT
        (new Trigger(() -> copilot.getRightTriggerAxis() > 0.03)).whenActive(new SequentialCommandGroup(new DeployIntake(intake), new RunIntake(intake, () -> copilot.getRightTriggerAxis()))); //intake and deply on right trigger
        (new JoystickButton(copilot, 5)).whileHeld(new DeployIntake(intake)); //Retract intake
        (new JoystickButton(copilot, 6)).whileHeld(new RetractIntake(intake)); //Deploy intake
        (new Trigger(() -> copilot.getRightTriggerAxis() > 0.03)).whenActive(new RunIntake(intake, () -> copilot.getRightTriggerAxis())); //manual intake up
        (new Trigger(() -> copilot.getLeftTriggerAxis() > 0.03)).whenActive(new RunIntake(intake, () -> -copilot.getLeftTriggerAxis())); //manual intake down
        (new JoystickButton(copilot, 1)).whileHeld(new RunIndexer(indexer, () -> -Constants.DEFAULT_INDEXER_POWER));
        (new JoystickButton(copilot, 4)).whileHeld(new RunIndexer(indexer, () -> Constants.DEFAULT_INDEXER_POWER));
        // (new Trigger(() -> copilot.getLeftTriggerAxis() > 0.03)).whenActive(
        //     new ParallelCommandGroup(
        //         new RunIntake(intake, () -> -copilot.getLeftTriggerAxis()), //manual intake down
        //         new RunIndexer(indexer, () -> -copilot.getLeftTriggerAxis())
        // ));

        //(new JoystickButton(copilot, 6)).whileHeld(new RetractIntake(intake, indexer)); //Retract intake

        // (new Trigger(() -> copilot.getLeftTriggerAxis() > 0.03)).whenActive(new RunIndexer(indexer, () -> copilot.getLeftTriggerAxis())); //manual indexer up
        //(new JoystickButton(copilot, 5)).whileHeld(new RunIndexer(indexer, () -> -Constants.DEFAULT_INDEXER_POWER)); //Manual indexer down
        // (new JoystickButton(copilot, 2)).whileHeld(new ParallelCommandGroup(new RunIndexer(indexer, () -> -Constants.DEFAULT_INDEXER_POWER), new RunIntake(intake, () -> -Constants.DEFAULT_INTAKE_POWER))); //Manual indexer and collector out (spit)
        
        (new JoystickButton(copilot, 8)).whenPressed(new InstantCommand(() -> indexer.resetBallCount())); //Reset ball count
    }

    @Override
    protected void configureDefaultCommands() {
		drivetrain.setDefaultCommand(new DifferentialTankDrive(drivetrain, () -> -driverLeft.getY() , () -> -driverRight.getY(), driverFilter));
        turret.setDefaultCommand(new AimTurret(vision, turret, drivetrain, imu, () -> copilotFilter.filter(copilot.getRightX()), () -> copilot.getPOV()));
        // // shooter.setDefaultCommand(new MoveHoodManual(shooter, () -> -copilot.getRightY()));
		shooter.setDefaultCommand(new MoveHoodSetpoint(shooter));
        // intake.setDefaultCommand(new MoveIntake(intake, () -> copilotFilter.filter(copilot.getLeftY())));
        indexer.setDefaultCommand(new AutoIndexCargo(indexer));

        climber.setDefaultCommand(
            new runClimb(
                climber,
                () -> (
                    climb.getLeftY() +
                    // I know some people don't like these so I'll document it
                    // If the d-pad up is pressed, add 1 to total power
                    climb.getPOV() == 0 ? 1 : 0 +
                    // If the d-pad down is pressed, add -1 to total power
                    climb.getPOV() == 180 ? -1 : 0
                ),
                () -> (
                    climb.getRightY() +
                    // same thing as above, if it's up add 1
                    climb.getPOV() == 0 ? 1 : 0 +
                    // if it's down add -1
                    climb.getPOV() == 180 ? -1 : 0
                )
            )
        );
	}

    @Override
    protected void configureFaultCodes() { }

    @Override
    protected void configureFaultMonitors() { }

    @Override
    protected void configureSystemTests() { }

    @Override
    public LightningDrivetrain getDrivetrain() {
        return drivetrain;
    }

    @Override
    protected void initializeDashboardCommands() { }
	
    @Override
    protected void releaseDefaultCommands() { }

	private void registerTestPaths() {
		try {
			Autonomous.register("1 meter", 
			(new Path("1Meter.path", false)).getCommand(drivetrain));
		} catch(Exception e) {
			System.err.println("Unexpected Error: " + e.getMessage());
		}
		try {
			Autonomous.register("1 meter forward 1 meter right", 
			(new Path("1Forward1right.path", false)).getCommand(drivetrain));
		} catch(Exception e) {
			System.err.println("Unexpected Error: " + e.getMessage());
		}
	}
    
}
