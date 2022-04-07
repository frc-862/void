package com.lightningrobotics.voidrobot.commands.indexer;

import java.util.function.DoubleSupplier;

import com.lightningrobotics.voidrobot.subsystems.HubTargeting;
import com.lightningrobotics.voidrobot.subsystems.Indexer;
import com.lightningrobotics.voidrobot.subsystems.Shooter;
import com.lightningrobotics.voidrobot.subsystems.Indexer.BallColor;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class EjectBall extends CommandBase {

    // Creates the indexer subsystem
    private final Indexer indexer;
    // The power we want to supply to the indexer
    private DoubleSupplier power;

    

    public EjectBall(Indexer indexer) {
        this.indexer = indexer;

        addRequirements(indexer); 
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        boolean isEnenmyBall = !DriverStation.getAlliance().toString().equals(indexer.getUpperBallColor().toString()) && indexer.getUpperBallColor() != BallColor.nothing;
        
        if(isEnenmyBall && indexer.getBallCount() == 1){
				indexer.setPower(1);
			}
			else{
				indexer.setPower(0);
			}
    }

    @Override
    public void end(boolean interrupted) {
        indexer.stop();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
	
}