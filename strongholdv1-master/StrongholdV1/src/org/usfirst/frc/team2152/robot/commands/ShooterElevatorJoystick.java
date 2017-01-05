package org.usfirst.frc.team2152.robot.commands;

import org.usfirst.frc.team2152.robot.OI;
import org.usfirst.frc.team2152.robot.Robot;
import org.usfirst.frc.team2152.robot.subsystems.ShooterElevator;
import org.usfirst.frc.team2152.robot.utilities.Gain;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ShooterElevatorJoystick extends Command {
	private Gain sGain;

    public ShooterElevatorJoystick() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.shooterElevatorSubsystem);
    	sGain = new Gain(0.4, 0);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	//This will be joystick based movement; disable PID mechnism
    	Robot.shooterElevatorSubsystem.disable();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	double left = Robot.oi.operatorJoystick.getRawAxis(OI.LEFT_TRIGGER);
    	left = left*0.2;
		double right = Robot.oi.operatorJoystick.getRawAxis(OI.RIGHT_TRIGGER);
		right = right*0.5;
		if (Robot.shooterElevatorSubsystem.getLimitSwitchLower() == ShooterElevator.LIMIT_HIT) {
			left = 0;
		}
		double speed = left + (-right);
		Robot.shooterElevatorSubsystem.setMotor(speed);
		
		//Let console know when you hit upper limit
		if (Robot.shooterElevatorSubsystem.getLimitSwitchUpper() == ShooterElevator.LIMIT_HIT) {
			Robot.logger.console("SE Joystick: upper limit switch hit");
		} else if (Robot.shooterElevatorSubsystem.getLimitSwitchLower() == ShooterElevator.LIMIT_HIT) {
			Robot.shooterElevatorSubsystem.resetCounter();
		} else {
			Robot.shooterElevatorSubsystem.setMotor(speed);
			//Robot.logger.console("SE Joystick: counter: " + Robot.shooterElevatorSubsystem.getCounterValue());
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
    	Robot.shooterElevatorSubsystem.setMotor(0);
    }
}
