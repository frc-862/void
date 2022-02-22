package com.lightningrobotics.voidrobot.commands.auto;

import com.lightningrobotics.common.auto.Path;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Intake;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class FourBallTerminal extends CommandBase {

    
    private final Indexer indexer;
    private final Intake intake;
    private final Turret turret;
    private final Vision vision;
    private final Drivetrain drivetrain;

    Path start4BallPath = new Path("Start4Ball.path", false);
    Path middle4BallPath = new Path("Middle4Ball.path", false);
    Path end4BallPath = new Path("End4Ball.path", false);   

    public FourBallTerminal(Indexer indexer, Intake intake, Turret turret, Vision vision, Drivetrain drivetrain) {
        this.indexer = indexer;
        this.intake = intake;
        this.turret = turret;
        this.vision = vision;
        this.drivetrain = drivetrain;

        addRequirements(indexer, intake, turret, vision, drivetrain);
        
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        try {
            new SequentialCommandGroup(start4BallPath.getCommand(drivetrain), middle4BallPath.getCommand(drivetrain), end4BallPath.getCommand(drivetrain));
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
        }

    }

    @Override
    public void end(boolean interrupted) {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
