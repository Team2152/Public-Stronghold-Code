# strongholdv1

NOTE: Use the RAW view of this file for correct spacing.


Initial port of Stronghold Labview code into Java.


From LabView to Java Command Based template.


src/org/usfirst/frc/team2152/robot
OI.java................................Operator Interface (Joysticks, Buttons, etc)
Robot.java.............................Robots entry point; Sets up dashboard chooser; Define subsystems
RobotDirection.java....................2152: Module to define convenience enum of directions
RobotMap.java..........................Define names to ports, etc.  

src/org/usfirst/frc/team2152/robot/commands
AutonomousDefenseGroupCross.java.......2152: Command group which preps the robot (shooter, 
                                             defense manipulator, orientation and speed) and crosses the
                                             defense
DefenseManipulatorGroupMove.java.......2152: Command group which given a setPoint and tolerance will
                                             reset the DM and move it to the desired setPoint based on the 
                                             supplied tolerance (+/-)
DefenseManipulatorJoystick.java........2152: Command which uses the driverJoystick trigger buttons to move
                                             the DefenseManipulator (DM) up and down.  This is the default
                                             command for the DefenseManipulator subsystem.
DefenseManipulatorMove.java............2152: Base movement command for DM.  Takes setPoint, tolerance and 
                                             flag.  This is used by all other commands.
DefenseManipulatorReset.java...........2152: Moves the DM in stowed position until it hits the DM limit switch.
DriveTrainJoystick.java................2152: Command which uses the driverJoystick left/right sticks to control
                                             the robot using tank drive.  This is the default command for the 
                                             DriveTrain subsystem.  Uses POV at 0 to enable Heading Hold.  
                                             When using Heading Hold only the right stick is used.
DriveTrainMove.java....................2152: Command which moves the robot forward or backwards, using heading
                                             Hold or not, for a specified amount of time.
AutoTeleOpCrossChevalDeFrise.java......2152: Command which execute a predetermined set of movements to cross the cheval de 
                                             frise autonomously.
ShooterElevatorJoystick.java...........2152: Command which uses the operator Joystick trigger buttons to move
                                             the ShooterElevator (SE) up or down.  This is the default command for
                                             the ShooterElevator subsystem.
ShooterElevatorMove.java...............2152: Command which moves the SE up for the specified amount of time.  This command
                                             should not be used by itself.  Only as part of the command group 
                                             ShooterElevatorResetThenMoveUp().
ShooterElevatorReset.java..............2152: Command which moves the SE to the lowest point (until the SE low limit
                                             switch is hit).
ShooterElevatorResetThenMoveUp.java....2152: Command group which resets the SE then moves it up for the specified amount of time.
ShooterGroupCommand.java...............2152: Command group which either Loads or Launches a boulder until the specified joystick
                                             button is de-pressed. This command group controls the ShooterLauncher and the Intake
                                             subsystems.
ShooterLaunchUse.java..................2152: Command which controls the Shooter's ability to load or launch.  
StrongholdGroupTest.java...............2152: Command group to test/debug other commands.  Use as necessary.

src/org/usfirst/frc/team2152/robot/subsystems
DefenseManipulator.java................2152: Subsystem which describes and provides access to DM.  Motor(VictorSP), 
                                             Encoder, Limit Switch. PIDSubsystem. Default command DefenseManipulatorJoystick().
DriveTrain.java........................2152: Subsystem which describes the drive train and provices access to drivetrain.
                                             4 x Motor(CANTalon SRX), TankDrive.  Default command DriveTrainJoystick().
Intake.java............................2152: Subsystem which describes the boulder intake. Motor(VictorSP).
NavX.java..............................2152: Subsystem which describes and provides access to the NavX board.
ShooterElevator.java...................2152: Subsystem whihc describes and provides access to the ShooterElevator.
                                             2 Window Motors, 1 ESC (VictorSP), Counter, Limit Switch High, Limit Switch Low.
ShooterLauncher.java...................2152: Subsystem which describes and provides access to ShooterLauncher.  2 CIM Motors each
                                             with ESC (VictorSP), 2 Continuous Rotation Servos each with ESC (VictorSP).

src/org/usfirst/frc/team2152/robot/utilities
Gain.java..............................2152: Utility class to apply gains for the drive train.
Log.java...............................2152: Utility class to log to the driver statin console.  Much like a subsystem there is only
                                             one instance which allows us to turn it on or off as needed.
NAVXPortMapping.java...................2152: Utility class to easily map ports on the Navx to actual port numbers on the Roborio.
PIDContants.java.......................2152: Utility class to easily define PID gains in a central place.

