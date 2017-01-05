package org.usfirst.frc.team2152.robot.commands;

import org.usfirst.frc.team2152.robot.Robot;
import org.usfirst.frc.team2152.robot.subsystems.DriveTrain;
import org.usfirst.frc.team2152.robot.OI;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class VisionSteer extends Command{


	
	public boolean mecanumEnabled = false;
	public boolean     strafeLeft = false;
	public boolean    strafeRight = false;
	public boolean    forwardMove = false;
	public boolean   backwardMove = false;
	public boolean       turnLeft = false;
	public boolean      turnRight = false;
	public int             button = 0;
	public Joystick      joystick = null;
	private Timer   watchDogTimer;

	public VisionSteer(Joystick js, int whichButton, boolean turnR, boolean turnL, boolean forward, boolean backward, boolean strafeL, boolean strafeR, boolean mecanum) {
		// Use requires() here to declare subsystem dependencies
		// eg. requires(chassis);
		requires(Robot.driveTrainSubsystem);
		mecanumEnabled  = mecanum;
		strafeLeft      = strafeL;
		strafeRight     = strafeR;
		forwardMove     = forward;
		backwardMove    = backward;
		turnLeft        = turnL;
		turnRight       = turnR;
		joystick        = js;
		button          = whichButton;
		watchDogTimer   = new Timer();
		watchDogTimer.reset();
		
		
				
		


	}



	public void initialize() {
		Robot.driveTrainSubsystem.tankDrive(0, 0);

	}

	
	public void execute() {
		if(mecanumEnabled == true){
			if(strafeLeft == true && strafeRight == false){
				//watchDogTimer.start();
				Robot.driveTrainSubsystem.mecanumDrive_Cartesian(0.5, 0);
				

			
			 }else if(strafeLeft == false && strafeRight == true){
				//watchDogTimer.start();
				Robot.driveTrainSubsystem.mecanumDrive_Cartesian(-0.5, 0);
			}

		}else if(forwardMove == true && backwardMove == false && turnRight == false && turnLeft == false){
			//watchDogTimer.start();
			Robot.driveTrainSubsystem.tankDrive(DriveTrain.forward(0.5),DriveTrain.forward(0.5));

		}else if (backwardMove == true && forwardMove == false && turnRight == false && turnLeft == false){
			//watchDogTimer.start();
			Robot.driveTrainSubsystem.tankDrive(DriveTrain.reverse(0.5), DriveTrain.reverse(0.5));

		}else if (turnLeft == true && turnRight == false){
			//watchDogTimer.start();
			Robot.driveTrainSubsystem.tankDrive(-0.5, 0.5);

		}else if (turnRight == true && turnLeft == false){
			//watchDogTimer.start();
			Robot.driveTrainSubsystem.tankDrive(0.5, -0.5);
		}else{
			Robot.driveTrainSubsystem.tankDrive(0, 0);}
	}


	protected boolean isFinished() {
    	boolean value = false;
		if (joystick != null && joystick.getRawButton(button) == true)
			value = false;
		else
			value = true;
		return value;
	}

	protected void end() {
		// TODO Auto-generated method stub

	}

	protected void interrupted() {
		// TODO Auto-generated method stub

	}
}
