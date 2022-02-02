// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands;

import java.util.Map;

import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class TurnTurret extends CommandBase {

    private Turret turret;

    private ShuffleboardTab turretTab = Shuffleboard.getTab("turret");

    private NetworkTableEntry targetEntry;

    private NetworkTableEntry kPEntry;

    private NetworkTableEntry currentDegrees;

    private final double DEFAULT_TARGET = 180d;

    private double turretkP = 0.035;

    public TurnTurret(Turret turret) {
        this.turret = turret;
        addRequirements(turret);

        targetEntry = turretTab
                .add("target", 180)
                .withWidget(BuiltInWidgets.kNumberSlider)
                .withProperties(Map.of("min", -360, "max", 360)) // specify widget properties here
                .getEntry();

        kPEntry = turretTab
            .add("kP", turretkP)
            .getEntry();

        currentDegrees = turretTab
            .add("current turret degrees", turret.turretRevToDeg())
            .getEntry();
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
        turret.setTarget(180);
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        turret.twistTurret(targetEntry.getDouble(DEFAULT_TARGET), kPEntry.getDouble(turretkP));
        currentDegrees.setDouble(turret.turretRevToDeg());
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
        turret.stopTurret();
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return turret.isDone();
    }
}