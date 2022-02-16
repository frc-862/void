package com.lightningrobotics.voidrobot;

import com.lightningrobotics.common.LightningContainer;
import com.lightningrobotics.common.subsystem.drivetrain.LightningDrivetrain;
import com.lightningrobotics.common.util.filter.JoystickFilter;
import com.lightningrobotics.common.util.filter.JoystickFilter.Mode;
import com.lightningrobotics.voidrobot.commands.AimTurret;
import com.lightningrobotics.voidrobot.commands.RunIndexer;
import com.lightningrobotics.voidrobot.commands.RunShooter;
import com.lightningrobotics.voidrobot.commands.test.VoltageTestContinuous;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.LEDs;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

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
	// private static LEDs leds = new LEDs();
	// private static Shooter shooter = new Shooter();
	private static Indexer indexer = new Indexer();
	private static final Drivetrain drivetrain = new Drivetrain();
	
	private static final Joystick driverLeft = new Joystick(Constants.DRIVER_LEFT_PORT);
	private static final Joystick driverRight = new Joystick(Constants.DRIVER_RIGHT_PORT);
	private static final XboxController driver = new XboxController(Constants.DRIVER_PORT); // changed from joystick to xboxcontroller

	private static final JoystickFilter FILTER = new JoystickFilter(0.15, 0.1, 1, Mode.LINEAR); // TODO test this filters

    // TODO commands shouldn't be here . . .
	// private static VoltageTestContinuous VContinous;
	// private static MoveShooter moveShooter = new MoveShooter(shooter);

    public RobotContainer() {
        super();
    }

    @Override
    protected void configureAutonomousCommands() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void configureButtonBindings() {
        // if(xbox.getBButtonPressed()) {
            //TODO: left trigger down (analogue) 
            // TODO: 
    }

    @Override
    protected void configureDefaultCommands() {
		// VContinous = new VoltageTestContinuous(shooter);
		// VContinous = new VoltageTestContinuous(shooter);
		// shooter.setDefaultCommand(new MoveShooter(shooter));
		// indexer.setDefaultCommand(new QueueBalls(indexer));
        // turret.setDefaultCommand(new AimTurret(turret, vision, () -> FILTER.FILTER(driver.getLeftX()))); // this should return degrees
		// leds = new LEDs();

        // shooter.setDefaultCommand(new MoveShooter(shooter));

		//drivetrain.setDefaultCommand(new DifferentialTankDrive(drivetrain, ()-> -driver.getLeftY(), ()-> -driver.getRightY(), FILTER));

		// indexer.setDefaultCommand(new RunIndexer(indexer, ()-> driver.getLeftY()));

        // turret.setDefaultCommand(new AimTurret(turret, () -> driver.getLeftX() * 180)); // this should return degrees
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
