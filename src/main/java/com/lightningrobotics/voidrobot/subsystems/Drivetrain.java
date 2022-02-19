package com.lightningrobotics.voidrobot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Drivetrain extends SubsystemBase {
	// TODO use lightning base code when ready

    /** Creates a new Drivetrain. */
    final TalonFX left1 = new TalonFX(5); // TODO these should be static or initialized in constructor
    final TalonFX left2 = new TalonFX(4);
    final TalonFX left3 = new TalonFX(6);

    final TalonFX right1 = new TalonFX(3);
    final TalonFX right2 = new TalonFX(1);
    final TalonFX right3 = new TalonFX(2);

    public Drivetrain() {
        left2.follow(left1);
        left3.follow(left1);

        right2.follow(right1);
        right3.follow(right1);

        left1.setInverted(true);
        right1.setInverted(false);

        left2.setInverted(false);
        left3.setInverted(false);

        right2.setInverted(true);
        right3.setInverted(true);

    }

    public void setPower(double left, double right){
        left1.set(ControlMode.PercentOutput, left);
        right1.set(ControlMode.PercentOutput, right);
    }

    public void stop(){
        setPower(0, 0);
    }

    public boolean isMoving() {
        return left1.getSelectedSensorVelocity() < 0.05 && right1.getSelectedSensorVelocity() < 0.05;
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
    }
}
