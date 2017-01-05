package org.usfirst.frc.team2152.robot.subsystems;

import org.usfirst.frc.team2152.robot.Robot;
import org.usfirst.frc.team2152.robot.RobotMap;
import org.usfirst.frc.team2152.robot.commands.ShooterElevatorJoystick;
import org.usfirst.frc.team2152.robot.utilities.NAVXPortMapping;
import org.usfirst.frc.team2152.robot.utilities.PIDConstants;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.command.PIDSubsystem;


/**
 *
 */
public class ShooterElevator extends PIDSubsystem {
 
	//for this subsystem a reasonable amount of speed when performing non-PID operations	
	public  static final double    DEFAULT_SPEED_UP   = 1;
	public  static final double    DEFAULT_SPEED_DOWN = 0.35;
	//for this subsystem: the longest amount of time for an operation to take place
	//before we say:  lets stop this to prevent physical damage to component
	public  static final int       WATCHDOG_TIMEOUT = 5; 
	//Subsystem has a switch:  since they can be wired open or closed define when its been "hit"
	public static final boolean    LIMIT_HIT              = false;
	public static final boolean    LIMIT_NOT_HIT          = true;
	public static final double     ENCODER_MULTIPLIER     = 0.087890625;


	//Components that make up this subsystem
	private VictorSP        motor; 
	private int             limitSwitchUpperDIO;
	private DigitalInput    limitSwitchUpper; 
	private int             limitSwitchLowerDIO;
	private DigitalInput    limitSwitchLower;   
	private Counter         counter;

	// Initialize your subsystem here
	public ShooterElevator() {
		// Use these to get going:
		// setSetpoint() -  Sets where the PID controller should move the system
		//                  to
		// enable() - Enables the PID controller.

		//Setup PID
		super(PIDConstants.SE_Kp, PIDConstants.SE_Ki, PIDConstants.SE_Kd);
		this.getPIDController().setContinuous(false);
		this.setOutputRange(PIDConstants.SE_OUT_MIN, PIDConstants.SE_OUT_MAX);
		this.disable(); //DO NOT ENABLE UNLESS YOU HAVE GREAT, NO, PERFECT PID Gains - otherwise you will lock up the window motor worm gears
		//Setup motor
		motor               = new VictorSP(RobotMap.PWM_SIX);
		motor.enableDeadbandElimination(true);
		motor.setSafetyEnabled(false);
		motor.set(0);

		//Setup Limit Switches
		limitSwitchUpperDIO = NAVXPortMapping.getNAVXPort(NAVXPortMapping.DIO, RobotMap.DIO_MXP_2);
		limitSwitchUpper    = new DigitalInput(limitSwitchUpperDIO);
		limitSwitchLowerDIO = NAVXPortMapping.getNAVXPort(NAVXPortMapping.DIO, RobotMap.DIO_MXP_3);
		limitSwitchLower    = new DigitalInput(limitSwitchLowerDIO);


		//Setup Counter (encoder on shooter is a Counter)
		try {

			counter = new Counter();
			counter.setUpSource(RobotMap.DIO_4);
			counter.setUpSourceEdge(true, true);
			counter.setDownSource(RobotMap.DIO_3);
			counter.setExternalDirectionMode();
			counter.reset();

		} catch (Exception e) {
			Robot.logger.console("ShooterElevator: exception: " + e.toString());
		}
	}

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		setDefaultCommand(new ShooterElevatorJoystick());
	}

	protected double returnPIDInput() {
		// Return your input value for the PID loop
		// e.g. a sensor, like a potentiometer:
		// yourPot.getAverageVoltage() / kYourMaxVoltage;
		//DO NOT ENABLE UNLESS YOU HAVE PERFECTLY TUNED PID GAINS
		return 0; //counter.get();
	}

	protected void usePIDOutput(double output) {
		// Use output to drive your system, like a motor
		// e.g. yourMotor.set(output);
		//output = output * -1;
		//motor.set(output);
		//DO NOT ENABLE UNLESS YOU HAVE PERFECTLY TUNED PID GAINS:  RISK == Locking up window motor worm gears
	}

	public double getCounterValue() {
		return (((double)counter.get() * -1) * ENCODER_MULTIPLIER); 
	}
	
	public void resetCounter() {
		counter.reset();
	}

	public boolean getLimitSwitchUpper() {
		return limitSwitchUpper.get();
	}

	public boolean getLimitSwitchLower() {
		return limitSwitchLower.get();
	}

	public void setMotor(double speed) {
		motor.set(speed);
	}
	
	public static double up(double speed) {
		return (-1 * speed);
	}

	public static double down(double speed) {
		return speed;
	}
}
