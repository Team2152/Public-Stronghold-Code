package org.usfirst.frc.team2152.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

import org.usfirst.frc.team2152.robot.commands.AutoTeleOpCrossChevalDeFrise;
import org.usfirst.frc.team2152.robot.commands.DefenseManipulatorReset;
import org.usfirst.frc.team2152.robot.commands.DriveTrainInfoReverseModeToggle;
import org.usfirst.frc.team2152.robot.commands.ShooterGroupCommand;
import org.usfirst.frc.team2152.robot.commands.VisionSteer;



/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
	//// CREATING BUTTONS
	// One type of button is a joystick button which is any button on a joystick.
	// You create one by telling it which joystick it's on and which button
	// number it is.
	// Joystick stick = new Joystick(port);
	// Button button = new JoystickButton(stick, buttonNumber);

	// There are a few additional built in buttons you can use. Additionally,
	// by subclassing Button you can create custom triggers and bind those to
	// commands the same as any other Button.

	//// TRIGGERING COMMANDS WITH BUTTONS
	// Once you have a button, it's trivial to bind it to a button in one of
	// three ways:

	// Start the command when the button is pressed and let it run the command
	// until it is finished as determined by it's isFinished method.
	// button.whenPressed(new ExampleCommand());

	// Run the command while the button is being held down and interrupt it once
	// the button is released.
	// button.whileHeld(new ExampleCommand());

	// Start the command when the button is released  and let it run the command
	// until it is finished as determined by it's isFinished method.
	// button.whenReleased(new ExampleCommand());

	//Team 2152:  research whether we need to expose the joysticks and buttons outside of this class
	//Note:  if we can bind all of the buttons here to commands then we may not need to expose any objects.

	//=== POV values for primary directions
	public final static int POV_0    = 0;
	public final static int POV_90   = 90;
	public final static int POV_180  = 180;
	public final static int POV_270  = 270;
	
	//===Joystick IDs
	public final static int DRIVER_JOYSTICK   = 0;
	public final static int OPERATOR_JOYSTICK = 1;
	
	//=== Axis ids; they are the same for each joystick
	public final static int LEFT_STICK    = 1;
	public final static int RIGHT_STICK   = 5;
	//public final static int SPEED_STICK   = 999999;  // Deoderant...always wear it.
	public final static int LEFT_TRIGGER  = 2;
	public final static int RIGHT_TRIGGER = 3;
	
	//=== Define the various components of a joystick.  
	//=== Note: stronghold does not use all of these.  Once the code ported
	//=== we can remove the unused ones.
	public Joystick driverJoystick;
	private Button dButtonA;
	private Button dButtonB;
	private Button dButtonX;
	private Button dButtonY;
	private Button dButtonBumpL;
	private Button dButtonBumpR;
	final private int dButtonAid     = 1;
	final private int dButtonBid     = 2;
	final private int dButtonXid     = 3;
	final private int dButtonYid     = 4;
	final private int dButtonBumpLid = 5;
	final private int dButtonBumpRid = 6;

	public Joystick operatorJoystick;
	private Button oButtonA;
	private Button oButtonB;
	private Button oButtonX;
	private Button oButtonY;
	private Button oButtonBumpL;
	private Button oButtonBumpR;
	private Button oButtonBack;
	private Button oButtonStart;
	final private int oButtonAid     = 1;
	final private int oButtonBid     = 2;
	final private int oButtonXid     = 3;
	final private int oButtonYid     = 4;
	final private int oButtonBumpLid = 5;
	final private int oButtonBumpRid = 6;
	final private int oButtonBackid  = 7;
	final private int oButtonStartid = 8;

	public OI() {

		//Setup driver joystick1
		try {
			driverJoystick = new Joystick(DRIVER_JOYSTICK);
			dButtonA       = new JoystickButton(driverJoystick, dButtonAid);
			dButtonB       = new JoystickButton(driverJoystick, dButtonBid);
			dButtonX       = new JoystickButton(driverJoystick, dButtonXid);
			dButtonY       = new JoystickButton(driverJoystick, dButtonYid);
			dButtonBumpL   = new JoystickButton(driverJoystick, dButtonBumpLid);
			dButtonBumpR   = new JoystickButton(driverJoystick, dButtonBumpRid);
			setupDriverButtons();
		} catch (Exception e) {
			Robot.logger.console("OI: Unable to setup driver joystick: " + e.toString());
		}

		//Setup operator joystick
		try {
			operatorJoystick = new Joystick(OPERATOR_JOYSTICK);
			oButtonA         = new JoystickButton(operatorJoystick, oButtonAid);
			oButtonB         = new JoystickButton(operatorJoystick, oButtonBid);
			oButtonX         = new JoystickButton(operatorJoystick, oButtonXid);
			oButtonY         = new JoystickButton(operatorJoystick, oButtonYid);
			oButtonBumpL     = new JoystickButton(operatorJoystick, oButtonBumpLid);
			oButtonBumpR     = new JoystickButton(operatorJoystick, oButtonBumpRid);
			oButtonBack      = new JoystickButton(operatorJoystick, oButtonBackid);
			oButtonStart     = new JoystickButton(operatorJoystick, oButtonStartid);
			setupOperatorButtons();
		} catch (Exception e) {
			Robot.logger.console("OI: Unable to setup operator joystick: " + e.toString());
		}

	}
	

	public void setupDriverButtons() {
		dButtonB.whenReleased(new DefenseManipulatorReset());
		dButtonY.whenReleased(new AutoTeleOpCrossChevalDeFrise(850, 0.5, 0.8, 1.5, 0.8));
		dButtonA.whenReleased(new DriveTrainInfoReverseModeToggle());
		//ButtonX.whenReleased(new StrongholdGroupTest());
		//dButtonB.whenReleased(new ShooterElevatorMove(RobotDirection.DOWN, 1));
		//dButtonY.whenReleased(new ShooterElevatorMove(RobotDirection.UP, 1));
	}

	public void setupOperatorButtons() {
		oButtonBumpL.whenPressed(new ShooterGroupCommand(operatorJoystick, oButtonBumpLid, RobotDirection.LOAD));
		oButtonBumpR.whenPressed(new ShooterGroupCommand(operatorJoystick, oButtonBumpRid, RobotDirection.LAUNCH));
		//boolean turnR, boolean turnL, boolean forward, boolean backward, boolean strafeL, boolean strafeR, boolean mecanum
		oButtonX.whenPressed(new VisionSteer(operatorJoystick, oButtonXid, true, false, false, false, false, false, false));
		//oButtonX.whenReleased(new VisionSteer(false, false, false, false, false, false, false));
		oButtonB.whenPressed(new VisionSteer(operatorJoystick, oButtonBid, false, true, false, false, false, false, false));
		//oButtonB.whenReleased(new VisionSteer(false, false, false, false, false, false, false));
		oButtonY.whenPressed(new VisionSteer(operatorJoystick, oButtonYid, false, false, true, false, false, false, false));
		//oButtonY.whenReleased(new VisionSteer(false, false, false, false, false, false, false));
		oButtonA.whenPressed(new VisionSteer(operatorJoystick, oButtonAid, false, false, false, true, false, false, false));
		//oButtonA.whenReleased(new VisionSteer(false, false, false, false, false, false, false));
		oButtonBack.whenPressed(new VisionSteer(operatorJoystick, oButtonBackid, false, false, false, false, true, false, true));
		//oButtonBack.whenReleased(new VisionSteer(false, false, false, false, false, false, false));
		oButtonStart.whenPressed(new VisionSteer(operatorJoystick, oButtonStartid, false, false, false, false, false, true, true));
		//oButtonStart.whenReleased(new VisionSteer(false, false, false, false, false, false, false));
	} 
}

