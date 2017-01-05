package org.usfirst.frc.team2152.robot.commands;

import org.usfirst.frc.team2152.robot.Robot;
import org.usfirst.frc.team2152.robot.subsystems.DriveTrain;
import org.usfirst.frc.team2152.robot.OI;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class VisionSteerDefault extends Command{


	public final static String  VISION_STEER              = "Enable Vision Steer?";
	public final static int     VISION_STEER_WATCHDOG_MAX = 1;
	
	//Only this class needs access to these variables (class members)
	private boolean mecanumEnabled = false;
	private boolean     strafeLeft = false;
	private boolean    strafeRight = false;
	private boolean    forwardMove = false;
	private boolean   backwardMove = false;
	private boolean       turnLeft = false;
	private boolean      turnRight = false;
	private int             button = 0;
	private Joystick      joystick = null;
	private Timer    watchDogTimer = null;
	private double       forwardBackwardSpeed = 0.5;
	private double       turningSpeed         = 0.6;

	public VisionSteerDefault() {
		// Use requires() here to declare subsystem dependencies
		// eg. requires(chassis);
		requires(Robot.driveTrainSubsystem);
		watchDogTimer   = new Timer();
		watchDogTimer.reset();
	}

    public void getVisionData() {
    	forwardMove = Robot.visionSteerData.getFoward();
    	backwardMove = Robot.visionSteerData.getBackward();
    	turnLeft = Robot.visionSteerData.getTurnLeft();
    	turnRight = Robot.visionSteerData.getTurnRight();
    	strafeLeft = Robot.visionSteerData.getStrafeLeft();
    	strafeRight = Robot.visionSteerData.getStrafeRight();
    	forwardBackwardSpeed = Robot.visionSteerData.getForwardBackwardSpeed();
    	turningSpeed = Robot.visionSteerData.getTurningSpeed();
    	//SmartDashboard.putString(VisionSteerDefault.VISION_STEER, Robot.visionSteerData.getCurrentData());
    }

	public void initialize() {
		Robot.driveTrainSubsystem.tankDrive(0, 0);
	}

	public void execute() {
		getVisionData();
		if(mecanumEnabled == true){
			if(strafeLeft == true && strafeRight == false){
				Robot.driveTrainSubsystem.mecanumDrive_Cartesian(forwardBackwardSpeed, 0);
			}else if(strafeLeft == false && strafeRight == true){
				Robot.driveTrainSubsystem.mecanumDrive_Cartesian(-forwardBackwardSpeed, 0);
			}
		}else if(forwardMove == true && backwardMove == false && turnRight == false && turnLeft == false){
			Robot.driveTrainSubsystem.tankDrive(DriveTrain.forward(forwardBackwardSpeed),DriveTrain.forward(forwardBackwardSpeed));

		}else if (backwardMove == true && forwardMove == false && turnRight == false && turnLeft == false){
			Robot.driveTrainSubsystem.tankDrive(DriveTrain.reverse(forwardBackwardSpeed), DriveTrain.reverse(forwardBackwardSpeed));

		}else if (turnLeft == true && turnRight == false){
			Robot.driveTrainSubsystem.tankDrive(DriveTrain.reverse(turningSpeed), DriveTrain.forward(turningSpeed));

		}else if (turnRight == true && turnLeft == false){
			Robot.driveTrainSubsystem.tankDrive(DriveTrain.forward(turningSpeed), DriveTrain.reverse(turningSpeed));
		}else{
			Robot.driveTrainSubsystem.tankDrive(0, 0);}
	}


	protected boolean isFinished() {
    	return false;
	}

	protected void end() {
		// TODO Auto-generated method stub
		Robot.driveTrainSubsystem.tankDrive(0, 0);

	}

	protected void interrupted() {
		// TODO Auto-generated method stub
		Robot.driveTrainSubsystem.tankDrive(0, 0);

	}
}