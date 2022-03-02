package com.lightningrobotics.voidrobot;

import java.time.Instant;

import com.lightningrobotics.common.LightningContainer;
import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.common.subsystem.drivetrain.LightningDrivetrain;
import com.lightningrobotics.common.util.filter.JoystickFilter;
import com.lightningrobotics.common.util.filter.JoystickFilter.Mode;
import com.lightningrobotics.common.util.operator.trigger.TwoButtonTrigger;
import com.lightningrobotics.voidrobot.commands.auto.*;
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
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class RobotContainer extends LightningContainer{

	public static final boolean TESTING = true;

    // Subsystems
	private static final LightningIMU imu = LightningIMU.navX();
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
	private static final JoystickFilter driverFilter = new JoystickFilter(0.13, 0.1, 1, Mode.CUBED);

    @Override
    protected void configureAutonomousCommands() {
		Autonomous.register("4 Ball Terminal", new FourBallTerminal(drivetrain, indexer, intake, shooter));
		Autonomous.register("4 Ball Hanger", new FourBallHanger(drivetrain, indexer, intake, shooter));
        if(TESTING) registerTestPaths();        
    }

    @Override
    protected void configureButtonBindings() {
		
        // DRIVER
        // (new JoystickButton(driverRight, 1)).whileHeld(new ShootCargo(shooter, indexer, turret, vision)); // Auto shoot
        // (new TwoButtonTrigger((new JoystickButton(driverRight, 1)), (new JoystickButton(driverRight, 2)))).whenActive(new ShootClose(shooter, indexer, turret)); // Shoot close no vision
        
        // // COPILOT
        (new Trigger(() -> copilot.getRightTriggerAxis() > 0.03)).whenActive(new RunIntake(intake, () -> copilot.getRightTriggerAxis())); //intake 
        (new JoystickButton(copilot, 1)).whenPressed(new DeployIntake(intake)); //Deploy intake
        (new JoystickButton(copilot, 4)).whenPressed(new RetractIntake(intake)); //Retract intake
        (new JoystickButton(copilot, 5)).whileHeld(new RunIndexer(indexer, () -> Constants.DEFAULT_INDEXER_POWER)); //Manual intake up
        (new JoystickButton(copilot, 6)).whileHeld(new RunIndexer(indexer, () -> -Constants.DEFAULT_INDEXER_POWER)); //Manual intake down
        (new Trigger(() -> copilot.getLeftTriggerAxis() > 0.03)).whenActive(
            new ParallelCommandGroup(
                new RunIndexer(indexer, () -> copilot.getLeftTriggerAxis()),
                new RunIntake(intake, () -> copilot.getLeftTriggerAxis())
            ));
        (new JoystickButton(copilot, 8)).whenPressed(new InstantCommand(() -> indexer.resetBallCount())); // start button to reset
		// // TODO: add bias stuff
        
		// // CLIMB
		// // TODO: add climber stuff
		(new POVButton(climb, 0)).whenPressed(new InstantCommand()); 
        (new POVButton(climb, 180)).whenPressed(new InstantCommand()); 		
    }

    @Override
    protected void configureDefaultCommands() {
		//drivetrain.setDefaultCommand(new DifferentialTankDrive(drivetrain, () -> -driverLeft.getY() , () -> -driverRight.getY(), driverFilter));
         turret.setDefaultCommand(new AimTurret(turret, vision, drivetrain, () -> copilot.getRightX(), () -> copilot.getRightY()));

        //shooter.setDefaultCommand(new MoveHood(shooter, () -> copilot.getRightY()));
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
		var tab = Shuffleboard.getTab("Vision");
		// tab.add(new InstantCommand(() -> vision.turnOnVisionLight(), vision));
		// tab.add(new InstantCommand(() -> vision.turnOffVisionLight(), vision));
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
