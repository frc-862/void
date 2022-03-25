package com.lightningrobotics.voidrobot.commands.turret;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

import com.lightningrobotics.voidrobot.subsystems.HubTargeting;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AdjustBias extends CommandBase {

	private final HubTargeting targeting;

	private IntSupplier POV; 
	private BooleanSupplier xButton;

	public AdjustBias(HubTargeting targeting, IntSupplier POV, BooleanSupplier xButton) {
		this.POV = POV;
		this.targeting = targeting;
		this.xButton = xButton;

		addRequirements(targeting);
	}

	@Override
	public void execute() {
		switch(POV.getAsInt()) {
			case 0: 
				targeting.adjustBiasDistance(0.05);
			break;
			case 180: 
				targeting.adjustBiasDistance(-0.05);
			break;
			case 270: 
				targeting.adjustBiasAngle(-0.05);
			break;
			case 90: 
				targeting.adjustBiasAngle(0.05);
			break;
		}

		if(xButton.getAsBoolean()) {
			targeting.zeroBias();
		}

	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
}
