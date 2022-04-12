package com.lightningrobotics.voidrobot.commands.auto.commands;

import com.lightningrobotics.voidrobot.subsystems.Intake;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutonIntake extends CommandBase {

    private final Intake intake;

    public AutonIntake(Intake intake) {
        this.intake = intake;

        addRequirements(intake);
    }    

    @Override
    public void execute() {
        intake.setPower(0.75);

        if (!intake.getDeployedSensor()){
            intake.actuateIntake(0.75d); 
        } else {
            intake.actuateIntake(0d);
        }
    }

    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
        intake.stop();
        intake.stopDeploy();
    }

    @Override
    public boolean isFinished(){
        return false;
    }
}
