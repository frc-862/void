package com.lightningrobotics.voidrobot.commands.test;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;

import com.lightningrobotics.common.logging.DataLogger;
import com.lightningrobotics.voidrobot.constants.RobotMap;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Shooter;

public class VoltageTestContinuous extends CommandBase {

    Shooter shooter;

    private ShuffleboardTab voltageTab = Shuffleboard.getTab("voltage test");

    double voltageDrawn = 0;
    double currentDrawn = 0;
    double powerDrawn = 0;
    double energyDrawn = 0;
    double totalEnergyDrawn = 0;

    private NetworkTableEntry voltage;
    private NetworkTableEntry current;
    private NetworkTableEntry power;
    private NetworkTableEntry energy;

    PowerDistribution powerDistributor = new PowerDistribution(10, ModuleType.kCTRE);

    public VoltageTestContinuous(Shooter shooter) {
        this.shooter = shooter;

        addRequirements(shooter);

        voltage = voltageTab
            .add("voltage", voltageDrawn)
            .getEntry();
        current = voltageTab
            .add("current", currentDrawn)
            .getEntry();
        power = voltageTab
            .add("power", powerDrawn)
            .getEntry();
        energy = voltageTab
            .add("energy", energyDrawn)
            .getEntry();

        DataLogger.addDataElement("voltage drawn", () -> voltageDrawn);
        DataLogger.addDataElement("current drawn", () -> currentDrawn);
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {}

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {

        shooter.setPower(0.5);

        voltageDrawn = powerDistributor.getVoltage(); 
        currentDrawn = powerDistributor.getCurrent(RobotMap.FLYWHEEL_MOTOR_ID);
        powerDrawn = powerDistributor.getTotalPower();
        energyDrawn = powerDistributor.getTotalEnergy();

        voltage.setDouble(voltageDrawn);
        current.setDouble(currentDrawn);
        power.setDouble(powerDrawn);
        energy.setDouble(energyDrawn);

        totalEnergyDrawn += energyDrawn;

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
