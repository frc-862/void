package com.lightningrobotics.voidrobot;

import java.util.Arrays;

import com.lightningrobotics.common.LightningContainer;
import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.common.subsystem.drivetrain.LightningDrivetrain;
import com.lightningrobotics.common.util.filter.JoystickFilter;
import com.lightningrobotics.common.util.filter.JoystickFilter.Mode;
import com.lightningrobotics.voidrobot.commands.ZeroTurretHood;
import com.lightningrobotics.voidrobot.commands.auto.paths.FiveBallTerminal;
import com.lightningrobotics.voidrobot.commands.auto.paths.OneBall;
import com.lightningrobotics.voidrobot.commands.auto.paths.TwoBall;
import com.lightningrobotics.voidrobot.commands.climber.NextRung;
import com.lightningrobotics.voidrobot.commands.climber.arms.ArmsEngageHooks;
import com.lightningrobotics.voidrobot.commands.climber.arms.ArmsToReach;
import com.lightningrobotics.voidrobot.commands.climber.arms.MoveArmsManual;
import com.lightningrobotics.voidrobot.commands.climber.arms.StartMidClimb;
import com.lightningrobotics.voidrobot.commands.climber.GetReadyForClimb;
import com.lightningrobotics.voidrobot.commands.climber.ManualClimb;
import com.lightningrobotics.voidrobot.commands.climber.pivot.MoveBothPivots;
import com.lightningrobotics.voidrobot.commands.climber.pivot.MoveLeftPivot;
import com.lightningrobotics.voidrobot.commands.climber.pivot.MoveRightPivot;
import com.lightningrobotics.voidrobot.commands.climber.pivot.PivotToHold;
import com.lightningrobotics.voidrobot.commands.climber.pivot.PivotToReach;
import com.lightningrobotics.voidrobot.commands.hood.ResetHood;
import com.lightningrobotics.voidrobot.commands.indexer.*;
import com.lightningrobotics.voidrobot.commands.intake.*;

import com.lightningrobotics.voidrobot.commands.shooter.*;
import com.lightningrobotics.voidrobot.commands.turret.*;
import com.lightningrobotics.voidrobot.constants.*;
import com.lightningrobotics.voidrobot.subsystems.*;
import com.lightningrobotics.common.auto.*;
import com.lightningrobotics.common.command.core.TimedCommand;
import com.lightningrobotics.common.command.drivetrain.differential.DifferentialTankDrive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.*;

public class RobotContainer extends LightningContainer {

	public static final boolean TESTING = true;

    // Subsystems
	private static final LightningIMU imu = LightningIMU.navX();
    private static final Climber climber = new Climber(imu);
	private static final Drivetrain drivetrain = new Drivetrain(imu);
    private static final Turret turret = new Turret();
	private static final Shooter shooter = new Shooter();
	private static final Indexer indexer = new Indexer();
	private static final Intake intake = new Intake();
    private static final Hood hood = new Hood();
	// private static final LEDs leds = new LEDs();
                                            
	// Joysticks
	private static final Joystick driverLeft = new Joystick(JoystickConstants.DRIVER_LEFT_PORT);
	private static final Joystick driverRight = new Joystick(JoystickConstants.DRIVER_RIGHT_PORT);
	private static final XboxController copilot = new XboxController(JoystickConstants.COPILOT_PORT);
	private static final XboxController climb = new XboxController(JoystickConstants.CLIMB_PORT);

	// Joystick Filters
    private static final JoystickFilter driverFilter = new JoystickFilter(0.13, 0.1, 1, Mode.CUBED);
    // private static final JoystickFilter copilotFilter = new JoystickFilter(0.13, 0.1, 1, Mode.LINEAR);

	private static final HubTargeting targeting = new HubTargeting(drivetrain::getPose, () -> drivetrain.getGains().getKinematics().forward(drivetrain.getDriveState()), turret::getCurrentAngle, hood::getAngle, shooter::getCurrentRPM);

    @Override
    protected void configureAutonomousCommands() {

        try {
			Autonomous.register("Taxi", new Path("1-2Ball.path", false).getCommand(drivetrain));
			Autonomous.register("2 Ball", new TwoBall(drivetrain, shooter, hood, turret, indexer, intake, targeting));
            Autonomous.register("1 Ball", new OneBall(drivetrain, shooter, hood, turret, indexer, intake, targeting));
            Autonomous.register("5 Ball Terminal", new FiveBallTerminal(drivetrain, indexer, intake, shooter, hood, turret, targeting));
		} catch (Exception e) {
			System.err.println("I did an oopsie.");
            e.printStackTrace();
		}

        if(TESTING) registerTestPaths();        
    }

