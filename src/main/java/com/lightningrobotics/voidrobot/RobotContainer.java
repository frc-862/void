package com.lightningrobotics.voidrobot;

import java.util.Arrays;

import com.lightningrobotics.common.LightningContainer;
import com.lightningrobotics.common.subsystem.core.LightningIMU;
import com.lightningrobotics.common.subsystem.drivetrain.LightningDrivetrain;
import com.lightningrobotics.common.testing.SystemTest;
import com.lightningrobotics.common.testing.SystemTestCommand;
import com.lightningrobotics.common.util.filter.JoystickFilter;
import com.lightningrobotics.common.util.filter.JoystickFilter.Mode;
// import com.lightningrobotics.voidrobot.commands.SystemCheck;
// import com.lightningrobotics.voidrobot.commands.SystemCheckNoTest;
import com.lightningrobotics.voidrobot.commands.ZeroTurretHood;
import com.lightningrobotics.voidrobot.commands.auto.paths.FiveBallTerminal;
import com.lightningrobotics.voidrobot.commands.auto.paths.OneBall;
import com.lightningrobotics.voidrobot.commands.auto.paths.TwoBall;
import com.lightningrobotics.voidrobot.commands.climber.arms.ArmsDownLimit;
import com.lightningrobotics.voidrobot.commands.climber.arms.ArmsReleaseBar;
import com.lightningrobotics.voidrobot.commands.climber.arms.ArmsUpLimit;
import com.lightningrobotics.voidrobot.commands.climber.arms.ArmsManual;
import com.lightningrobotics.voidrobot.commands.climber.arms.ArmsMid;
import com.lightningrobotics.voidrobot.commands.climber.BackHooks;
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

import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.*;

public class RobotContainer extends LightningContainer {

	public static final boolean TESTING = true;

    // Subsystems
	private static final LightningIMU imu = LightningIMU.navX();
    private static final ClimbArms arms = new ClimbArms();
    private static final ClimbPivots pivots = new ClimbPivots();
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

	private static final HubTargeting targeting = new HubTargeting(drivetrain::getPose, () -> drivetrain.getGains().getKinematics().forward(drivetrain.getDriveState()), turret::getCurrentAngle, hood::getAngle, shooter::getCurrentRPM, indexer::getEjectedBall);

    @Override
    protected void configureAutonomousCommands() {

        try {
            Autonomous.register("1 Meter.path file", new Path("1Meter.path", false).getCommand(drivetrain));
			Autonomous.register("Taxi", new Path("1-2Ball.path", false).getCommand(drivetrain));
			Autonomous.register("2 Ball", new TwoBall(drivetrain, shooter, hood, turret, indexer, intake, pivots, targeting));
            Autonomous.register("1 Ball", new OneBall(drivetrain, shooter, hood, turret, indexer, intake, pivots, targeting));
            Autonomous.register("5 Ball Terminal", new FiveBallTerminal(drivetrain, indexer, intake, shooter, hood, turret, pivots, targeting));
		} catch (Exception e) {
			System.err.println("I did an oopsie.");
            e.printStackTrace();
		}

        if(TESTING) registerTestPaths();        
    }

