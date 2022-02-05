package com.lightningrobotics.voidrobot;


import java.util.Arrays;

import com.lightningrobotics.common.LightningContainer;
import com.lightningrobotics.common.auto.Autonomous;
import com.lightningrobotics.common.command.drivetrain.differential.DifferentialTankDrive;
import com.lightningrobotics.common.subsystem.drivetrain.LightningDrivetrain;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;

import com.lightningrobotics.common.auto.Path;

import edu.wpi.first.math.geometry.Pose2d;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of
 * the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer extends LightningContainer {
	private static final Drivetrain drivetrain = new Drivetrain();
	
	private static final Joystick driverLeft = new Joystick(Constants.DRIVER_LEFT_PORT);
	private static final Joystick driverRight = new Joystick(Constants.DRIVER_RIGHT_PORT);
	private static final XboxController driver = new XboxController(Constants.DRIVER_PORT); // changed from joystick to xboxcontroller

	public RobotContainer() {
		// Configure the button bindings
		configureButtonBindings();
		

		// drivetrain.setDefaultCommand(new DifferentialTankDrive(drivetrain, ()-> -driver.getLeftY(), ()-> -driver.getRightY())); // uses default lightning joystick filter, could add one later
		drivetrain.setDefaultCommand(new DifferentialTankDrive(drivetrain, ()-> -driverLeft.getY(), ()-> -driverRight.getY())); // uses driverstation joystick
	}

	@Override
	protected void configureAutonomousCommands() {
		try {
			Autonomous.register("Test Differential Auton", 
			(new Path(Arrays.asList(new Pose2d(0d, 0d, Rotation2d.fromDegrees(0d)), 
				new Pose2d(5d, 0d, Rotation2d.fromDegrees(0d))))).getCommand(drivetrain));
		} catch(Exception e) {
			System.err.println("Unexpected Error: " + e.getMessage());
		}
	}

	/**
	 * Use this to pass the autonomous command to the main {@link Robot} class.
	 *
	 * @return the command to run in autonomous
	 */
	public Command getAutonomousCommand() {
		// An ExampleCommand will run in autonomous
		return null;
	}

	@Override
	protected void configureButtonBindings() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void configureDefaultCommands() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void configureFaultCodes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void configureFaultMonitors() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void configureSystemTests() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LightningDrivetrain getDrivetrain() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initializeDashboardCommands() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void releaseDefaultCommands() {
		// TODO Auto-generated method stub
		
	}
}
