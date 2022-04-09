package com.lightningrobotics.voidrobot.commands.intake;

import java.util.function.DoubleSupplier;

import com.fasterxml.jackson.databind.introspect.ConcreteBeanPropertyBase;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Intake;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class RunIntake extends CommandBase {

    private final Intake intake;
    
    private DoubleSupplier power;

    public RunIntake(Intake intake, DoubleSupplier power) {
        this.intake = intake;
        this.power = power;

        addRequirements(intake);
    }    

    @Override
    public void execute() {
        intake.setPower(power.getAsDouble());

        if (!intake.getDeployedSensor()){
            intake.actuateIntake(Constants.DEFAULT_INTAKE_WINCH_POWER); 
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
