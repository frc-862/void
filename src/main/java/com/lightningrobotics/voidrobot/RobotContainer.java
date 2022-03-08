package com.lightningrobotics.voidrobot;

import com.lightningrobotics.common.LightningContainer;
import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.common.subsystem.drivetrain.LightningDrivetrain;
import com.lightningrobotics.common.util.filter.JoystickFilter;
import com.lightningrobotics.common.util.filter.JoystickFilter.Mode;
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
import edu.wpi.first.wpilibj2.command.InstantCommand;
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
        (new JoystickButton(driverRight, 1)).whileHeld(new ShootCargo(shooter, indexer, turret, vision), false); // Auto shoot
        (new JoystickButton(driverRight, 2)).whileHeld(new ShootClose(shooter, indexer, turret), false); // Shoot close no vision
		(new JoystickButton(driverLeft, 1)).whenPressed(new InstantCommand(vision::toggleVisionLights, vision)); // toggle vision LEDs
        
        // COPILOT:

        // Collector Controls:
        (new Trigger(() -> copilot.getRightTriggerAxis() > 0.03)).whenActive(new RunIntake(intake, () -> copilot.getRightTriggerAxis())); //RT: run collector in
        (new JoystickButton(copilot, JoystickConstants.BUTTON_B)).whileHeld(new RunIntake(intake, () -> -1)); //B: run collector out
        (new JoystickButton(copilot, JoystickConstants.RIGHT_BUMPER)).whileHeld(new DeployIntake(intake)); //RB: Retract intake
        (new JoystickButton(copilot, JoystickConstants.BUTTON_BACK)).whileHeld(new RetractIntake(intake)); //SELECT/BACK: Deploy intake

        // Indexer Controls:
        (new JoystickButton(copilot, JoystickConstants.LEFT_BUMPER)).whileHeld(new RunIndexer(indexer, () -> -Constants.DEFAULT_INDEXER_POWER)); //LB: run indexer down
        (new Trigger(() -> copilot.getLeftTriggerAxis() > 0.03)).whenActive(new RunIndexer(indexer, () -> copilot.getLeftTriggerAxis())); //LT: run indexer up
        (new JoystickButton(copilot, JoystickConstants.BUTTON_START)).whenPressed(new InstantCommand(() -> indexer.resetBallCount())); //START: Reset ball count

    }

    @Override
    protected void configureDefaultCommands() {
        //AUTO
        indexer.setDefaultCommand(new AutoIndexCargo(indexer));
        //DRIVER
		drivetrain.setDefaultCommand(new DifferentialTankDrive(drivetrain, () -> -driverLeft.getY() , () -> -driverRight.getY(), driverFilter));
        //TODO: make this aim bias from d-pad by default and toggle on manual control via shuffleboard
        //COPILOT
        turret.setDefaultCommand(new AimTurret(vision, turret, drivetrain, imu, () -> copilotFilter.filter(copilot.getRightX()), () -> copilot.getPOV()));
        shooter.setDefaultCommand(new MoveHoodManual(shooter, () -> copilot.getPOV()));

        //CLIMB
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
                ),
                //set left and right pivot powers
                () -> (
                    climb.getRightTriggerAxis() - climb.getLeftTriggerAxis() //RT: pivot forward, LT: pivot backwards
                ),
                () -> (
                    climb.getRightTriggerAxis() - climb.getLeftTriggerAxis()
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
