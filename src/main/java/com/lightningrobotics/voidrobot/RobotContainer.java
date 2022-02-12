package com.lightningrobotics.voidrobot;

import com.lightningrobotics.common.LightningContainer;
import com.lightningrobotics.common.subsystem.drivetrain.LightningDrivetrain;
import com.lightningrobotics.common.util.filter.JoystickFilter;
import com.lightningrobotics.common.util.filter.JoystickFilter.Mode;
import com.lightningrobotics.voidrobot.commands.AimTurret;
import com.lightningrobotics.voidrobot.commands.Drive;
import com.lightningrobotics.voidrobot.commands.QueueBalls;
import com.lightningrobotics.voidrobot.commands.VoltageTestContinuous;
import com.lightningrobotics.voidrobot.commands.MoveShooter;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.LEDs;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class RobotContainer extends LightningContainer{

    // Subsystems
<<<<<<< HEAD
    // private static Turret turret = new Turret();
	// private static LEDs leds = new LEDs();
	// private static Shooter shooter = new Shooter();
	private static Indexer indexer = new Indexer();

    private static final XboxController driver = new XboxController(0); //TODO: set right ID

    // private static final JoystickFilter filter = new JoystickFilter(0.15, 0.01, 1, Mode.LINEAR);
=======
    // private static Turret turret;
	// private static LEDs leds = new LEDs();
	private static Shooter shooter = new Shooter();
	// private static Indexer indexer = new Indexer();

    // private static final XboxController xbox = new XboxController(1); //TODO: set right ID

    private static moveShooter shooterMove = new moveShooter(shooter);
>>>>>>> feature/PROG-159-template-subsystems

    // TODO commands shouldn't be here . . .
    // Cap
	// private static VoltageTestContinuous VContinous;
<<<<<<< HEAD
	// private static MoveShooter moveShooter = new MoveShooter(shooter);
=======
	// private static moveShooter shooterMove;
>>>>>>> feature/PROG-159-template-subsystems

    public RobotContainer() {
        super();
    }

    @Override
    protected void configureAutonomousCommands() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void configureButtonBindings() {
<<<<<<< HEAD
        if(driver.getBButtonPressed()) {
            //TODO: left trigger down (analogue), left bumper up
        }
=======
        // if(xbox.getBButtonPressed()) {
            //TODO: left trigger down (analogue) 
            // TODO: 
                 
>>>>>>> feature/PROG-159-template-subsystems
        
    }

    @Override
    protected void configureDefaultCommands() {
        // turret.setDefaultCommand(new TurnTurret(turret));

		// VContinous = new VoltageTestContinuous(shooter);

		// VContinous = new VoltageTestContinuous(shooter);

		// shooter.setDefaultCommand(new MoveShooter(shooter));

<<<<<<< HEAD
		// indexer.setDefaultCommand(new QueueBalls(indexer));

        // turret.setDefaultCommand(new AimTurret(turret, () -> driver.getLeftX() * 180)); // this should return degrees
=======
		shooter.setDefaultCommand(shooterMove);

		// indexer.setDefaultCommand(new QueueBalls(indexer));
>>>>>>> feature/PROG-159-template-subsystems

		// leds = new LEDs();
        
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
