package com.lightningrobotics.voidrobot.commands.auto.commands;

import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Intake;

import edu.wpi.first.hal.simulation.ConstBufferCallback;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutonIntake extends CommandBase {

    private final Intake intake;

    public AutonIntake(Intake intake) {
        this.intake = intake;

        addRequirements(intake);
    }    

    @Override
    public void execute() {

        intake.actuateIntake(0.75d);
        intake.setPower(0.75d);

        // if (!intake.getDeployedSensor()){
        //     intake.actuateIntake(0.75d); 
        // } else {
        //     intake.actuateIntake(0d);
        //     intake.setPower(0.75);
        // }
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
