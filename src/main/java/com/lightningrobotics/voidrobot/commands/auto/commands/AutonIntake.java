package com.lightningrobotics.voidrobot.commands.auto.commands;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Intake;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutonIntake extends CommandBase {
	
    private Intake intake;
    // private Indexer indexer;
    private double ballsWanted;

    // public AutonIntake(Intake intake, double ballsWanted) {
	public AutonIntake(Intake intake) {

		this.intake = intake;
        // this.indexer = indexer;
        // this.ballsWanted = ballsWanted;

        addRequirements(intake);
    }    

    @Override
    public void execute() {/*  */
        intake.setPower(Constants.DEFAULT_INDEXER_POWER);
        // indexer.setPower(Constants.DEFAULT_INTAKE_POWER);
    }

    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
        intake.stop();
    }

    @Override
    public boolean isFinished(){
        // return indexer.getBallCount() == ballsWanted;
		return false;
	}
}