    @Override
    protected void configureButtonBindings() {

        // DRIVER
        (new JoystickButton(driverRight, 1)).whileHeld(new StopAndShoot(shooter, hood, indexer, targeting, drivetrain, imu), false);//Shoot on close wall right stick left button
        // (new JoystickButton(driverLeft, 1)).whileHeld(new LaunchPadCannedShot(shooter, hood, indexer, targeting), false); // launch pad shot
        (new JoystickButton(driverRight, 4)).whileHeld(new ShootCargoTarmac(shooter, hood, indexer, turret, targeting), false); // shoot cargo from tarmac
        (new JoystickButton(driverRight, 2)).whileHeld(new ShootClose(shooter, hood, indexer, turret, targeting), false); // Shoot close no vision
		(new JoystickButton(driverLeft, 2)).whileHeld(new ParallelCommandGroup(
			new ZeroTurretHood(hood, turret), 
			new InstantCommand(turret::resetConstraint)));
        //(new JoystickButton(driverLeft, 3)).whileHeld(new ReverseFlywheel(shooter, indexer));
        // (new JoystickButton(driverRight, 3)).whileHeld(new ShootCargo(shooter, hood, indexer, targeting, drivetrain), false); use this
        (new JoystickButton(driverRight, 3)).whileHeld(new MovingShot(shooter, hood, indexer, targeting, drivetrain), false);
		(new JoystickButton(driverLeft, 4)).whenPressed(turret::resetConstraint);
       
        //TODO: Ask eric for button
       // (new JoystickButton(driverRight, 3)).whileHeld(new CloseWallCannedShot(shooter, hood, indexer, targeting, turret), false);//Shoot on close wall right stick left button
       // (new JoystickButton(driverRight, 4)).whileHeld(new FarWallCannedShot(shooter, hood, indexer, targeting, turret), false);//Shoot on Far wall right stick Right button
        // (new JoystickButton(driverRight, 4)).toggleWhenPressed(new AutoShoot(drivetrain, targeting, turret, indexer, shooter, hood));


        // COPILOT
        // (new Trigger(() -> copilot.getRightTriggerAxis() > 0.03)).whenActive(new RunIntake(intake, () -> copilot.getRightTriggerAxis())); //RT: run collector in
        (new Trigger(() -> copilot.getRightTriggerAxis() > 0.03)).whileActiveContinuous(new RunIntake(intake, () -> copilot.getRightTriggerAxis())); // RT: move intake out and run intake
        (new JoystickButton(copilot, JoystickConstants.BUTTON_B)).whileHeld(new RunIntake(intake, () -> -1)); //B: run collector out
        (new JoystickButton(copilot, JoystickConstants.RIGHT_BUMPER)).whileHeld(new MoveIntake(intake, () -> -Constants.DEFAULT_INTAKE_WINCH_POWER)); //RB: Retract intake
        (new JoystickButton(copilot, JoystickConstants.BUTTON_BACK)).whileHeld(new MoveIntake(intake, () -> Constants.DEFAULT_INTAKE_WINCH_POWER)); //SELECT/BACK: Deploy intake
        (new JoystickButton(copilot, JoystickConstants.LEFT_BUMPER)).whileHeld(new RunIndexer(indexer, shooter, () -> -Constants.DEFAULT_INDEXER_POWER)); //LB: run indexer down
        (new JoystickButton(copilot, JoystickConstants.BUTTON_Y)).whileHeld(new RunCommand(() -> shooter.setRPM(-1000), shooter));
        (new Trigger(() -> copilot.getLeftTriggerAxis() > 0.03)).whileActiveContinuous(new RunIndexer(indexer, shooter, () -> copilot.getLeftTriggerAxis()));//LT: run indexer up
        (new JoystickButton(copilot, JoystickConstants.BUTTON_START)).whenPressed(new InstantCommand(() -> indexer.resetBallCount())); //START: Reset ball count 

        (new POVButton(copilot, 0)).whenPressed(new InstantCommand(() -> targeting.adjustBiasDistance(Constants.DEFAULT_DISTANCE_BIAS_ADJUSTMENT)));
        (new POVButton(copilot, 180)).whenPressed(new InstantCommand(() -> targeting.adjustBiasDistance(-Constants.DEFAULT_DISTANCE_BIAS_ADJUSTMENT)));
        (new POVButton(copilot, 90)).whenPressed(new InstantCommand(() -> targeting.adjustBiasAngle(-Constants.DEFAULT_ANGLE_BIAS_ADJUSTMENT)));
        (new POVButton(copilot, 270)).whenPressed(new InstantCommand(() -> targeting.adjustBiasAngle(Constants.DEFAULT_ANGLE_BIAS_ADJUSTMENT)));
        (new JoystickButton(copilot, JoystickConstants.BUTTON_X)).whenPressed(targeting::zeroBias);

		// CLIMB
        (new JoystickButton(climb, JoystickConstants.BUTTON_START)).whenPressed(new GetReadyForClimb(hood, turret, shooter, intake, targeting));
        (new JoystickButton(climb, JoystickConstants.BUTTON_BACK)).whenPressed(
            new ParallelCommandGroup(
                new RunCommand(() -> turret.setDisableTurret(false), turret),
                new RunCommand(() -> hood.setDisableHood(false), hood),
                new SafeRetrackIntake(intake)
        ));
        (new POVButton(climb, 0)).whileHeld(new ArmsManual(arms, 1));
        (new POVButton(climb, 180)).whileHeld(new ArmsManual(arms, -1));

		// (new JoystickButton(climb, JoystickConstants.BUTTON_B)).whenPressed(new RunCommand(() -> intake.actuateIntake(-Constants.DEFAULT_INTAKE_POWER), intake));
        (new JoystickButton(climb, JoystickConstants.BUTTON_B)).whenHeld(new SequentialCommandGroup(
            new ArmsUpLimit(arms),
            new PivotToReach(pivots).withTimeout(1.0)
        ));

        (new JoystickButton(climb, JoystickConstants.BUTTON_Y)).whenHeld(new ArmsMid(arms, pivots));

		(new JoystickButton(climb, JoystickConstants.BUTTON_X)).whenHeld(new ConditionalCommand(
            new SequentialCommandGroup( //if we're already off of hooks, just pivot back and go straight up
                new PivotToReach(pivots),
                new ArmsUpLimit(arms)
            ),

            new SequentialCommandGroup(//if we're still on hookes, raise enough to get off and then go to reach
				new ArmsReleaseBar(arms),
				new PivotToReach(pivots),
                new ArmsUpLimit(arms)
			),
			() -> (((arms.getleftEncoder() + arms.getRightEncoder()) / 2) >= 20000) // check if arms are high enough to be off of hook already
		)
        );


        (new JoystickButton(climb, JoystickConstants.BUTTON_A)).whenHeld(
            new ConditionalCommand(
                new ArmsDownLimit(arms), // if starting from mid, just pull arms down
                new ParallelCommandGroup( //otherwise, run timed pivot and arm movement
                    new PivotToHold(pivots),
                    new SequentialCommandGroup (
                        new WaitCommand(1.0),
                        new ArmsDownLimit(arms)
                    )
                ),
                () -> (pivots.getLeftHoldSensor() && pivots.getRightHoldSensor())
            )
        );

        //Automated retract from back hooks code, should stop mashing into the vision mount, but untested
        // (new POVButton(climb, 90)).whenHeld(new ParallelCommandGroup(
        //     new StartEndCommand(() -> arms.setPower(-1, -1), () -> arms.setPower(0, 0), arms).withTimeout(0.4), //run the arms down for 0.4 seconds
        //     new SequentialCommandGroup( //wait 0.1 seconds, then start pivoting
        //         new WaitCommand(0.1),
        //         new PivotToHold(pivots)
        //     )
        // ));

        //"final" controls
        (new JoystickButton(climb, JoystickConstants.RIGHT_BUMPER)).whileHeld(pivots::pivotToHold);
        (new JoystickButton(climb, JoystickConstants.LEFT_BUMPER)).whileHeld(pivots::pivotToReach);
        (new Trigger(() -> climb.getLeftTriggerAxis() > 0.03)).whileActiveContinuous(new MoveBothPivots(pivots, () -> -climb.getLeftTriggerAxis()));
        (new Trigger(() -> climb.getRightTriggerAxis() > 0.03)).whileActiveContinuous(new MoveBothPivots(pivots, () -> climb.getRightTriggerAxis()));

        // Milford controls
        // (new Trigger(() -> climb.getLeftTriggerAxis() > 0.03)).whileActiveContinuous(new MoveLeftPivot(climber, () -> climb.getLeftTriggerAxis()));
        // (new Trigger(() -> climb.getRightTriggerAxis() > 0.03)).whileActiveContinuous(new MoveRightPivot(climber, () -> climb.getRightTriggerAxis()));
        // (new JoystickButton(climb, JoystickConstants.LEFT_BUMPER)).whileHeld(new MoveLeftPivot(climber, () -> -Constants.DEFAULT_PIVOT_POWER));
        // (new JoystickButton(climb, JoystickConstants.RIGHT_BUMPER)).whileHeld(new MoveRightPivot(climber, () -> -Constants.DEFAULT_PIVOT_POWER));

    }


