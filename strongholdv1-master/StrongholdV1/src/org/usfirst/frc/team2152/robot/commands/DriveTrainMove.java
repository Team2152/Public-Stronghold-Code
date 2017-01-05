package org.usfirst.frc.team2152.robot.commands;

import org.usfirst.frc.team2152.robot.Robot;
import org.usfirst.frc.team2152.robot.subsystems.DriveTrain;
import org.usfirst.frc.team2152.robot.utilities.PIDConstants;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class DriveTrainMove extends Command implements PIDOutput {
	
	PIDController pidHH; 
	double        errorFromHeading = 0.0;
	float         setPoint         = 0.0f;
	boolean       bUseHeadingHold  = false;
	double        motorSpeed       = DriveTrain.DEFAULT_SPEED;
	double        secondsToMoveFor = 0;
	Timer         timer;
	
    public DriveTrainMove(boolean useHeadingHold, double speed, double seconds) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.driveTrainSubsystem);
    	requires(Robot.navxSubsystem);
    	
    	bUseHeadingHold  = useHeadingHold;
    	motorSpeed       = speed;
    	secondsToMoveFor = seconds;
    	
    	//=== Setup the PID controller for Heading Hold should the driver want to use it
    	pidHH = new PIDController(PIDConstants.HH_Kd, 
    							  PIDConstants.HH_Ki, 
    							  PIDConstants.HH_Kd, 
    							  Robot.navxSubsystem.getAHRS(), 
    							  this);
    	pidHH.disable();
    	pidHH.setInputRange(PIDConstants.HH_IN_MIN, PIDConstants.HH_IN_MAX);
    	pidHH.setOutputRange(DriveTrain.min(speed), DriveTrain.max(speed));
    	pidHH.setAbsoluteTolerance(PIDConstants.HH_TOLERANCE);
    	pidHH.setContinuous(true);
    	
    	//setup timer
    	timer = new Timer();

    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	
    	if (bUseHeadingHold) {
			//pressed POV 0 == heading hold enable
			Robot.logger.console("Enabling heading hold");
			Robot.navxSubsystem.getAHRS().reset();
			setPoint = Robot.navxSubsystem.getAHRS().getYaw();
			Robot.logger.console("Current Heading: " + setPoint);
			errorFromHeading = 0;
			pidHH.setSetpoint(setPoint);
			pidHH.enable();
		} else {
			Robot.logger.console("Not using heading hold");
		}
    	timer.reset();
    	timer.start();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	
    	if (bUseHeadingHold) {
    		Robot.driveTrainSubsystem.tankDrive(motorSpeed-errorFromHeading, motorSpeed+errorFromHeading);
    	} else {
    		Robot.driveTrainSubsystem.tankDrive(motorSpeed, motorSpeed);
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	boolean value = false;
    	
    	if (secondsToMoveFor < timer.get()) {
    		value = true;
    		Robot.logger.console("DriverTrainMove timer popped: " + timer.get());
    	}
    		
        return value;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.driveTrainSubsystem.tankDrive(0, 0);
    	if (pidHH.isEnabled())
    		pidHH.disable();
    	timer.stop();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	if (pidHH.isEnabled())
    		pidHH.disable();
    	Robot.driveTrainSubsystem.tankDrive(0, 0);
    }

	@Override
	public void pidWrite(double output) {
		errorFromHeading = output;	
	}
}
