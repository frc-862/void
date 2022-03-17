package com.lightningrobotics.voidrobot.commands.auto.commands;

import com.lightningrobotics.voidrobot.subsystems.Intake;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutonIntake extends CommandBase {
	
    private Intake intake;

	public AutonIntake(Intake intake) {
		this.intake = intake;

        addRequirements(intake);
    }    

    @Override
    public void execute() {
        intake.setPower(-0.75);
    }

    @Override
    public void end(boolean interrupted) {
        intake.stop();
    }

    @Override
    public boolean isFinished(){
		return false;
	}
}
