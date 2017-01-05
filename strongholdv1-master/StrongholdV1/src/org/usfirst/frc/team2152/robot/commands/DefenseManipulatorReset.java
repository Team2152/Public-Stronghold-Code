package org.usfirst.frc.team2152.robot.commands;

import org.usfirst.frc.team2152.robot.Robot;
import org.usfirst.frc.team2152.robot.subsystems.DefenseManipulator;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class DefenseManipulatorReset extends Command {

	private Timer  watchDogTimer   = new Timer();
	private int    watchDogTimeOut = DefenseManipulator.WATCHDOG_TIMEOUT;  
	private double dmSpeed         = DefenseManipulator.DEFAULT_SPEED;
	
    public DefenseManipulatorReset() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.defenseManipulatorSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	//Make sure it is not going to move based on SetPoint/PIDSubsystem
    	Robot.defenseManipulatorSubsystem.disable();
    	watchDogTimer.start();
    	
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.defenseManipulatorSubsystem.setMotor(dmSpeed);
    	
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	boolean bFinished = false;
    	
    	//While within timer max and the limit switch is reporting true then continue
    	if (watchDogTimer.get() < watchDogTimeOut  && 
    		Robot.defenseManipulatorSubsystem.getLimitSwitch() == DefenseManipulator.LIMIT_NOT_HIT)
    		bFinished = false;
    	else
    		bFinished = true;
    	
        return bFinished;
    }

    // Called once after isFinished returns true
    protected void end() {
    	//We finished so stop the subsystem, reset encoder and stop the timer
    	Robot.defenseManipulatorSubsystem.setMotor(0);
    	Robot.defenseManipulatorSubsystem.resetEncoder();
    	watchDogTimer.stop();
    	Robot.logger.console("DM completed reset command: " + watchDogTimer.get());
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Robot.defenseManipulatorSubsystem.setMotor(0);
    }
}
