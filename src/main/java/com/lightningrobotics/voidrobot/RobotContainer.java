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
import com.lightningrobotics.voidrobot.commands.VoidDrivetrain;
import com.lightningrobotics.voidrobot.commands.test.VoltageTestContinuous;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Intake;
import com.lightningrobotics.voidrobot.subsystems.LEDs;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.TriggerAndThumb;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

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
    private static Intake intake = new Intake();
	private static Indexer indexer = new Indexer();
    private static Drivetrain drivetrain = new Drivetrain();

    private static final XboxController copilot = new XboxController(0); //TODO: set right ID
    private static final XboxController climb = new XboxController(1); //TODO: set right ID
    private static final Joystick driverLeft = new Joystick(6); //TODO: nice (set right ID)
    private static final Joystick driverRight = new Joystick(9); //TODO: nice (set right ID)

    private static final JoystickFilter filter = new JoystickFilter(0.15, 0.01, 1, Mode.LINEAR);

    public RobotContainer() {
        super();
    }

    @Override
    protected void configureAutonomousCommands() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void configureButtonBindings() {
        //DRIVER
        // (new JoystickButton(driverRight, 1)).whileHeld(new RunAutoShoot(shooter, indexer)); //Auto shoot
        // (new TriggerAndThumb((new JoystickButton(driverRight, 1)), (new JoystickButton(driverRight, 2)))).whenPressed(new ShootClose(shooter)); // shoot close
        

        //COPILOT
        // (new Trigger(() -> copilot.getRightTriggerAxis() > 0.03)).whenActive(new InstantCommand(() -> intake.setPower(copilot.getRightTriggerAxis()), intake)); //intake 
        // (new JoystickButton(copilot, 1)).whenPressed(new DeployIntake(intake)); //Deploy intake
        // (new JoystickButton(copilot, 4)).whenPressed(new RetractIntake(intake)); //Retract intake
        // (new JoystickButton(copilot, 5)).whenActive(new InstantCommand(() -> indexer.setPower(Constants.DEFAULT_INDEXER_POWER), intake)); //Manual intake up
        // (new JoystickButton(copilot, 6)).whenActive(new InstantCommand(() -> indexer.setPower(-Constants.DEFAULT_INDEXER_POWER), intake)); //Manual intake down
        (new Trigger(() -> Math.abs((copilot.getRightTriggerAxis() - copilot.getLeftTriggerAxis())) > 0.03)).whenActive(    
        new ParallelCommandGroup(   
        new RunIndexer(indexer, () -> (copilot.getRightTriggerAxis() - copilot.getLeftTriggerAxis())), 
        new RunIntake(intake, () -> (copilot.getRightTriggerAxis() - copilot.getLeftTriggerAxis()))
        ));

        (new JoystickButton(copilot, 8)).whenPressed(new InstantCommand(() -> indexer.resetBallCount())); // start button to reset

        // (new Trigger(() -> copilot.getLeftTriggerAxis() > 0.03)).whenActive(new InstantCommand(() -> indexer.setPower(-copilot.getLeftTriggerAxis()), indexer)); //indexer out
        // (new Trigger(() -> copilot.getLeftTriggerAxis() > 0.03)).whenActive(new InstantCommand(() -> intake.setPower(-copilot.getLeftTriggerAxis()), intake)); //intake out
        //TODO: add bias stuff
        /*
        (new POVButton(climb, 0)).whenPressed(new InstantCommand()); //TODO: add climber stuff
        (new POVButton(climb, 180)).whenPressed(new InstantCommand()); //TODO: add climber stuff
        */
    }

    @Override
    protected void configureDefaultCommands() {
		// drivetrain.setDefaultCommand(new VoidDrivetrain(drivetrain, () -> -driverLeft.getY(), () -> -driverLeft.getY()));
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
