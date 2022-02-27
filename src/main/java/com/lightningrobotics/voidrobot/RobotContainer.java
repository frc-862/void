package com.lightningrobotics.voidrobot;

import com.lightningrobotics.common.LightningContainer;
import com.lightningrobotics.common.subsystem.drivetrain.LightningDrivetrain;
import com.lightningrobotics.common.util.filter.JoystickFilter;
import com.lightningrobotics.common.util.filter.JoystickFilter.Mode;
import com.lightningrobotics.voidrobot.commands.AimTurret;
import com.lightningrobotics.voidrobot.commands.DeployIntake;
import com.lightningrobotics.voidrobot.commands.RetractIntake;
import com.lightningrobotics.voidrobot.commands.RunAutoShoot;
import com.lightningrobotics.voidrobot.commands.RunIndexer;
import com.lightningrobotics.voidrobot.commands.RunIntake;
import com.lightningrobotics.voidrobot.commands.RunShooter;
import com.lightningrobotics.voidrobot.commands.ShootClose;
import com.lightningrobotics.voidrobot.commands.auto.FourBallHanger;
import com.lightningrobotics.voidrobot.commands.auto.FourBallTerminal;
import com.lightningrobotics.voidrobot.commands.test.VoltageTestContinuous;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.constants.JoystickConstants;
import com.lightningrobotics.voidrobot.commands.test.VoltageTestContinuous;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Intake;
import com.lightningrobotics.voidrobot.subsystems.LEDs;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.TriggerAndThumb;
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
	private static final Drivetrain drivetrain = new Drivetrain();
    private static final Turret turret = new Turret();
	private static final Shooter shooter = new Shooter();
	private static final Indexer indexer = new Indexer();
	private static final Intake intake = new Intake();
    private static final Vision vision = new Vision();
	private static final LEDs leds = new LEDs();
	
	// Joysticks
	private static final Joystick driverLeft = new Joystick(JoystickConstants.DRIVER_LEFT_PORT);
	private static final Joystick driverRight = new Joystick(JoystickConstants.DRIVER_RIGHT_PORT);
	private static final XboxController copilot = new XboxController(JoystickConstants.CO_PILOT_PORT);
	private static final JoystickFilter driverFilter = new JoystickFilter(0.07, 0.1, 1, Mode.CUBED);

    @Override
    protected void configureAutonomousCommands() {
		Autonomous.register("4 Ball Terminal", new FourBallTerminal(drivetrain, indexer, intake, shooter));
		Autonomous.register("4 Ball Hanger", new FourBallHanger(drivetrain, indexer, intake, shooter));
        registerTestPaths();        
    }

    @Override
    protected void configureButtonBindings() {
		
        // DRIVER
        // (new JoystickButton(DRIVER_RIGHT, 1)).whileHeld(new RunAutoShoot(shooter, indexer)); //Auto shoot
        // (new TriggerAndThumb((new JoystickButton(DRIVER_RIGHT, 1)), (new JoystickButton(DRIVER_RIGHT, 2)))).whenPressed(new ShootClose(shooter)); // shoot close
        
        // COPILOT
        // (new Trigger(() -> CO_PILOT.getRightTriggerAxis() > 0.03)).whenActive(new InstantCommand(() -> intake.setPower(CO_PILOT.getRightTriggerAxis()), intake)); //intake 
        // (new JoystickButton(CO_PILOT, 1)).whenPressed(new DeployIntake(intake)); //Deploy intake
        // (new JoystickButton(CO_PILOT, 4)).whenPressed(new RetractIntake(intake)); //Retract intake
        // (new JoystickButton(CO_PILOT, 5)).whenActive(new InstantCommand(() -> indexer.setPower(Constants.DEFAULT_INDEXER_POWER), intake)); //Manual intake up
        // (new JoystickButton(CO_PILOT, 6)).whenActive(new InstantCommand(() -> indexer.setPower(-Constants.DEFAULT_INDEXER_POWER), intake)); //Manual intake down
        // (new Trigger(() -> CO_PILOT.getLeftTriggerAxis() > 0.03)).whenActive(new InstantCommand(() -> indexer.setPower(-CO_PILOT.getLeftTriggerAxis()), indexer)); //indexer out
        // (new Trigger(() -> CO_PILOT.getLeftTriggerAxis() > 0.03)).whenActive(new InstantCommand(() -> intake.setPower(-CO_PILOT.getLeftTriggerAxis()), intake)); //intake out
        // TODO: add bias stuff
        // (new POVButton(climb, 0)).whenPressed(new InstantCommand()); //TODO: add climber stuff
        // (new POVButton(climb, 180)).whenPressed(new InstantCommand()); //TODO: add climber stuff
		// (new Trigger(() -> Math.abs((CO_PILOT.getRightTriggerAxis() - CO_PILOT.getLeftTriggerAxis())) > 0.03)).whenActive(    
        // 	new ParallelCommandGroup(   
		// 		new RunIndexer(indexer, () -> (CO_PILOT.getRightTriggerAxis() - CO_PILOT.getLeftTriggerAxis())), 
		// 		new RunIntake(intake, () -> (CO_PILOT.getRightTriggerAxis() - CO_PILOT.getLeftTriggerAxis()))
        // ));
        // (new JoystickButton(CO_PILOT, 8)).whenPressed(new InstantCommand(() -> indexer.resetBallCount())); // start button to reset

    }

    @Override
    protected void configureDefaultCommands() {
		drivetrain.setDefaultCommand(new DifferentialTankDrive(drivetrain, () -> -driverLeft.getY() , () -> -driverRight.getY(), driverFilter));
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
