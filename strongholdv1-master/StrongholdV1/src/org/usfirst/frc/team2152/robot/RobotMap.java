package org.usfirst.frc.team2152.robot;
/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
    // For example to map the left and right motors, you could define the
    // following variables to use with your drivetrain subsystem.
    // public static int leftMotor = 1;
    // public static int rightMotor = 2;
    
    // If you are using multiple modules, make sure to define both the port
    // number and the module. For example you with a rangefinder:
    // public static int rangefinderPort = 1;
    // public static int rangefinderModule = 1;
	
	
	
	//=== Defense Manipulator subsystem ids
	public static final int PWM_ZERO                   = 0;   // DEFENSE MANIPULATOR ESC
	public static final int DIO_MXP_1                  = 1;   // DEFENSE MANIPULATOR LIMIT SWITCH
	public static final int DIO_8                      = 8;   // DEFENSE MANIPULATOR ENCODER A
	public static final int DIO_9                      = 9;   // DEFENSE MANIPULATOR ENCODER B
	
	//=== Drive Train subsystem ids
	public static final int motorFrontRightId          = 1;
	public static final int motorFrontLeftId           = 4;
	public static final int motorRearLeftId            = 3;
	public static final int motorRearRightId           = 2;
	
	//=== Intake subsystem ids
	public static final int PWM_ONE                    = 1;   // INTAKE MOTOR ESC
	
	//=== Shooter Elevator ids
	public static final int PWM_SIX                    = 6;   // SHOOTER ELEVATOR MOTOR (WINDOW MOTORS) ESC
	public static final int DIO_MXP_2                  = 2;   // SHOOTER ELEVATOR UPPER LIMIT SWITCH
	public static final int DIO_MXP_3                  = 3;   // SHOOTER ELEVATOR LOWER LIMIT SWITCH
	public static final int DIO_2                      = 2;   // ENCODER - PULSE - UNUSED IN CODE, BUT ITS CONNECTED
	public static final int DIO_3                      = 3;   // ENCODER - DIRECTION
	public static final int DIO_4                      = 4;   // ENCODER - COUNT (INDEX)
	
	//=== Shooter Launcher ids
	public static final int PWM_TWO                    = 2;   // SHOOTER LAUNCHER MOTOR LEFT
	public static final int PWM_THREE                  = 3;   // SHOOTER LAUNCHER MOTOR RIGHT
	public static final int PWM_FOUR                   = 4;   // SHOOTER LAUNCHER SERVO LEFT
	public static final int PWM_FIVE                   = 5;   // SHOOTER LAUNCHER SERVO RIGHT
	
	
}
