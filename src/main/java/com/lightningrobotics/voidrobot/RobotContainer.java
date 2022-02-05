package com.lightningrobotics.voidrobot;

import com.lightningrobotics.voidrobot.commands.Drive;
import com.lightningrobotics.voidrobot.commands.TurnTurret;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Intake;
import com.lightningrobotics.voidrobot.subsystems.LEDs;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of
 * the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
	// TODO use lightning base when ready

	// private static final Drivetrain drivetrain = new Drivetrain();

	// private static final Intake intake = new Intake();

	private static Turret turret;

	private static LEDs leds;

	private static final XboxController driver = new XboxController(0);

	

	public RobotContainer() {
		// Configure the button bindings
		configureButtonBindings();
		
		// turret.setDefaultCommand(new TurnTurret(turret));

		// drivetrain.setDefaultCommand(new Drive(drivetrain, () -> driver.getRawAxis(1), () -> driver.getRawAxis(5)));

		turret = new Turret(() -> driver.getRightX());

		// leds = new LEDs();
	}

	/**
	 * Use this method to define your button->command mappings. Buttons can be
	 * created by
	 * instantiating a {@link GenericHID} or one of its subclasses ({@link
	 * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing
	 * it to a {@link
	 * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
	 */
	private void configureButtonBindings() {
		JoystickButton button = new JoystickButton(driver, 1);

		button.whenPressed(new InstantCommand(() -> leds.setAllRGB(0, 0, 255)));

		button.whenReleased(new InstantCommand(() -> leds.stopLEDs()));
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
}
