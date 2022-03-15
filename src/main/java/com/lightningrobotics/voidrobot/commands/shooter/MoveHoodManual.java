package com.lightningrobotics.voidrobot.commands.shooter;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.Shooter;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class MoveHoodManual extends CommandBase {
  DoubleSupplier power;
  Hood hood;
  private static ShuffleboardTab driverView = Shuffleboard.getTab("Competition");
	private static NetworkTableEntry manualHoodEntry = driverView.add("Manual Hood Control", false).getEntry();

  public MoveHoodManual(Hood hood, DoubleSupplier power) {
    this.hood = hood;
    //this.power = power;
	this.power = power;

    addRequirements(hood);
  }

  @Override
  public void initialize() {}

  @Override
  public void execute() {   
		if (manualHoodEntry.getBoolean(false)) {
			hood.setPower(power.getAsDouble() * Constants.HOOD_MANUAL_SPEED_MULTIPLIER); 
		} 
  }

  @Override
  public void end(boolean interrupted) {
    hood.stop();
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}

