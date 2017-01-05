package org.usfirst.frc.team2152.robot.commands;

import org.usfirst.frc.team2152.robot.Robot;
import org.usfirst.frc.team2152.robot.RobotDirection;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ShooterLauncherUse extends Command {

	public final static double SERVO_DELAY = 1.5;
	
	Joystick       joyStick      = null;
	int            buttonToCheck = 1;
	RobotDirection direction     = RobotDirection.LOAD;
	Timer          delayTimer    = null;
	
    public ShooterLauncherUse(Joystick js, int buttonId, RobotDirection dir) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.shooterLauncherSubsystem);
    	requires(Robot.intakeSubsystem);
    	delayTimer    = new Timer();
    	joyStick      = js;
    	buttonToCheck = buttonId;
    	direction     = dir;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	Robot.logger.console("ShooterLauncherUse: button to check: " + buttonToCheck);
    	Robot.logger.console("ShooterLauncherUse: direction: " + direction);
    	delayTimer.reset();
    	delayTimer.start();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	if (direction == RobotDirection.LAUNCH) {
    		Robot.shooterLauncherSubsystem.launchFire();
    	    Robot.intakeSubsystem.inTakeFire();
    		if (delayTimer.get() > SERVO_DELAY)
    			Robot.shooterLauncherSubsystem.servoFire();
    	} else {
    		Robot.shooterLauncherSubsystem.launchLoad();
    		Robot.shooterLauncherSubsystem.servoLoad();
    		Robot.intakeSubsystem.inTakeLoad();
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	boolean value = false;
		if (joyStick != null && joyStick.getRawButton(buttonToCheck) == true)
			value = false;
		else
			value = true;
		return value;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.shooterLauncherSubsystem.stopLauncher();
    	Robot.intakeSubsystem.inTakeStop();
    	delayTimer.stop();
    	Robot.logger.console("ShooterLauncherUse: sequence ended: " + delayTimer.get());
    	delayTimer.reset();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Robot.shooterLauncherSubsystem.stopLauncher();
    }
}