    @Override
    protected void configureButtonBindings() {

        // DRIVER
        (new JoystickButton(driverRight, 1)).whileHeld(new ShootCargo(shooter, hood, indexer, targeting), false); // Auto shoot
        (new JoystickButton(driverLeft, 1)).whileHeld(new ShootCargoManual(shooter, hood, indexer, turret, targeting), false); // Auto shoot
        (new JoystickButton(driverRight, 2)).whileHeld(new ShootClose(shooter, hood, indexer, turret, targeting), false); // Shoot close no vision
		(new JoystickButton(driverLeft, 2)).whileHeld(new ZeroTurretHood(hood, turret));
        // (new JoystickButton(driverRight, 3)).toggleWhenPressed(new AutoShoot(drivetrain, targeting, turret, indexer, shooter, hood));

        // COPILOT
        (new Trigger(() -> copilot.getRightTriggerAxis() > 0.03)).whenActive(new RunIntake(intake, () -> copilot.getRightTriggerAxis())); //RT: run collector in
        (new JoystickButton(copilot, JoystickConstants.BUTTON_B)).whileHeld(new RunIntake(intake, () -> -1)); //B: run collector out
        (new JoystickButton(copilot, JoystickConstants.RIGHT_BUMPER)).whileHeld(new MoveIntake(intake, () -> -Constants.DEFAULT_WINCH_POWER)); //RB: Retract intake
        (new JoystickButton(copilot, JoystickConstants.BUTTON_BACK)).whileHeld(new MoveIntake(intake, () -> Constants.DEFAULT_WINCH_POWER)); //SELECT/BACK: Deploy intake
        (new JoystickButton(copilot, JoystickConstants.BUTTON_Y)).whileHeld(new AutoIndexCargo(indexer));
        (new JoystickButton(copilot, JoystickConstants.LEFT_BUMPER)).whileHeld(new RunIndexer(indexer, () -> -Constants.DEFAULT_INDEXER_POWER)); //LB: run indexer down
        (new Trigger(() -> copilot.getLeftTriggerAxis() > 0.03)).whileActiveContinuous(new RunIndexer(indexer, () -> copilot.getLeftTriggerAxis()));//LT: run indexer up
        (new JoystickButton(copilot, JoystickConstants.BUTTON_START)).whenPressed(new InstantCommand(() -> indexer.resetBallCount())); //START: Reset ball count

		// CLIMB
		// (new JoystickButton(climb, JoystickConstants.BUTTON_A)).whenPressed(new MakeHoodAndTurretZero(turret, shooter));
        (new JoystickButton(climb, JoystickConstants.BUTTON_START)).whileHeld(new GetReadyForClimb(hood, turret, shooter, targeting));
        (new JoystickButton(climb, JoystickConstants.BUTTON_BACK)).whenPressed(
            new SequentialCommandGroup(
                new InstantCommand(() -> turret.setDisableTurret(false)),
                new InstantCommand(() -> hood.setDisableHood(false))
        ));
        // (new JoystickButton(climb, JoystickConstants.RIGHT_BUMPER)).whileHeld(new PivotToHold(climber));
        // (new JoystickButton(climb, JoystickConstants.LEFT_BUMPER)).whileHeld(new PivotToReach(climber));
        (new POVButton(climb, 0)).whileHeld(new MoveArmsManual(climber, 1));
        (new POVButton(climb, 180)).whileHeld(new MoveArmsManual(climber, -1));
        (new POVButton(climb, 90)).whileHeld(new PivotToHold(climber));
        (new POVButton(climb, 270)).whileHeld(new PivotToReach(climber));
        // (new JoystickButton(climb, JoystickConstants.BUTTON_A)).whenPressed(new NextRung(climber).withInterrupt(() -> new JoystickButton(climb, JoystickConstants.BUTTON_B).get()), false);

        // (new JoystickButton(climb, JoystickConstants.BUTTON_B)).whileHeld(new ArmsEngageHooks(climber));

        (new JoystickButton(climb, JoystickConstants.BUTTON_Y)).whenHeld(new StartMidClimb(climber));
        (new JoystickButton(climb, JoystickConstants.BUTTON_X)).whenHeld(new SequentialCommandGroup(
                                                                        new ParallelCommandGroup(
                                                                            new PivotToReach(climber),
                                                                            new SequentialCommandGroup(
                                                                                new WaitCommand(0.7),
                                                                                new ArmsToReach(climber)
                                                                            )
                                                                        ),
                                                                        new TimedCommand(new InstantCommand(climber::pivotToHold), 0.2),
                                                                            new InstantCommand(climber::stopPivot)
                                                                        )
                                                                    );
        (new JoystickButton(climb, JoystickConstants.BUTTON_A)).whenHeld(
                                                                new ParallelCommandGroup(
                                                                    new PivotToHold(climber),
                                                                    new SequentialCommandGroup (
                                                                        new WaitCommand(0.25),
                                                                        new ArmsEngageHooks(climber)
                                                                    )
                                                                )    
                                                            );

        // (new Trigger(() -> climb.getLeftTriggerAxis() > 0.03)).whileActiveContinuous(new MoveLeftPivot(climber, () -> climb.getLeftTriggerAxis()));
        // (new Trigger(() -> climb.getRightTriggerAxis() > 0.03)).whileActiveContinuous(new MoveRightPivot(climber, () -> climb.getRightTriggerAxis()));
        // (new JoystickButton(climb, JoystickConstants.LEFT_BUMPER)).whileHeld(new MoveLeftPivot(climber, () -> -Constants.DEFAULT_PIVOT_POWER));
        // (new JoystickButton(climb, JoystickConstants.RIGHT_BUMPER)).whileHeld(new MoveRightPivot(climber, () -> -Constants.DEFAULT_PIVOT_POWER));

        (new JoystickButton(climb, JoystickConstants.LEFT_BUMPER)).whileHeld(new MoveBothPivots(climber, () -> -Constants.DEFAULT_PIVOT_POWER));
        (new JoystickButton(climb, JoystickConstants.RIGHT_BUMPER)).whileHeld(new MoveBothPivots(climber, () -> Constants.DEFAULT_PIVOT_POWER));

        (new Trigger(() -> climb.getLeftTriggerAxis() > 0.03)).whileActiveContinuous(new MoveLeftPivot(climber, () -> -climb.getLeftTriggerAxis()));
        (new Trigger(() -> climb.getRightTriggerAxis() > 0.03)).whileActiveContinuous(new MoveRightPivot(climber, () -> -climb.getRightTriggerAxis()));


    }


