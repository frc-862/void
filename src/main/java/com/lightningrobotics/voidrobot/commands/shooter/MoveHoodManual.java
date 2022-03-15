package com.lightningrobotics.voidrobot.commands.shooter;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class MoveHoodManual extends CommandBase {
  Shooter shooter;
  DoubleSupplier power;
  private static ShuffleboardTab driverView = Shuffleboard.getTab("Competition");
	private static NetworkTableEntry manualHoodEntry = driverView.add("Manual Hood Control", false).getEntry();

  public MoveHoodManual(Shooter shooter, DoubleSupplier power) {
    this.shooter = shooter;
    //this.power = power;
	this.power = power;
  }

  @Override
  public void initialize() {}

  @Override
  public void execute() {

		shooter.setManualHoodOverride(true);
   
		if (manualHoodEntry.getBoolean(false)) {
			shooter.setHoodPower(power.getAsDouble() * Constants.HOOD_MANUAL_SPEED_MULTIPLIER); 
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
}

