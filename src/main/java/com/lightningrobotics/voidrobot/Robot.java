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
import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends LightningRobot {

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

		// Start Driver Camera
		new Thread(() -> {

			int width = 160;
			int height = 120;
			//width = 640;
			//height = 480;
			int fps = 15;

			UsbCamera camera = CameraServer.startAutomaticCapture();
			camera.setResolution(width, height);
			camera.setFPS(fps);

			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// CvSink sink = CameraServer.getVideo();
			// CvSource output = CameraServer.putVideo("Driver Camera", width, height);

			// Mat inputImage = new Mat();
			// Mat rotatedImage = new Mat();

			// while(!Thread.interrupted()) {
			// 	sink.grabFrame(inputImage);
			// 	Core.rotate(inputImage, rotatedImage, Core.ROTATE_180);
			// 	output.putFrame(rotatedImage);
			// }

		}).start();

    }

}
