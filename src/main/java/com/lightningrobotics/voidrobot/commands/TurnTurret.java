// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands;

import java.util.Map;

import com.lightningrobotics.voidrobot.subsystems.LEDs;
import com.lightningrobotics.voidrobot.subsystems.Turret;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class TurnTurret extends CommandBase {

    private Turret turret;

    private double turretTarget = 0d;

    private double targetAngle;

    LEDs leds = new LEDs();

    public TurnTurret(Turret turret, double targetAngle) {
        this.turret = turret;
        this.targetAngle = targetAngle;
        addRequirements(turret);        
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {

        if (Math.abs(targetAngle) < 0.5) {
            targetAngle = 0;
            //deadban for controller; tune later
          }
          turretTarget += targetAngle;
          double error = 0;
      
          if(turretTarget > 180) {
            turretTarget -= 360;
          } 
          if(turretTarget < -180) {
            turretTarget += 360;
          }
      
          error = turretTarget - turret.turretRevToDeg();
      
          if(turretTarget >= 135) {
            error = 135 - turret.turretRevToDeg();
            leds.setAllRGB(255, 0, 0);
          } else if(turretTarget <= -135) {
            error = -135 - turret.turretRevToDeg(); // basically sets turretTarget to 135 without changing turretTarget so the turret can wrap around
            leds.setAllRGB(255, 0, 0);
          } else if(Math.abs(error) > 5) { //TODO: tune the compliance angle, add vision
            leds.setAllRGB(255, 255, 0);
          } else {
            leds.setAllRGB(0, 255, 0);
          }
      
          
          // if (Math.abs(error) < 1) {
          //   isDone = true;  //TODO: fix later
          // }
      
        
      
      
          turret.twistTurret(error, turretTarget); //input target here
          
        
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
        turret.stopTurret();
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return turret.isDone();
    }
}