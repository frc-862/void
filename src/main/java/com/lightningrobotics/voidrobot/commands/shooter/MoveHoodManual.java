// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.shooter;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class MoveHoodManual extends CommandBase {
  Shooter shooter;
  DoubleSupplier power;
  DoubleSupplier POV;
  private static ShuffleboardTab trimTab = Shuffleboard.getTab("Biases");
	private static NetworkTableEntry manualHoodEntry = trimTab.add("Manual Hood Control", false).getEntry();

  public MoveHoodManual(Shooter shooter, DoubleSupplier POV) {
    this.shooter = shooter;
    //this.power = power;
    this.POV = POV;

    addRequirements(shooter);
  }

  @Override
  public void initialize() {}

  @Override
  public void execute() {
    if (manualHoodEntry.getBoolean(false)) {
      shooter.setHoodPower(POVToStandard(POV) / 20); 
    }
  }

  @Override
  public void end(boolean interrupted) {
    shooter.setHoodPower(0);
  }

  @Override
  public boolean isFinished() {
    return false;
  }

  public double POVToStandard(DoubleSupplier POV){
    if (POV.getAsDouble() == 0){
        return 1;
    } else if (POV.getAsDouble() == 180){
        return -1;
    } else {
        return 0;
    }
}
}
