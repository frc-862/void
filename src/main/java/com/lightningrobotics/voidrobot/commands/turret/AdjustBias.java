// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands.turret;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.cscore.VideoEvent;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AdjustBias extends CommandBase {

	private final Vision vision;
	private DoubleSupplier POV; 

  public AdjustBias(Vision vision, DoubleSupplier POV) {
	  this.POV = POV;
		this.vision = vision;
	}

  @Override
  public void initialize() {}

  @Override
  public void execute() {
	switch((int)POV.getAsDouble()) {
		case 0: 
			vision.adjustBias(0.1);
		break;
		case 180: 
			vision.adjustBias(-0.1);
		break;
	}

  }

  @Override
  public void end(boolean interrupted) {}

  @Override
  public boolean isFinished() {
    return false;
  }
}
