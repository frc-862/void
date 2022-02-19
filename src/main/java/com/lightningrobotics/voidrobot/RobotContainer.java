package com.lightningrobotics.voidrobot;

import com.lightningrobotics.common.LightningContainer;
import com.lightningrobotics.common.subsystem.drivetrain.LightningDrivetrain;
import com.lightningrobotics.common.util.filter.JoystickFilter;
import com.lightningrobotics.common.util.filter.JoystickFilter.Mode;
import com.lightningrobotics.voidrobot.commands.AimTurret;
import com.lightningrobotics.voidrobot.commands.RunIndexer;
import com.lightningrobotics.voidrobot.commands.RunShooter;
import com.lightningrobotics.voidrobot.commands.test.VoltageTestContinuous;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.LEDs;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import java.nio.file.DirectoryStream.Filter;
import java.util.Arrays;

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
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class RobotContainer extends LightningContainer{

    // Subsystems
    private static Turret turret = new Turret();
    private static Vision vision = new Vision();
	private static LEDs leds = new LEDs();
	private static Shooter shooter = new Shooter();
	private static Indexer indexer = new Indexer();
    private static Drivetrain drivetrain = new Drivetrain();

    private static final XboxController copilot = new XboxController(0); //TODO: set right ID
    private static final XboxController climb = new XboxController(1); //TODO: set right ID
    private static final Joystick driverLeft = new Joystick(6); //TODO: nice (set right ID)
    private static final Joystick driverRight = new Joystick(9); //TODO: nice (set right ID)

    private static final JoystickFilter filter = new JoystickFilter(0.15, 0.01, 1, Mode.LINEAR);

    // TODO commands shouldn't be here . . .
	// private static VoltageTestContinuous VContinous;
	// private static MoveShooter moveShooter = new MoveShooter(shooter);

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
        // if(xbox.getBButtonPressed()) {
            //TODO: left trigger down (analogue) 
            // TODO: 
    }

    @Override
    protected void configureDefaultCommands() {
		//DRIVER
        // (new JoystickButton(driverRight, 1)).whileHeld(new RunAutoShoot(shooter, indexer)); //Auto shoot
        // (new TriggerAndThumb((new JoystickButton(driverRight, 1)), (new JoystickButton(driverRight, 2)))).whenPressed(new ShootClose(shooter)); // shoot close
        

        //COPILOT
        // (new Trigger(() -> copilot.getRightTriggerAxis() > 0.03)).whenActive(new InstantCommand(() -> intake.setPower(copilot.getRightTriggerAxis()), intake)); //intake 
        // (new JoystickButton(copilot, 1)).whenPressed(new DeployIntake(intake)); //Deploy intake
        // (new JoystickButton(copilot, 4)).whenPressed(new RetractIntake(intake)); //Retract intake
        // (new JoystickButton(copilot, 5)).whenActive(new InstantCommand(() -> indexer.setPower(Constants.DEFAULT_INDEXER_POWER), intake)); //Manual intake up
        // (new JoystickButton(copilot, 6)).whenActive(new InstantCommand(() -> indexer.setPower(-Constants.DEFAULT_INDEXER_POWER), intake)); //Manual intake down
        // (new Trigger(() -> copilot.getLeftTriggerAxis() > 0.03)).whenActive(new InstantCommand(() -> indexer.setPower(-copilot.getLeftTriggerAxis()), indexer)); //indexer out
        // (new Trigger(() -> copilot.getLeftTriggerAxis() > 0.03)).whenActive(new InstantCommand(() -> intake.setPower(-copilot.getLeftTriggerAxis()), intake)); //intake out
        //TODO: add bias stuff
        /*
        (new POVButton(climb, 0)).whenPressed(new InstantCommand()); //TODO: add climber stuff
        (new POVButton(climb, 180)).whenPressed(new InstantCommand()); //TODO: add climber stuff
        */
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
