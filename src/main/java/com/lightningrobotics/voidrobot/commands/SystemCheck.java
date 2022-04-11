// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.lightningrobotics.voidrobot.commands;

import java.util.function.BooleanSupplier;

import com.lightningrobotics.common.command.drivetrain.differential.DifferentialTankDrive;
import com.lightningrobotics.common.fault.LightningFaultCodes;
import com.lightningrobotics.common.geometry.kinematics.DrivetrainSpeed;
import com.lightningrobotics.common.testing.SystemTest;
import com.lightningrobotics.common.testing.SystemTestCommand;
import com.lightningrobotics.voidrobot.commands.climber.GetReadyForClimb;
import com.lightningrobotics.voidrobot.commands.climber.arms.ArmsEngageHooks;
import com.lightningrobotics.voidrobot.commands.climber.arms.ArmsLetGoOfHooks;
import com.lightningrobotics.voidrobot.commands.climber.arms.ArmsToReach;
import com.lightningrobotics.voidrobot.commands.climber.arms.StartMidClimb;
import com.lightningrobotics.voidrobot.commands.climber.pivot.PivotToHold;
import com.lightningrobotics.voidrobot.commands.climber.pivot.PivotToReach;
import com.lightningrobotics.voidrobot.commands.shooter.ShootCargo;
import com.lightningrobotics.voidrobot.commands.shooter.ShootClose;
import com.lightningrobotics.voidrobot.constants.Constants;
import com.lightningrobotics.voidrobot.subsystems.Climber;
import com.lightningrobotics.voidrobot.subsystems.Drivetrain;
import com.lightningrobotics.voidrobot.subsystems.Hood;
import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Intake;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Turret;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;

public class SystemCheck extends SystemTest {
  Intake intake;
  Indexer indexer;
  Drivetrain drivetrain;
  Shooter shooter;
  Hood hood;
  HubTargeting targeting;
  Turret turret;
  Climber climber;
  BooleanSupplier nextButton;

  boolean isDone = false;

  private static ShuffleboardTab sysCheckTab = Shuffleboard.getTab("system check");

  private static NetworkTableEntry currentMode = sysCheckTab.add("current mode", "").getEntry();

  public SystemCheck(Intake intake, Indexer indexer, Drivetrain drivetrain, Shooter shooter, Hood hood, HubTargeting targeting, Turret turret, Climber climber, BooleanSupplier nextButton) {
    super("system test", LightningFaultCodes.getFaultCode("system test"), Priority.DO_FIRST); //idk what to put here

    this.intake = intake;
    this.indexer = indexer;
    this.drivetrain = drivetrain;
    this.shooter = shooter;
    this.hood = hood;
    this.targeting = targeting;
    this.turret = turret;
    this.climber = climber;
    
    this.nextButton = nextButton;

    System.out.println("constructor entered");

    new SequentialCommandGroup(
        new PrintCommand("sequential entered"),

        new InstantCommand(() -> currentMode.setString("intake in")),
        new StartEndCommand(
            () -> intake.setPower(1), 
            () -> intake.setPower(0),
            intake
        ).until(nextButton),

        new WaitCommand(0.2),

        new InstantCommand(() -> currentMode.setString("intake out")),
        new StartEndCommand(
            () -> intake.setPower(-1), 
            () -> intake.setPower(0),
            intake
        ).until(nextButton),

        new WaitCommand(0.2),

        new InstantCommand(() -> currentMode.setString("indexer up")),
        new StartEndCommand(
            () -> indexer.setPower(1), 
            () -> indexer.setPower(0),
            indexer
        ).until(nextButton),

        new WaitCommand(0.2),

        new InstantCommand(() -> currentMode.setString("indexer down")),
        new StartEndCommand(
            () -> indexer.setPower(-1), 
            () -> indexer.setPower(0),
            indexer
        ).until(nextButton),

        new WaitCommand(0.2),

        // TODO: see if no one tries to kill me because I have no idea how the drivetrain works

        new InstantCommand(() -> currentMode.setString("drive forward")),
        new StartEndCommand(
          () -> drivetrain.tankDrive(0.5, 0.5),
          () -> drivetrain.tankDrive(0, 0),
          drivetrain
        ).until(nextButton),

        new WaitCommand(0.2),

        new InstantCommand(() -> currentMode.setString("drive backward")),
        new StartEndCommand(
          () -> drivetrain.tankDrive(-0.5, -0.5),
          () -> drivetrain.tankDrive(0, 0),
          drivetrain
        ).until(nextButton),

        new InstantCommand(() -> currentMode.setString("shoot with vision")),
        new ShootCargo(shooter, hood, indexer, targeting, drivetrain).until(nextButton),

        new WaitCommand(0.2),

        new InstantCommand(() -> currentMode.setString("shoot close")),
        new ShootClose(shooter, hood, indexer, turret, targeting).until(nextButton),

        new WaitCommand(0.2),

        new InstantCommand(() -> currentMode.setString("zero turret (runs continuously)")),
        new GetReadyForClimb(hood, turret, shooter, targeting).until(nextButton),

        new WaitCommand(0.2),

        // new InstantCommand(() -> currentMode.setString("pivot to hold")),
        // new RunCommand(climber::pivotToHold).until(nextButton),

        // new WaitCommand(0.2),

        // new InstantCommand(() -> currentMode.setString("pivot to reach")),
        // new RunCommand(climber::pivotToReach).until(nextButton),

        // new WaitCommand(0.2),

        // new InstantCommand(() -> currentMode.setString("arms to reach")),
        // new RunCommand(() -> climber.setArmsTarget(Constants.REACH_HEIGHT), climber).until(nextButton), //TODO: make it go to max constraint

        // new WaitCommand(0.2),

        // new InstantCommand(() -> currentMode.setString("arms to hold")),
        // new RunCommand(() -> climber.setArmsTarget(10000), climber).until(nextButton), //not 0 because I dont want to floor the arms

        // new WaitCommand(0.2),

        // new InstantCommand(() -> currentMode.setString("climb to mid bar")),
        // new StartMidClimb(climber),

        // new WaitCommand(0.2),

        // new InstantCommand(() -> currentMode.setString("climb to hold")),
        // new ParallelCommandGroup(
        //   new PivotToHold(climber),
        //   new SequentialCommandGroup (
        //     new WaitCommand(1.0),
        //     new ArmsEngageHooks(climber)
        //   )
        // ).until(nextButton),

        // new WaitCommand(0.2),

        // new InstantCommand(() -> currentMode.setString("climb to reach")),
        // new SequentialCommandGroup(
        // 	new ArmsLetGoOfHooks(climber),
        // 	new PivotToReach(climber),
        // 	new ArmsToReach(climber)
        // ).until(nextButton),

        // new WaitCommand(0.2),

        new InstantCommand(() -> currentMode.setString("done!")),

        new InstantCommand(() -> isDone = true)
      ).schedule();
  }

  @Override
  public void end(boolean interrupted) {
    super.end(interrupted);
    // new SequentialCommandGroup(
    //   new InstantCommand(() -> climber.setArmsTarget(10000), climber),
    //   new InstantCommand(climber::pivotToReach)
    // ).schedule();

    new SequentialCommandGroup(
      new InstantCommand(() -> turret.setDisableTurret(false)),
      new InstantCommand(() -> hood.setDisableHood(false))
    ).schedule();
  }

  @Override
  public void initialize() {
  }
  @Override
  public void execute() {
  }

  @Override
  public boolean didPass() {
      return isDone;
  }
  

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
