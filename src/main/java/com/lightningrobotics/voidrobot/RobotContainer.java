package com.lightningrobotics.voidrobot;

import com.lightningrobotics.common.LightningContainer;
import com.lightningrobotics.common.subsystem.drivetrain.LightningDrivetrain;
import com.lightningrobotics.voidrobot.commands.QueueBalls;
import com.lightningrobotics.voidrobot.commands.VoltageTestContinuous;
import com.lightningrobotics.voidrobot.commands.moveShooter;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.LEDs;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.wpilibj.Joystick;

public class RobotContainer extends LightningContainer{

    // Subsystems
    private static Turret turret;
	private static LEDs leds = new LEDs();
	private static Shooter shooter;
	private static Indexer indexer = new Indexer();

    // TODO commands shouldn't be here . . .
    // Cap
	private static VoltageTestContinuous VContinous;
	private static moveShooter shooterMove;

    public RobotContainer() {
        super();
    }

    @Override
    protected void configureAutonomousCommands() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void configureButtonBindings() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void configureDefaultCommands() {
        // turret.setDefaultCommand(new TurnTurret(turret));

		// drivetrain.setDefaultCommand(new Drive(drivetrain, () -> driver.getRawAxis(1), () -> driver.getRawAxis(5)));

		// turret = new Turret(() -> driver.getRightX());

		// shooter = new Shooter();

		// VContinous = new VoltageTestContinuous(shooter);

		// VContinous = new VoltageTestContinuous(shooter);

		// shooterMove = new moveShooter(shooter);

		// shooter.setDefaultCommand(shooterMove);

		indexer.setDefaultCommand(new QueueBalls(indexer));

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
