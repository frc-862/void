package com.lightningrobotics.voidrobot;

import com.lightningrobotics.common.LightningRobot;

import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends LightningRobot {

    public DataLog dataLog = new DataLog();
    private int commandInitEntry = dataLog.start("commandInit", "String");

    public Robot() {
        super(new RobotContainer());

    }

    @Override
    public void robotInit() {
        super.robotInit();
        CommandScheduler.getInstance().onCommandInitialize((action) -> {
            dataLog.appendString(commandInitEntry, action.getName(), (long) (Timer.getFPGATimestamp() * 1000));
        });
    }

}