    @Override
    protected void configureDefaultCommands() {        
		drivetrain.setDefaultCommand(new DifferentialTankDrive(drivetrain, () -> -driverLeft.getY() , () -> -driverRight.getY(), driverFilter));
        turret.setDefaultCommand(new AimTurret(turret, targeting));
		// targeting.setDefaultCommand(new AdjustBias(targeting, () -> copilot.getPOV(), () -> (new JoystickButton(copilot, JoystickConstants.BUTTON_X).get())));
        //indexer.setDefaultCommand(new EjectBall(indexer));
        shooter.setDefaultCommand(new AutoFlywheelHood(shooter, hood, targeting, indexer));
        arms.setDefaultCommand(new ManualClimb(arms, () -> -climb.getLeftY(), () -> -climb.getRightY()));
        intake.setDefaultCommand(new SafeRetrackIntake(intake));
	}

    @Override
    protected void configureFaultCodes() { }

    @Override
    protected void configureFaultMonitors() { }

    @Override
    protected void configureSystemTests() { 
        // SystemTest.register(new SystemCheck(intake, indexer, drivetrain, shooter, hood, targeting, turret, arms, () -> (new JoystickButton(copilot, JoystickConstants.BUTTON_A).get())));
    }

    @Override
    public LightningDrivetrain getDrivetrain() {
        return drivetrain;
    }

    @Override
    protected void initializeDashboardCommands() { 
		var tab = Shuffleboard.getTab("hood");

        var climbTab = Shuffleboard.getTab("climber");

        var sysCheckTab = Shuffleboard.getTab("system check"); //TODO: check if this is good

        var subsystemTab = Shuffleboard.getTab("subsystems");
        subsystemTab.add(indexer);
        subsystemTab.add(shooter);
        subsystemTab.add(arms);
        subsystemTab.add(intake);
        subsystemTab.add(hood);
		// var compTab = Shuffleboard.getTab("Competition");
		tab.add(new ResetHood(hood));
		// compTab.add(new MoveHoodManual(shooter, () -> copilot.getLeftY()));

        climbTab.add(new InstantCommand(arms::resetEncoders));
        
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