    @Override
    protected void configureDefaultCommands() {        
		drivetrain.setDefaultCommand(new DifferentialTankDrive(drivetrain, () -> -driverLeft.getY() , () -> -driverRight.getY(), driverFilter));
        turret.setDefaultCommand(new AimTurret(turret, targeting));
		targeting.setDefaultCommand(new AdjustBias(targeting, () -> copilot.getPOV(), () -> (new JoystickButton(copilot, JoystickConstants.BUTTON_X).get())));
        // indexer.setDefaultCommand(new AutoIndexCargo(indexer));

        climber.setDefaultCommand(new ManualClimb(climber, () -> -climb.getLeftY(), () -> -climb.getRightY()));
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
		var tab = Shuffleboard.getTab("hood");

        var climbTab = Shuffleboard.getTab("climber");
		// var compTab = Shuffleboard.getTab("Competition");
		tab.add(new ResetHood(hood));
		// compTab.add(new MoveHoodManual(shooter, () -> copilot.getLeftY()));

        climbTab.add(new InstantCommand(climber::resetArmEncoders));
        
	}
	
    @Override
    protected void releaseDefaultCommands() { }

	private void registerTestPaths() {
		try {
			Autonomous.register("1 meter", 
                new SequentialCommandGroup(
                    new InstantCommand(() -> System.out.println("One Meter Forward Yay!")),
                    new Path(Arrays.asList(new Pose2d(0d, 0d, Rotation2d.fromDegrees(0)), 
                                        new Pose2d(1d, 0d, Rotation2d.fromDegrees(0)))).getCommand(drivetrain),
                    new InstantCommand(() -> System.out.println("Did We Move One Meter?"))
                )
                
            );
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
