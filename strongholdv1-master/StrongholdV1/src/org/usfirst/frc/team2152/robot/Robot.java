package org.usfirst.frc.team2152.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import org.usfirst.frc.team2152.robot.commands.AutonomousDefenseGroupCross;
import org.usfirst.frc.team2152.robot.subsystems.DefenseManipulator;
import org.usfirst.frc.team2152.robot.subsystems.DriveTrain;
import org.usfirst.frc.team2152.robot.subsystems.Intake;
import org.usfirst.frc.team2152.robot.subsystems.NavX;
import org.usfirst.frc.team2152.robot.subsystems.ShooterElevator;
import org.usfirst.frc.team2152.robot.subsystems.ShooterLauncher;
import org.usfirst.frc.team2152.robot.utilities.DriveTrainInfo;
import org.usfirst.frc.team2152.robot.utilities.Gain;
import org.usfirst.frc.team2152.robot.utilities.Log;
import org.usfirst.frc.team2152.robot.utilities.VisionSteerData;
import org.usfirst.frc.team2152.robot.network.UDPReceiver;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	public static final DefenseManipulator defenseManipulatorSubsystem = new DefenseManipulator();
	public static final NavX               navxSubsystem               = new NavX();
	public static final DriveTrain         driveTrainSubsystem         = new DriveTrain();
	public static final Intake             intakeSubsystem             = new Intake();
    public static final ShooterElevator    shooterElevatorSubsystem    = new ShooterElevator();
	public static final ShooterLauncher    shooterLauncherSubsystem    = new ShooterLauncher();
	public static final Log                logger                      = new Log(Log.LOG_DEFAULT);
	public static final Gain               driveTrainJoystickGain      = new Gain();      
	public static final DriveTrainInfo     driveTrainInfo              = new DriveTrainInfo();
	public static final int                UDP_DATA_PORT               = 5807;
	public static OI oi;
	public static final VisionSteerData visionSteerData = new VisionSteerData(Gain.PCT_25, Gain.PCT_25);


    Command                                autonomousCommand;
    SendableChooser                        autoModeChooser;
    UDPReceiver                             udpReceiver;
    
    

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
		//=== Create Operator Interface ===
    	oi = new OI();
    	
    	//=== Create the Autonomous Mode widget and associate the appropriate Command for each defense
        autoModeChooser = new SendableChooser();
        autoModeChooser.addDefault("No Autonomous",          null);
        autoModeChooser.addObject ("Portcullis",             null);
        autoModeChooser.addObject ("Cheval de Frise",        null);
        autoModeChooser.addObject ("Moat",                   new AutonomousDefenseGroupCross(RobotDirection.FORWARD, RobotDirection.UP, -0.80, 0.2)); //4
        autoModeChooser.addObject ("Ramparts",               new AutonomousDefenseGroupCross(RobotDirection.FORWARD, RobotDirection.UP, 0.8, 0.2));//4
        autoModeChooser.addObject ("Drawbridge",             null);
        autoModeChooser.addObject ("Sally Port",             null);
        autoModeChooser.addObject ("Rock Wall",              new AutonomousDefenseGroupCross(RobotDirection.FORWARD, RobotDirection.UP, 0.80, 0.2));//4
        autoModeChooser.addObject ("Rough Terrain",          new AutonomousDefenseGroupCross(RobotDirection.FORWARD, RobotDirection.UP, 0.75, 0.2));//4
        autoModeChooser.addObject ("Low Bar",                new AutonomousDefenseGroupCross(RobotDirection.FORWARD, RobotDirection.DOWN, 0.6, 0.2));//4
        SmartDashboard.putData("Auto mode", autoModeChooser);
        
        //=== Create the widget to control the drive train Gain.  The Gain object will check the dashboard
        SmartDashboard.putNumber(Gain.DRIVE_TRAIN_GAIN, Gain.PCT_100);
        
        //=== Create the widget that controls whether we log to the console
        SmartDashboard.putBoolean(Log.LOG_SETTING, Log.LOG_DEFAULT);

        CameraServer server = CameraServer.getInstance();
        server.setQuality(5);
        server.startAutomaticCapture("cam0");   
        try {
        	
        	udpReceiver = new UDPReceiver(UDP_DATA_PORT);
        	udpReceiver.start();
        } catch (Exception e) {
        	System.err.println("Robot: Error starting communications: " + e.toString());
        }
        
      
    }
	
	/**
     * This function is called once each time the robot enters Disabled mode.
     * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
     */
    public void disabledInit(){

    }
	
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString code to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the chooser code above (like the commented example)
	 * or additional comparisons to the switch structure below with additional strings & commands.
	 */
    public void autonomousInit() {
        autonomousCommand = (Command) autoModeChooser.getSelected();
           	
    	// schedule the autonomous command
        if (autonomousCommand != null) autonomousCommand.start();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    }

    public void teleopInit() {
		// This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to 
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (autonomousCommand != null) autonomousCommand.cancel();
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
}
