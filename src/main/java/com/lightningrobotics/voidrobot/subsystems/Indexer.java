package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.lightningrobotics.voidrobot.Constants;

import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Indexer extends SubsystemBase {
	
	// VictorSPX to run

	private final DigitalInput BEAM_BREAK_ENTER = new DigitalInput(Constants.ENTER_BEAM_BREAK);
	private final DigitalInput BEAM_BREAK_EXIT = new DigitalInput(Constants.EXIT_BEAM_BREAK);

	private boolean beamBreakEnterStatus = false;
	private boolean previousBeamBreakEnterStatus = false;
	private boolean beamBreakExitStatus = false;
	private boolean previousBeamBreakExitStatus = false;

	public int ballCount = 0;

	private VictorSPX Indexer;

	public Indexer() {
		Indexer = new VictorSPX(Constants.INDEXER_MOTOR_ID);
	}

	@Override
	public void periodic() {
		beamBreakEnterStatus = getBeamBreakEnterStatus();
		beamBreakExitStatus = getBeamBreakExitStatus();
		
		if (beamBreakEnterStatus != previousBeamBreakEnterStatus && beamBreakEnterStatus){
			ballCount++;
		}
		if (beamBreakExitStatus != previousBeamBreakExitStatus && beamBreakExitStatus){
			ballCount--;
		}
		
		previousBeamBreakEnterStatus = beamBreakEnterStatus;
		previousBeamBreakExitStatus = beamBreakExitStatus;
	
		SmartDashboard.putNumber("Ball Count", getBallCount());
	}

	public void setPower(double power) {
		Indexer.set(VictorSPXControlMode.PercentOutput, power);
	}

	public boolean getBeamBreakEnterStatus(){
		return !BEAM_BREAK_ENTER.get();
	}

	public boolean getBeamBreakExitStatus(){
		return !BEAM_BREAK_EXIT.get();
	}

	public void closeBeamBreaks(){
		BEAM_BREAK_ENTER.close();
		BEAM_BREAK_EXIT.close();
	}

	public int getBallCount(){
		return ballCount;
	}
	
	public void stop() {
		Indexer.set(VictorSPXControlMode.PercentOutput, 0);
	}


}
