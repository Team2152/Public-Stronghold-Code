package org.usfirst.frc.team2152.robot.subsystems;

import org.usfirst.frc.team2152.robot.RobotMap;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.command.Subsystem;


/**
 *
 */
public class ShooterLauncher extends Subsystem {

	//for this subsystem a reasonable amount of speed when performing non-PID operations	
	public  static final double    DEFAULT_SPEED    = 1;
	//for this subsystem: the longest amount of time for an operation to take place
	//before we say:  lets stop this to prevent physical damage to component
	public  static final int       WATCHDOG_TIMEOUT = 8; 
		
	
	//Components that make up this subsystem
	private VictorSP        motorL; 
	private VictorSP        motorR;
	private VictorSP        servoL;
	private VictorSP        servoR;
	//=== Drive Train
	private RobotDrive      servoDrive;
	private RobotDrive      motorDrive;
	
	
	
	// Initialize your subsystem here
    public ShooterLauncher() {
        
    	//Setup motor
    	motorL              = new VictorSP(RobotMap.PWM_TWO);
    	motorL.enableDeadbandElimination(true);
    	motorL.set(0);
    	motorR              = new VictorSP(RobotMap.PWM_THREE);
    	motorR.enableDeadbandElimination(true);
    	motorR.set(0);
    	
    	//Setup motor
    	servoL              = new VictorSP(RobotMap.PWM_FOUR);
    	servoL.enableDeadbandElimination(true);
    	servoL.set(0);
    	servoR              = new VictorSP(RobotMap.PWM_FIVE);
    	
    	servoR.enableDeadbandElimination(true);
    	servoR.set(0);
    	
    	//=== Create drive train object for 4 motors ===
        motorDrive = new RobotDrive(motorL, motorR);
        motorDrive.tankDrive(0, 0);
        motorDrive.setSafetyEnabled(false);
        servoDrive = new RobotDrive(servoL, servoR);
        servoDrive.tankDrive(0, 0);
        motorDrive.setSafetyEnabled(false);
    }
               
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand();
    }
       
    public void launchFire() {
    	motorDrive.tankDrive(DEFAULT_SPEED, DEFAULT_SPEED);
    }
    
	public void launchLoad() {
		double speed = -1 * DEFAULT_SPEED;
		motorDrive.tankDrive(speed, speed);
	}
	
	public void servoLoad() {
		servoDrive.tankDrive(DEFAULT_SPEED, DEFAULT_SPEED);
	}
    
	public void servoFire() {
		double speed = -1 * DEFAULT_SPEED;
		servoDrive.tankDrive(speed, speed);
	}
	
	public void stopLauncher() {
		servoDrive.tankDrive(0, 0);
		motorDrive.tankDrive(0, 0);
	}
	
    
}
