package com.lightningrobotics.voidrobot;

import com.lightningrobotics.common.LightningContainer;
import com.lightningrobotics.common.subsystem.drivetrain.LightningDrivetrain;
import com.lightningrobotics.common.util.filter.JoystickFilter;
import com.lightningrobotics.common.util.filter.JoystickFilter.Mode;
import com.lightningrobotics.voidrobot.commands.AimTurret;
import com.lightningrobotics.voidrobot.commands.AimTurretNoVision;
import com.lightningrobotics.voidrobot.commands.DeployIntake;
import com.lightningrobotics.voidrobot.commands.RetractIntake;
import com.lightningrobotics.voidrobot.commands.RunAutoShoot;
import com.lightningrobotics.voidrobot.commands.RunIndexer;
import com.lightningrobotics.voidrobot.commands.RunIntake;
import com.lightningrobotics.voidrobot.commands.RunShooter;
import com.lightningrobotics.voidrobot.commands.ShootClose;
import com.lightningrobotics.voidrobot.commands.test.VoltageTestContinuous;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.constants.JoystickConstants;
import com.lightningrobotics.voidrobot.commands.test.VoltageTestContinuous;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.HowitzerDrivetrain;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Intake;
import com.lightningrobotics.voidrobot.subsystems.LEDs;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.TriggerAndThumb;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import java.nio.file.DirectoryStream.Filter;
import java.util.Arrays;

import javax.print.attribute.standard.Copies;

