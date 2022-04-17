package com.lightningrobotics.voidrobot;

import com.lightningrobotics.common.LightningRobot;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.CvSink;
import edu.wpi.first.cscore.CvSource;
import edu.wpi.first.cscore.MjpegServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoMode;
import edu.wpi.first.cscore.VideoMode.PixelFormat;
import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends LightningRobot {

	UsbCamera camera;
	MjpegServer mjpegServer1;
	

    public DataLog dataLog = new DataLog();
    private int commandInitEntry = dataLog.start("commandInit", "String");

    public Robot() {
        super(new RobotContainer());

    }

    @Override
    public void robotInit() {

		// Standard Initialization
        super.robotInit();

		// Log Commands When They Are Scheduled
        CommandScheduler.getInstance().onCommandInitialize((cmd) -> {
            dataLog.appendString(commandInitEntry, cmd.getName(), (long) (Timer.getFPGATimestamp() * 1000));
        });

			int width = 160;
			int height = 120;
			//width = 640;
			//height = 480;
			int fps = 30;

			camera = new UsbCamera("USB Camera 0", 0);
			// camera.setResolution(width, height);
			// camera.setFPS(fps);
			camera.setVideoMode(PixelFormat.kMJPEG, width, height, fps);
			mjpegServer1 = new MjpegServer("serve_USB Camera 0", 1181);
			mjpegServer1.setFPS(fps);
			mjpegServer1.setResolution(width, height);
			mjpegServer1.setSource(camera);
			

			// camera = CameraServer.startAutomaticCapture();
    }

}
