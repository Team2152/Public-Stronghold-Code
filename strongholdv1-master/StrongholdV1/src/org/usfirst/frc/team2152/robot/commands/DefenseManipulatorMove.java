package org.usfirst.frc.team2152.robot.commands;

import org.usfirst.frc.team2152.robot.Robot;
import org.usfirst.frc.team2152.robot.subsystems.DefenseManipulator;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class DefenseManipulatorMove extends Command {

	private Timer   watchDogTimer;
	private int     setPoint = 0;
	private double  acceptableTolerance = 2;
	private boolean bReset = true;

	public DefenseManipulatorMove(int setpoint, double tolerance, boolean reset) {
		// Use requires() here to declare subsystem dependencies
		// eg. requires(chassis);
		requires(Robot.defenseManipulatorSubsystem);
		setPoint = setpoint;
		acceptableTolerance = tolerance;
		watchDogTimer = new Timer();
		watchDogTimer.reset();
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		if (bReset) {
			Robot.logger.console("DM Initialize Reset Before: " + Robot.defenseManipulatorSubsystem.getEncoderValue());
			Robot.defenseManipulatorSubsystem.resetDM(8);
			Robot.logger.console("DM Initialize Reset After: " + Robot.defenseManipulatorSubsystem.getEncoderValue());
		}
        
		watchDogTimer.start();
		Robot.defenseManipulatorSubsystem.setAbsoluteTolerance(acceptableTolerance);
		Robot.defenseManipulatorSubsystem.setSetpoint(setPoint);
		Robot.defenseManipulatorSubsystem.enable();
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		//Nothing to run here.  By enabling it in initialize() the subsystem will start moving
		//We check in isFinished if we should keep it moving or not.
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
        boolean bFinished = false;
    	
    	//While within timer max and the elapse time is less than desired then continue
    	double elapseTime = watchDogTimer.get();
    	if (Robot.defenseManipulatorSubsystem.onTarget() != true && elapseTime < DefenseManipulator.WATCHDOG_TIMEOUT)
    		bFinished = false;
    	else
    		bFinished = true;
    	
        return bFinished;
	}

	// Called once after isFinished returns true
	protected void end() {
		Robot.logger.console("DM End Before " + Robot.defenseManipulatorSubsystem.getEncoderValue());
		Robot.defenseManipulatorSubsystem.disable();
		Robot.logger.console("DM End After " + Robot.defenseManipulatorSubsystem.getEncoderValue());
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
		Robot.defenseManipulatorSubsystem.disable();
		Robot.logger.console("DM Interrupted " + Robot.defenseManipulatorSubsystem.getEncoderValue());
	}
}
