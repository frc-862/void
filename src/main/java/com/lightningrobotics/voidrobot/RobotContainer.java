package com.lightningrobotics.voidrobot;

import com.lightningrobotics.common.LightningContainer;
import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.common.subsystem.drivetrain.LightningDrivetrain;
import com.lightningrobotics.common.util.filter.JoystickFilter;
import com.lightningrobotics.common.util.filter.JoystickFilter.Mode;
import com.lightningrobotics.voidrobot.commands.ToggleZeroTurretHood;
import com.lightningrobotics.voidrobot.commands.auto.paths.FourBallHanger;
import com.lightningrobotics.voidrobot.commands.auto.paths.FourBallTerminal;
import com.lightningrobotics.voidrobot.commands.auto.paths.OneBall;
import com.lightningrobotics.voidrobot.commands.auto.paths.ThreeBallTerminal;
import com.lightningrobotics.voidrobot.commands.auto.paths.TwoBall;
import com.lightningrobotics.voidrobot.commands.auto.paths.TwoBallTest;
import com.lightningrobotics.voidrobot.commands.climber.MakeHoodAndTurretZero;
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
    private final Hood hood = new Hood();
	
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
        try {
			Autonomous.register("Taxi", new Path("1-2Ball.path", false).getCommand(drivetrain));
			Autonomous.register("test 2 Ball", new TwoBallTest(drivetrain, shooter, turret, indexer, intake, vision));
			Autonomous.register("2 Ball", new TwoBall(drivetrain, shooter, turret, indexer, intake, vision));
			Autonomous.register("1 Ball", new OneBall(drivetrain, shooter, turret, indexer, intake, vision));
			Autonomous.register("4 Ball Terminal", new FourBallTerminal(drivetrain, indexer, intake, shooter, turret, vision));
			Autonomous.register("3 Ball Terminal", new ThreeBallTerminal(drivetrain, indexer, intake, shooter, turret, vision));
			Autonomous.register("4 Ball Hanger", new FourBallHanger(drivetrain, indexer, intake, shooter, turret, vision));
		} catch (Exception e) {
			System.err.println("I did an oopsie.");
		}

        if(TESTING) registerTestPaths();        
    }

    @Override
    protected void configureButtonBindings() {

        // DRIVER
        (new JoystickButton(driverRight, 1)).whileHeld(new ShootCargo(shooter, hood, indexer, turret, vision), false); // Auto shoot
        (new JoystickButton(driverLeft, 1)).whileHeld(new ShootCargoManual(shooter, indexer, turret, vision), false); // Auto shoot
        (new JoystickButton(driverRight, 2)).whileHeld(new ShootClose(shooter, indexer, turret, vision), false); // Shoot close no vision
		(new JoystickButton(driverRight, 3)).whenPressed(new InstantCommand(vision::toggleVisionLights, vision)); // toggle vision LEDs
		// (new JoystickButton(driverLeft, 2)).whenPressed(new InstantCommand(() -> vision.toggleDisableVision()));
		(new JoystickButton(driverLeft, 2)).whileHeld(new ToggleZeroTurretHood(shooter, turret));
		// (new JoystickButton(driverLeft, 2)).whileHeld(new ZeroTurretHood(shooter, turret)); // TODO test

        // COPILOT:

        // Collector Controls:
        (new Trigger(() -> copilot.getRightTriggerAxis() > 0.03)).whenActive(new RunIntake(intake, () -> -copilot.getRightTriggerAxis())); //RT: run collector in
        (new JoystickButton(copilot, JoystickConstants.BUTTON_B)).whileHeld(new RunIntake(intake, () -> 1)); //B: run collector out
        (new JoystickButton(copilot, JoystickConstants.RIGHT_BUMPER)).whileHeld(new ActuateIntake(intake, indexer, -Constants.DEFAULT_WINCH_POWER)); //RB: Retract intake
        (new JoystickButton(copilot, JoystickConstants.BUTTON_BACK)).whileHeld(new ActuateIntake(intake, indexer, Constants.DEFAULT_WINCH_POWER)); //SELECT/BACK: Deploy intake

        // Indexer Controls:
        (new JoystickButton(copilot, JoystickConstants.LEFT_BUMPER)).whileHeld(new RunIndexer(indexer, () -> -Constants.DEFAULT_INDEXER_POWER)); //LB: run indexer down
        (new Trigger(() -> copilot.getLeftTriggerAxis() > 0.03)).whenActive(new RunIndexer(indexer, () -> copilot.getLeftTriggerAxis())); //LT: run indexer up
        (new JoystickButton(copilot, JoystickConstants.BUTTON_START)).whenPressed(new InstantCommand(() -> indexer.resetBallCount())); //START: Reset ball count

		//Climb Control:
		// (new JoystickButton(climb, JoystickConstants.BUTTON_A)).whenPressed(new MakeHoodAndTurretZero(turret, shooter));

    }

    @Override
    protected void configureDefaultCommands() {
        //AUTO
        // indexer.setDefaultCommand(new AutoIndexCargo(indexer, intake));
		drivetrain.setDefaultCommand(new DifferentialTankDrive(drivetrain, () -> -driverLeft.getY() , () -> -driverRight.getY(), driverFilter));
        turret.setDefaultCommand(new AimTurret(vision, turret, drivetrain));
		vision.setDefaultCommand(new AdjustBias(vision, () -> copilot.getPOV(), () -> (new JoystickButton(copilot, JoystickConstants.BUTTON_X).get())));

		// shooter.setDefaultCommand(new MoveHoodSetpoint(shooter));
     	//shooter.setDefaultCommand(new MoveHoodManual(shooter, () -> copilot.getLeftY()));
	    // shooter.setDefaultCommand(new RunShooterDashboard(shooter, vision));

        //CLIMB
        climber.setDefaultCommand(
            new runClimb(
                climber,
                () -> (
                    ((-1*climb.getLeftY()) +
                    // I know some people don't like these so I'll document it
                    // If the d-pad up is pressed, add 1 to total power
                    (climb.getPOV() == 0 ? 1 : 0) +
                    // If the d-pad down is pressed, add -1 to total power
                    (climb.getPOV() == 180 ? -1 : 0)) * 0.5
                ),
                () -> (
                    ((-1*climb.getRightY()) +
                    // same thing as above, if it's up add 1
                    (climb.getPOV() == 0 ? 1 : 0) +
                    // if it's down add -1
                    (climb.getPOV() == 180 ? -1 : 0)) * 0.5
                ),
                //set left and right pivot powers
                () -> (
                    climb.getLeftTriggerAxis() - //LT: pivot forwards
                    (climb.getLeftBumper() ? 0.5 : 0) //LB: Pivot Backwards
                ),
                () -> (
                    climb.getRightTriggerAxis() - //RT: pivot forwards
                    (climb.getRightBumper() ? 0.5 : 0) //RB: pivot backwards
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
    protected void initializeDashboardCommands() { 
		 var tab = Shuffleboard.getTab("shooter test");
		// var compTab = Shuffleboard.getTab("Competition");
		 tab.add(new ResetHood(shooter));
		// compTab.add(new MoveHoodManual(shooter, () -> copilot.getLeftY()));
	}
	
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
