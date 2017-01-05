package org.usfirst.frc.team2152.robot.commands;

import org.usfirst.frc.team2152.robot.Robot;
import org.usfirst.frc.team2152.robot.subsystems.ShooterElevator;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ShooterElevatorReset extends Command {

	private Timer  watchDogTimer   = new Timer();
	private int    watchDogTimeOut = ShooterElevator.WATCHDOG_TIMEOUT;  
	private double dmSpeed         = ShooterElevator.DEFAULT_SPEED_DOWN;
	
    public ShooterElevatorReset() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.shooterElevatorSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	//Make sure it is not going to move based on SetPoint/PIDSubsystem
    	Robot.shooterElevatorSubsystem.disable();
    	watchDogTimer.start();
    	
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.shooterElevatorSubsystem.setMotor(dmSpeed);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	boolean bFinished = false;
    	
    	//While within timer max and the limit switch is reporting true then continue
    	if (watchDogTimer.get() < watchDogTimeOut  && 
    		Robot.shooterElevatorSubsystem.getLimitSwitchLower() == ShooterElevator.LIMIT_NOT_HIT)
    		bFinished = false;
    	else
    		bFinished = true;
    	
        return bFinished;
    }

    // Called once after isFinished returns true
    protected void end() {
    	//We finished so stop the subsystem, reset encoder and stop the timer
    	Robot.shooterElevatorSubsystem.setMotor(0);
    	Robot.shooterElevatorSubsystem.resetCounter();
    	watchDogTimer.stop();
    	Robot.logger.console("SE completed reset command: " + watchDogTimer.get());
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Robot.shooterElevatorSubsystem.setMotor(0);
    }
}
