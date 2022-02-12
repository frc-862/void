package com.lightningrobotics.voidrobot.commands;

import com.lightningrobotics.voidrobot.Constants;
import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class MoveShooter extends CommandBase {
    private Shooter shooter;
    private ShuffleboardTab shooterTab = Shuffleboard.getTab("shooter test");
    private NetworkTableEntry displayRPM;
    private NetworkTableEntry setRPM;
    private NetworkTableEntry setkP;
    private NetworkTableEntry setkD;
    private NetworkTableEntry shooterPower;

    //   private NetworkTableEntry shooterVelocityDashboard;
    //   private NetworkTableEntry shooterTarget;
    //   private NetworkTableEntry shooterPower;
    //   private NetworkTableEntry shooterkP;
    //   private NetworkTableEntry shooterkD;



    /** Creates a new moveShooter. */
    public MoveShooter(Shooter shooter) {
        this.shooter = shooter;
        addRequirements(shooter);

        displayRPM = shooterTab
            .add("RPM-From encoder", 0)
            .getEntry();
        setRPM = shooterTab
            .add("setRPM", 0)
            .getEntry();
        setkP = shooterTab
            .add("set kP", 0.0025)
            .getEntry();
        setkD = shooterTab
            .add("set kD", 0.0)
            .getEntry();
        shooterPower = shooterTab
            .add("shooter power output", shooter.getPowerSetpoint())
            .getEntry(); 

        // shooterVelocityDashboard = shooterTab
        // .add("RPM", 0)
        // .getEntry();
        // shooterTarget = shooterTab
        // .add("current target", 0)
        // .getEntry();
        // shooterPower = shooterTab
        // .add("commanded power", 0)
        // .getEntry();
        // shooterkP = shooterTab
        //   .add("kP", 0.0025)
        //   .getEntry();
        // shooterkD = shooterTab
        //   .add("kD", 0)
        //   .getEntry();

    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {}

    // Called every time the scheduler runs while the command is scheduled.j
    @Override
    public void execute() {
        // shooterVelocityDashboard.setDouble(shooter.getEncoderRPMs()); // Putting our encouder value converted to RPM on the dashboard
        displayRPM.setDouble(shooter.getEncoderRPMs());

        shooter.setRPM(setRPM.getDouble(0));
        shooter.setPIDGains(setkP.getDouble(Constants.SHOOTER_KP), setkD.getDouble(Constants.SHOOTER_KD));
        shooterPower.setDouble(shooter.getPowerSetpoint());

    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {

    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return false;
    }
}
