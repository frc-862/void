package com.lightningrobotics.voidrobot.simulation;

import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.simulation.BatterySim;
import edu.wpi.first.wpilibj.simulation.DifferentialDrivetrainSim;
import edu.wpi.first.wpilibj.simulation.LinearSystemSim;
import edu.wpi.first.wpilibj.simulation.RoboRioSim;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Battery {
    public static void UseBattery(LinearSystemSim deviceSim){
        RoboRioSim.setVInVoltage(
            BatterySim.calculateDefaultBatteryLoadedVoltage(deviceSim.getCurrentDrawAmps())
            );
    }

    public static void UseBattery(DifferentialDrivetrainSim driveSim){
        RoboRioSim.setVInVoltage(
            BatterySim.calculateDefaultBatteryLoadedVoltage(driveSim.getCurrentDrawAmps())
            );
    }
}
