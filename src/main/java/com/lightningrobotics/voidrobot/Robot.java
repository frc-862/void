package com.lightningrobotics.voidrobot;

import com.lightningrobotics.common.LightningRobot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.MjpegServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends LightningRobot {

    public DataLog dataLog = new DataLog();
    private int commandInitEntry = dataLog.start("commandInit", "String");
	private MjpegServer mjpegServer;


    public Robot() {
        super(new RobotContainer());

    }

    @Override
    public void robotInit() {
        super.robotInit();
        CommandScheduler.getInstance().onCommandInitialize((action) -> {
            dataLog.appendString(commandInitEntry, action.getName(), (long) (Timer.getFPGATimestamp() * 1000));
        });

		// UsbCamera usbCamera = new UsbCamera("Driver Cam", 0);
		// usbCamera.setResolution(160, 120);
		// mjpegServer = new MjpegServer("server_USB Camera 0", 1181);
		// mjpegServer.setSource(usbCamera);

		//CameraServer.startAutomaticCapture();
    }

}
