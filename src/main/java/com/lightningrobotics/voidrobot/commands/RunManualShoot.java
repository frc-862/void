package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class RunManualShoot extends CommandBase {
    Shooter shooter;
    Indexer indexer;
    Drivetrain drivetrain;

    ShuffleboardTab shooterTab = Shuffleboard.getTab("shooterTab");
    NetworkTableEntry targetRPMs;

    private double shootingStartTime; 
    private double shootTime = 0.5d; // Time after we hit the top beam break before we end command

    private boolean isShooting = false;

    /** Creates a new ManualShooting. */
    public RunManualShoot(Shooter shooter, Indexer indexer, Drivetrain drivetrain) {
        this.shooter = shooter;
        this.indexer = indexer;
        this.drivetrain = drivetrain;

        addRequirements(shooter, indexer, drivetrain);
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
        targetRPMs = shooterTab
            .add("target RPMs", 0)
            .getEntry();

        indexer.setPower(0.5);
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        shooter.setRPM(targetRPMs.getDouble(0));
        // shooter.setRPM(Constants.SHOOTER_CLOSE_RPMS);

        if (indexer.getBeamBreakExitStatus()) { 
            indexer.stop(); // if ball is ready to shoot, stop the indexer
        }

        if (!drivetrain.isMoving() && !isShooting) {
            isShooting = true;
            indexer.setPower(0.5); // if the robot has stopped moving, shoot the ball
            shootingStartTime = Timer.getFPGATimestamp(); // initalize shooter start time
        }
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
        indexer.stop();
        shooter.stop();
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return Timer.getFPGATimestamp() - shootingStartTime > shootTime; // stop shooting when timer is done
    }
}
