package com.lightningrobotics.voidrobot.commands.turret;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

import com.lightningrobotics.voidrobot.subsystems.Vision;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AdjustBias extends CommandBase {

	private final Vision vision;

	private IntSupplier POV; 
	private BooleanSupplier xButton;

  public AdjustBias(Vision vision, IntSupplier POV, BooleanSupplier xButton) {
	  	this.POV = POV;
		this.vision = vision;
		this.xButton = xButton;

		addRequirements(vision);
	}

  @Override
  public void initialize() {}

  @Override
  public void execute() {
	switch(POV.getAsInt()) {
		case 0: 
			vision.adjustBias(0.05);
		break;
		case 180: 
			vision.adjustBias(-0.05);
		break;
	}

	if(xButton.getAsBoolean()) {
		vision.zeroBias();
	}

  }

	@Override
	public void end(boolean interrupted) {}

	@Override
	public boolean isFinished() {
		return false;
	}
}
