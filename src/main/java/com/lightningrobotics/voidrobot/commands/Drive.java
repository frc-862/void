package com.lightningrobotics.voidrobot.commands;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Drivetrain;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class Drive extends CommandBase {
	// TODO use lightning base code when ready

    /** Creates a new Drive. */
    Drivetrain drivetrain;
    private final DoubleSupplier left;
    private final DoubleSupplier right;


    public Drive(Drivetrain drivetrain, DoubleSupplier left, DoubleSupplier right) {
        addRequirements(drivetrain);
        this.drivetrain = drivetrain;
        this.left = left;
        this.right = right;
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {}

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        drivetrain.setPower(left.getAsDouble(), right.getAsDouble());
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
        drivetrain.stop();
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
    return false;
    }
}
