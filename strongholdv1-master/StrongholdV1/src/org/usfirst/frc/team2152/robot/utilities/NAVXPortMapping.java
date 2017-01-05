package org.usfirst.frc.team2152.robot.utilities;

public class NAVXPortMapping {
	public static final int DIO = 0;
	public static final int PWM = 1;
	
	private static final int MAX_DIO = 10;
	private static final int MAX_PWM = 10; 
	
	/***
	 * Based on portType, calculates the port number on the roborio which
	 * maps to the desired port on the NAVX board.  This function is a manual copy
	 * of the WPI library example online.  It was copied because this machine
	 * did not have internet access at the time of need.  Otherwise it would
	 * have been a copy and paste.
	 * 
	 * @param portType
	 * @param portOnNAVX
	 * @return portNumber
	 */
	public static int getNAVXPort(int portType, int portOnNAVX) {
		int portNumberRIO = -1;
		
		switch (portType) {
		   case DIO:
			   if (portOnNAVX >= 0 && portOnNAVX < MAX_DIO)
				   portNumberRIO = portOnNAVX + MAX_DIO + 
				                   (portOnNAVX > 3 ? 4 : 0);
			   break;
		   case PWM:
			   if (portOnNAVX >= 0 && portOnNAVX < MAX_PWM)
				   portNumberRIO = portOnNAVX + MAX_PWM;
			   break;
		}
		
		return portNumberRIO;
	}

}
