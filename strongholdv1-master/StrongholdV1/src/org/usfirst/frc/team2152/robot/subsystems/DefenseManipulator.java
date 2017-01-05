package org.usfirst.frc.team2152.robot.subsystems;

import org.usfirst.frc.team2152.robot.Robot;
import org.usfirst.frc.team2152.robot.RobotMap;
import org.usfirst.frc.team2152.robot.commands.DefenseManipulatorJoystick;
import org.usfirst.frc.team2152.robot.utilities.NAVXPortMapping;
import org.usfirst.frc.team2152.robot.utilities.PIDConstants;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.command.PIDSubsystem;


/**
 *
 */
public class DefenseManipulator extends PIDSubsystem {

	//for this subsystem a reasonable amount of speed when performing non-PID operations	
	public  static final double    DEFAULT_SPEED    = 0.5;
	//for this subsystem: the longest amount of time for an operation to take place
	//before we say:  lets stop this to prevent physical damage to component
	public  static final int       WATCHDOG_TIMEOUT = 8; 
	//Subsystem has a switch:  since they can be wired open or closed define when its been "hit"
	public static final boolean    LIMIT_HIT        = false;
	public static final boolean    LIMIT_NOT_HIT    = true;
	
	//Components that make up this subsystem
	private VictorSP        motor; 
	private int             limitSwitchDIO;
    private DigitalInput    limitSwitch;   
    private Encoder         encoder;
    private int             encoderA = RobotMap.DIO_8;
    private int             encoderB = RobotMap.DIO_9;
    
	
	// Initialize your subsystem here
    public DefenseManipulator() {
        // Use these to get going:
        // setSetpoint() -  Sets where the PID controller should move the system
        //                  to
        // enable() - Enables the PID controller.
    	
    	//Setup PID
    	super(PIDConstants.DM_Kp, PIDConstants.DM_Ki, PIDConstants.DM_Kd);
    	this.getPIDController().setContinuous(false);
    	this.setOutputRange(PIDConstants.DM_OUT_MIN, PIDConstants.DM_OUT_MAX);
    	//this.disable();
    	
    	//Setup motor
    	motor          = new VictorSP(RobotMap.PWM_ZERO);
    	motor.enableDeadbandElimination(true);
    	motor.setSafetyEnabled(false);
    	motor.set(0);
    	
    	//Setup Limit Switch
    	limitSwitchDIO = NAVXPortMapping.getNAVXPort(NAVXPortMapping.DIO, RobotMap.DIO_MXP_1);
        limitSwitch    = new DigitalInput(limitSwitchDIO);
    	
    	//Setup Encoder
    	//Passed in true to reverse and get positive values
    	encoder        = new Encoder(encoderA, encoderB, true, EncodingType.k4X);
    	encoder.setDistancePerPulse(7);
    	encoder.setSamplesToAverage(7);
    	encoder.reset();
    	
    }
               
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new DefenseManipulatorJoystick());
    }
    
    protected double returnPIDInput() {
        // Return your input value for the PID loop
        // e.g. a sensor, like a potentiometer:
        // yourPot.getAverageVoltage() / kYourMaxVoltage;
    	return encoder.get();
    }
    
    protected void usePIDOutput(double output) {
        // Use output to drive your system, like a motor
        // e.g. yourMotor.set(output);
    	output = output * -1;
    	motor.set(output);
    	
    }
    
    public int getEncoderValue() {
    	return encoder.get();
    }
    
    public boolean getLimitSwitch() {
    	return limitSwitch.get();
    }
    
    public void setMotor(double speed) {
    	motor.set(speed);
    }
    
    public void resetEncoder() {
    	encoder.reset();
    }
    
    public boolean resetDM(double watchDogSecs) {
    	
    	Timer watchDogTimer = new Timer();
    	watchDogTimer.start();
    	while (true) {
    		motor.set(DEFAULT_SPEED);
    		if (limitSwitch.get() == LIMIT_HIT) {
    			motor.set(0);
    			encoder.reset();
    			break;
    		} else if (watchDogTimer.get() > watchDogSecs) {
    			Robot.logger.console("resetDM: Watchdog Timer Popped: " + watchDogSecs);
    			break;
    		}
    	}
    	motor.set(0);
    	watchDogTimer.stop();
    	return true;
    }
}
