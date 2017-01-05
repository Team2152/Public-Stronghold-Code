package org.usfirst.frc.team2152.robot.commands;

import org.usfirst.frc.team2152.robot.OI;
import org.usfirst.frc.team2152.robot.Robot;
import org.usfirst.frc.team2152.robot.subsystems.DefenseManipulator;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class DefenseManipulatorJoystick extends Command {

    public DefenseManipulatorJoystick() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.defenseManipulatorSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	//This will be joystick based movement; disable PID mechnism
    	Robot.defenseManipulatorSubsystem.disable();
    	//
    	//int lastval =
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	double down = Robot.oi.driverJoystick.getRawAxis(OI.LEFT_TRIGGER);
		double up = Robot.oi.driverJoystick.getRawAxis(OI.RIGHT_TRIGGER);
		if (Robot.defenseManipulatorSubsystem.getLimitSwitch() == DefenseManipulator.LIMIT_HIT) {
			down = 0;
		}
		double speed = down + (-up);
		Robot.defenseManipulatorSubsystem.setMotor(speed);
		
		//Reset the encoder if the defense manipulator hits the switch
		if (Robot.defenseManipulatorSubsystem.getLimitSwitch() == DefenseManipulator.LIMIT_HIT) {
			Robot.defenseManipulatorSubsystem.resetEncoder();
			Robot.logger.console("DM Joystick: reset encoder");
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
    	//If interrupted, then stop motor
    	Robot.defenseManipulatorSubsystem.setMotor(0);
    }
}
