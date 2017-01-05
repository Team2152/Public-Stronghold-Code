package org.usfirst.frc.team2152.robot.commands;

import org.usfirst.frc.team2152.robot.OI;
import org.usfirst.frc.team2152.robot.Robot;
import org.usfirst.frc.team2152.robot.utilities.PIDConstants;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class DriveTrainJoystick extends Command implements PIDOutput {

	PIDController pidHH; 
	double errorFromHeading = 0.0;
	double previousPOVValue = -1.0;
	float  setPoint         = 0.0f;
	
    public DriveTrainJoystick() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.driveTrainSubsystem);
    	requires(Robot.navxSubsystem);
    	
    	//=== Setup the PID controller for Heading Hold should the driver want to use it
    	pidHH = new PIDController(PIDConstants.HH_Kd, 
    							  PIDConstants.HH_Ki, 
    							  PIDConstants.HH_Kd, 
    							  Robot.navxSubsystem.getAHRS(), 
    							  this);
    	pidHH.disable();
    	pidHH.setInputRange(PIDConstants.HH_IN_MIN, PIDConstants.HH_IN_MAX);
    	pidHH.setOutputRange(PIDConstants.HH_OUT_MIN, PIDConstants.HH_OUT_MAX);
    	pidHH.setAbsoluteTolerance(PIDConstants.HH_TOLERANCE);
    	pidHH.setContinuous(true);
    }

    // Called just before this Command runs the first time
    protected void initialize() {

    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {

    	boolean bFirstTime = false;
    	int pov = Robot.oi.driverJoystick.getPOV();
    	//Robot.logger.console("JS POV = " + pov);
    	
    	if (pov == OI.POV_0 && pov != previousPOVValue) {
			//pressed POV 0 == heading hold enable
			Robot.logger.console("Enabling heading hold");
			//Robot.navxSubsystem.getAHRS().reset();
			setPoint = (float) Robot.navxSubsystem.getAHRS().getYaw();
			Robot.logger.console("Current Heading: " + setPoint);
			pidHH.setSetpoint(setPoint);
			pidHH.enable();
			bFirstTime = true;	
		} else {
			bFirstTime = false;
		}
    	
    	//cache the pov so we can tell if its been changed
    	previousPOVValue = pov;
    	if (pov == OI.POV_0) {
    		double right = Robot.oi.driverJoystick.getRawAxis(OI.RIGHT_STICK);
    		if (bFirstTime) {
    			Robot.logger.console("Current Details: joystick: " + right + "; errorFromHeader " + errorFromHeading);
    		}
    		Robot.driveTrainSubsystem.tankDrive(right-errorFromHeading, right+errorFromHeading);
    	} else {
    		
    		//=== Normal joystick driving
    		//Disable the PID for HH and use normal tank drive
    		if (pidHH.isEnabled()) {
    			Robot.logger.console("Disabling heading hold");
    			pidHH.disable();
    		}
    		boolean mode  = Robot.driveTrainInfo.getReverseMode() ;
    		if (mode == true) {
    			double dLeft  = Robot.oi.driverJoystick.getRawAxis(OI.RIGHT_STICK);
        		double dRight = Robot.oi.driverJoystick.getRawAxis(OI.LEFT_STICK);
        		double oLeft  = Robot.oi.operatorJoystick.getRawAxis(OI.RIGHT_STICK);
        		double oRight = Robot.oi.operatorJoystick.getRawAxis(OI.LEFT_STICK);
        		double left   = oLeft/1.5 + dLeft;
        		double right  = oRight/1.5 + dRight;
        		Robot.driveTrainSubsystem.tankDrive(Robot.driveTrainJoystickGain.applyGain(left*-1),
                        Robot.driveTrainJoystickGain.applyGain(right*-1));
    		} else {
    			double dLeft  = Robot.oi.driverJoystick.getRawAxis(OI.LEFT_STICK);
        		double dRight = Robot.oi.driverJoystick.getRawAxis(OI.RIGHT_STICK);
        		double oLeft  = Robot.oi.operatorJoystick.getRawAxis(OI.LEFT_STICK);
        		double oRight = Robot.oi.operatorJoystick.getRawAxis(OI.RIGHT_STICK);
        		double left   = oLeft/1.5 + dLeft;
        		double right  = oRight/1.5 + dRight;
        		Robot.driveTrainSubsystem.tankDrive(Robot.driveTrainJoystickGain.applyGain(left),
                        Robot.driveTrainJoystickGain.applyGain(right));
    		}

    		
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
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
