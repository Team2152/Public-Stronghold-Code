
package org.usfirst.frc.team2152.robot.utilities;

public class PIDConstants {

	//Trying to keep PID values in central place so that we can update them as needed.

	//=== Defense Manipulator PID Gains
	public final static double DM_Kp        = 0.03;
	public final static double DM_Ki        = 0.00;
	public final static double DM_Kd        = 0.001;
	public final static int    DM_TOLERANCE = 2;
	public final static double DM_OUT_MIN   = -0.8;
	public final static double DM_OUT_MAX   = 0.8;

	//=== Drive Train Heading Hold PID Gains
	public final static double HH_Kp        = 0.08;
	public final static double HH_Ki        = 0.00000000000000000000001;
	public final static double HH_Kd        = 0.02;
	public final static double HH_TOLERANCE = 2.0;
	public final static float  HH_IN_MIN    = -180.0f;   //Kauai Labs - navx sample code uses float
	public final static float  HH_IN_MAX    = 180.0f;    //Kauai Labs - navx sample code uses float
	public final static double HH_OUT_MIN   = -1;
	public final static double HH_OUT_MAX   = 1;


	//=== Shooter Elevator PID Gains
	public final static double SE_Kp        = 0.00;
	public final static double SE_Ki        = 0.00;
	public final static double SE_Kd        = 0.00;
	public final static int    SE_TOLERANCE = 2;
	public final static double SE_OUT_MIN   = -0.3;
	public final static double SE_OUT_MAX   = 0.3;
	
	//=== Rotate to angle PID Gains
	public final static double RE_Kp        = 0.03;
	public final static double RE_Ki        = 0.00;
	public final static double RE_Kd        = 0.001;
	public final static int    RE_TOLERANCE = 2;
	public final static double RE_OUT_MIN   = -1;
	public final static double RE_OUT_MAX   = 1;


}
