package org.usfirst.frc.team2152.robot.commands;

import org.usfirst.frc.team2152.robot.Robot;
import org.usfirst.frc.team2152.robot.RobotDirection;
import org.usfirst.frc.team2152.robot.subsystems.ShooterElevator;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ShooterElevatorMove extends Command {

	private Timer          watchDogTimer   = new Timer();
	private int            watchDogTimeOut = ShooterElevator.WATCHDOG_TIMEOUT;  
	private RobotDirection direction       = RobotDirection.DOWN;
	private double         secondsToMove   = 0;
	
    public ShooterElevatorMove(RobotDirection dir, double seconds) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.shooterElevatorSubsystem);
    	direction     = dir;
    	secondsToMove = seconds;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	//Make sure it is not going to move based on SetPoint/PIDSubsystem
    	
    	Robot.shooterElevatorSubsystem.disable();
    	watchDogTimer.start();
    	
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	if (direction == RobotDirection.DOWN)
    	   Robot.shooterElevatorSubsystem.setMotor(ShooterElevator.down(ShooterElevator.DEFAULT_SPEED_DOWN));
    	else if (direction == RobotDirection.UP)
    		Robot.shooterElevatorSubsystem.setMotor(ShooterElevator.up(ShooterElevator.DEFAULT_SPEED_UP));
    	else
    		Robot.shooterElevatorSubsystem.setMotor(0);
    	
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	boolean bFinished = false;
    	
    	//While within timer max and the elapse time is less than desired then continue
    	double elapseTime = watchDogTimer.get();
    	if (elapseTime < secondsToMove && elapseTime < watchDogTimeOut)
    		bFinished = false;
    	else
    		bFinished = true;
    	
        return bFinished;
    }

    // Called once after isFinished returns true
    protected void end() {
    	//We finished so stop the subsystem, reset encoder and stop the timer
    	Robot.shooterElevatorSubsystem.setMotor(0);
    	watchDogTimer.stop();
    	Robot.logger.console("SE completed move command: " + watchDogTimer.get());
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Robot.shooterElevatorSubsystem.setMotor(0);
    }
}