import com.lightningrobotics.common.LightningContainer;
import com.lightningrobotics.common.auto.Autonomous;
import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.common.command.drivetrain.differential.DifferentialTankDrive;
import com.lightningrobotics.common.subsystem.drivetrain.LightningDrivetrain;
import com.lightningrobotics.common.util.filter.JoystickFilter;
import com.lightningrobotics.common.util.filter.JoystickFilter.Mode;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class RobotContainer extends LightningContainer{

    // Subsystems
    // private static Turret turret = new Turret();
    // private static Vision vision = new Vision();
	// private static LEDs leds = new LEDs();
	// private static Shooter shooter = new Shooter();
	private static Indexer indexer = new Indexer();
	private static Intake intake = new Intake();
	private static Turret turret = new Turret();
	private static HowitzerDrivetrain howitzerDrivetrain = new HowitzerDrivetrain();
	private static final Drivetrain drivetrain = new Drivetrain();
	
	private static final Joystick DRIVER_LEFT = new Joystick(JoystickConstants.DRIVER_LEFT_PORT);
	private static final Joystick DRIVER_RIGHT = new Joystick(JoystickConstants.DRIVER_RIGHT_PORT);
	private static final XboxController CO_PILOT = new XboxController(JoystickConstants.CO_PILOT_PORT); // changed from joystick to xboxcontroller

	private static final JoystickFilter FILTER = new JoystickFilter(0.15, 0.1, 1, Mode.CUBED); // TODO test this filters

    public RobotContainer() {
        super();
    }

    @Override
    protected void configureAutonomousCommands() {
        try {
			Autonomous.register("Test Differential Auton 0.5", 
			(new Path(Arrays.asList(new Pose2d(0d, 0d, Rotation2d.fromDegrees(0d)), 
				new Pose2d(0.5d, 0d, Rotation2d.fromDegrees(0d))))).getCommand(drivetrain));
		} catch(Exception e) {
			System.err.println("Unexpected Error: " + e.getMessage());
		}
		try {
			Autonomous.register("1/2 ball path", 
			(new Path("1-2Ball.path", false)).getCommand(drivetrain));
		} catch(Exception e) {
			System.err.println("Unexpected Error: " + e.getMessage());
		}
		try {
			Autonomous.register("3 ball hanger", 
			(new Path("3BallHanger.path", false)).getCommand(drivetrain));
		} catch(Exception e) {
			System.err.println("Unexpected Error: " + e.getMessage());
		}
		try {
			Autonomous.register("3 ball terminal", 
			(new Path("3BallTerminal.path", false)).getCommand(drivetrain));
		} catch(Exception e) {
			System.err.println("Unexpected Error: " + e.getMessage());
		}
		try {
			Autonomous.register("4/5 ball terminal", 
			(new Path("4-5BallTerminal.path", false)).getCommand(drivetrain));
		} catch(Exception e) {
			System.err.println("Unexpected Error: " + e.getMessage());
		}
		try {
			Autonomous.register("5/6 ball terminal", 
			(new Path("5-6BallTerminal.path", false)).getCommand(drivetrain));
		} catch(Exception e) {
			System.err.println("Unexpected Error: " + e.getMessage());
		}
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

    @Override
    protected void configureButtonBindings() {
        //DRIVER
        // (new JoystickButton(DRIVER_RIGHT, 1)).whileHeld(new RunAutoShoot(shooter, indexer)); //Auto shoot
        // (new TriggerAndThumb((new JoystickButton(DRIVER_RIGHT, 1)), (new JoystickButton(DRIVER_RIGHT, 2)))).whenPressed(new ShootClose(shooter)); // shoot close
        

        //COPILOT
        // (new Trigger(() -> CO_PILOT.getRightTriggerAxis() > 0.03)).whenActive(new InstantCommand(() -> intake.setPower(CO_PILOT.getRightTriggerAxis()), intake)); //intake 
        // (new JoystickButton(CO_PILOT, 1)).whenPressed(new DeployIntake(intake)); //Deploy intake
        // (new JoystickButton(CO_PILOT, 4)).whenPressed(new RetractIntake(intake)); //Retract intake
        // (new JoystickButton(CO_PILOT, 5)).whenActive(new InstantCommand(() -> indexer.setPower(Constants.DEFAULT_INDEXER_POWER), intake)); //Manual intake up
        // (new JoystickButton(CO_PILOT, 6)).whenActive(new InstantCommand(() -> indexer.setPower(-Constants.DEFAULT_INDEXER_POWER), intake)); //Manual intake down
        // (new Trigger(() -> CO_PILOT.getLeftTriggerAxis() > 0.03)).whenActive(new InstantCommand(() -> indexer.setPower(-CO_PILOT.getLeftTriggerAxis()), indexer)); //indexer out
        // (new Trigger(() -> CO_PILOT.getLeftTriggerAxis() > 0.03)).whenActive(new InstantCommand(() -> intake.setPower(-CO_PILOT.getLeftTriggerAxis()), intake)); //intake out
        //TODO: add bias stuff
        /*
        (new POVButton(climb, 0)).whenPressed(new InstantCommand()); //TODO: add climber stuff
        (new POVButton(climb, 180)).whenPressed(new InstantCommand()); //TODO: add climber stuff
        */

		(new Trigger(() -> Math.abs((CO_PILOT.getRightTriggerAxis() - CO_PILOT.getLeftTriggerAxis())) > 0.03)).whenActive(    
        new ParallelCommandGroup(   
        new RunIndexer(indexer, () -> (CO_PILOT.getRightTriggerAxis() - CO_PILOT.getLeftTriggerAxis())), 
        new RunIntake(intake, () -> (CO_PILOT.getRightTriggerAxis() - CO_PILOT.getLeftTriggerAxis()))
        ));

        (new JoystickButton(CO_PILOT, 8)).whenPressed(new InstantCommand(() -> indexer.resetBallCount())); // start button to reset
		(new JoystickButton(CO_PILOT, 1)).whenPressed(new AimTurretNoVision(turret, howitzerDrivetrain));
    }

    @Override
    protected void configureDefaultCommands() {
		// VContinous = new VoltageTestContinuous(shooter);
		// VContinous = new VoltageTestContinuous(shooter);
		// shooter.setDefaultCommand(new MoveShooter(shooter));
		// indexer.setDefaultCommand(new QueueBalls(indexer));
        // turret.setDefaultCommand(new AimTurret(turret, vision, () -> filter.filter(driver.getLeftX()))); // this should return degrees
		// leds = new LEDs();

        // shooter.setDefaultCommand(new MoveShooter(shooter));

		// indexer.setDefaultCommand(new RunIndexer(indexer, ()-> driver.getLeftY()));

        //turret.setDefaultCommand(new AimTurret(turret, vision)); // this should return degrees
		drivetrain.setDefaultCommand(new DifferentialTankDrive(drivetrain, () -> -DRIVER_LEFT.getY() , () -> -DRIVER_RIGHT.getY(), FILTER));
	}

    @Override
    protected void configureFaultCodes() { }

    @Override
    protected void configureFaultMonitors() { }

    @Override
    protected void configureSystemTests() { }

    @Override
    public LightningDrivetrain getDrivetrain() {
        return null;
    }

    @Override
    protected void initializeDashboardCommands() { }
	
    @Override
    protected void releaseDefaultCommands() { }
    
}
