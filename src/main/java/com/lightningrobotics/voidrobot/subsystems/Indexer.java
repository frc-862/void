package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.lightningrobotics.voidrobot.Constants;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Indexer extends SubsystemBase {
	
	// VictorSPX to run

	private VictorSPX Indexer;

	public Indexer() {
		Indexer = new VictorSPX(Constants.INDEXER_MOTOR_ID);
	}

	@Override
	public void periodic() {}

	public void setPower(double power) {
		Indexer.set(VictorSPXControlMode.PercentOutput, power);
	}

	public void stop() {
		Indexer.set(VictorSPXControlMode.PercentOutput, 0);
	}


}
