package org.usfirst.frc.team2152.robot.utilities;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionSteerData {
	public final static String VISION_STEER_DATA  = "Vision Steer Data: ";
	public final static String VISION_STEER_SPEED_FORWARD_BACKWARD = "Forward and Backward Speed for Vision Steer: ";
	public final static String VISION_STEER_SPEED_TURN = "Turning Speed for Vision Steer: ";
	private boolean forward              = false;
	private boolean backward             = false;
	private boolean turnLeft             = false;
	private boolean turnRight            = false;
	private boolean strafeLeft           = false;
	private boolean strafeRight          = false;
	private double  forwardBackwardSpeed = 0.0;
	private double  turningSpeed         = 0.0;
	private Timer   watchDogTimer;
	public double   elapseTime           = 0.0;
	
	//TODO - time to teach lesson on atomic operations...this needs to be synchronized
	
	public VisionSteerData(double frontBack, double turning) {
		forwardBackwardSpeed = frontBack;
		turningSpeed         = turning;
	}
	
	public void turnOff() {
		forward     = false;
		backward    = false;
		turnLeft    = false;
		turnRight   = false;
		strafeLeft  = false;
		strafeRight = false;
	}
	
	public boolean getFoward()      { return forward;     }
	public boolean getBackward()    { return backward;    }
	public boolean getTurnLeft()    { return turnLeft;    }
	public boolean getTurnRight()   { return turnRight;   }
	public boolean getStrafeLeft()  { return strafeLeft;  }
	public boolean getStrafeRight() { return strafeRight; }
	public double  getForwardBackwardSpeed()   { return forwardBackwardSpeed;   }
	public double  getTurningSpeed()       { return turningSpeed;   }
	
	
	public void setData(String data) {
		String[] directions = data.split(";");
		if (directions.length == 7) {   
		   turnOff();
		   watchDogTimer = new Timer();
		   watchDogTimer.reset();
		   System.out.println(directions[0] + ";" + directions[1] + ";" + directions[2] + ";" + directions[3]);
		   forward                = (directions[0].trim().equalsIgnoreCase("True")) ? true : false;
		   backward               = (directions[1].trim().equalsIgnoreCase("True")) ? true : false;
		   turnLeft               = (directions[2].trim().equalsIgnoreCase("True")) ? true : false;
		   turnRight              = (directions[3].trim().equalsIgnoreCase("True")) ? true : false;
		   strafeLeft             = (directions[4].trim().equalsIgnoreCase("True")) ? true : false;
		   strafeLeft             = (directions[5].trim().equalsIgnoreCase("True")) ? true : false;
		   forwardBackwardSpeed   = SmartDashboard.getNumber(VISION_STEER_SPEED_FORWARD_BACKWARD, Gain.PCT_25);
		   turningSpeed           = SmartDashboard.getNumber(VISION_STEER_SPEED_TURN, Gain.PCT_25);
		   SmartDashboard.putString(VISION_STEER_DATA, getCurrentData());
		} else {
			turnOff();
		}
	}
	
	public String getCurrentData() {
		double elapseTime = watchDogTimer.get();
		return (forwardBackwardSpeed + ":" + turningSpeed + ": " + forward + ";" + backward + ";" + turnLeft + ";" + turnRight + ";" + strafeLeft + ";" + strafeRight);
	}
}