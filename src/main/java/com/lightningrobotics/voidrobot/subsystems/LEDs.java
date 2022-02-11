package com.lightningrobotics.voidrobot.subsystems;


import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.Random;

import java.util.function.BiConsumer;


public class LEDs extends SubsystemBase {
    private final int ledCount = 30;
    private AddressableLED led;
    private final AddressableLEDBuffer buffer;
    private Random rand = new Random();

    private ShuffleboardTab LEDTab = Shuffleboard.getTab("LED");

    private NetworkTableEntry Red;
	
	private NetworkTableEntry Green;

	private NetworkTableEntry Blue;
    int i = 0;

    public LEDs() {
        led = new AddressableLED(0);
        led.setLength(ledCount);

        buffer = new AddressableLEDBuffer(ledCount);

        buffer.setRGB(2, 255, 0, 255);

        led.setData(buffer);

        led.start();

        Red = LEDTab
            .add("red", 0)
            .getEntry();
		Green = LEDTab
			.add("green", 0)
			.getEntry();
		Blue = LEDTab
			.add("blue", 0)
			.getEntry();

    }

    public static final Color LightningOrange = new Color(1, .5, 0);
    public void withEachLed(BiConsumer<AddressableLEDBuffer, Integer> l) {
        for (int i = 0; i < ledCount; ++i) {
            l.accept(buffer, i);
        }
    }



    public void snake() {
        try{
        Thread.sleep(250);
        }
        catch (InterruptedException e){

        }
        if(i <= 1) {
            buffer.setRGB(i, 0, 0, 255);
        } else if(i <= 2){
            buffer.setRGB(i, 0, 0, 255);
            buffer.setRGB(i - 1, 255, 30, 0);
            buffer.setRGB(i-2, 0, 0, 0);
        }else {
            buffer.setRGB(i, 0, 0, 255);
            buffer.setRGB(i - 1, 255, 30, 0);
            buffer.setRGB(i - 2, 0, 0, 0);
        }
        if(Math.round(Timer.getFPGATimestamp()*50)%1==0)
        {i++;}
        if(i > 16) {i = 0;}
    }

    public void setAllRGB(int r, int g, int b) {
        withEachLed((buf, i) -> buf.setRGB(i, r, g, b));
    }


    public void goBlinkGreen() {
        withEachLed((b,i) -> b.setLED(i, Color.kGreen));
    }

    public void flashOrangeBlue() {
        if(Math.round(Timer.getFPGATimestamp())%2==0)
        {withEachLed((buffer, index) -> buffer.setRGB(index, 255, 30, 0));}
        else {
            withEachLed((buffer, index) -> buffer.setRGB(index, 0, 0, 255));
        }
    }

    public void rainbow() {
        withEachLed(((buffer,index) -> buffer.setHSV(index, index % 255, 255, 200)));
    }
    

    public void stopLEDs() {
        withEachLed((buffer, index) -> buffer.setRGB(index, 0, 0, 0));
    }

    @Override
    public void periodic() {
        withEachLed((buffer, index) -> buffer.setRGB(index, 0, 0, 255));
        led.setData(buffer);
    }
}