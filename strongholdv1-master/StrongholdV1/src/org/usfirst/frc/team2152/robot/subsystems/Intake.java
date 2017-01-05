package org.usfirst.frc.team2152.robot.subsystems;

import org.usfirst.frc.team2152.robot.RobotMap;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Intake extends Subsystem {

	// Put methods for controlling this subsystem
	// here. Call these from Commands.
	//for this subsystem a reasonable amount of speed when performing non-PID operations	
	public  static final double    DEFAULT_SPEED    = 1;
	

	//Components that make up this subsystem
	private VictorSP        motor;
		
	public Intake() {
		//Setup motor
    	motor = new VictorSP(RobotMap.PWM_ONE);
    	motor.enableDeadbandElimination(true);
    	motor.setSafetyEnabled(false);
	}

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new IntakeTest());
	}

	public void inTakeLoad() {
		motor.set(DEFAULT_SPEED);
	}
	
	public void inTakeFire() {
		motor.set(-1 * DEFAULT_SPEED);
	}
	
	public void inTakeStop() {
		motor.set(0);
	}
}

