package org.usfirst.frc.team2152.robot.commands;

import org.usfirst.frc.team2152.robot.Robot;
import org.usfirst.frc.team2152.robot.RobotDirection;
import org.usfirst.frc.team2152.robot.subsystems.DriveTrain;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutonomousDefenseGroupCross extends CommandGroup {
    
    public  AutonomousDefenseGroupCross(RobotDirection robotOrientation, RobotDirection shooterOrientation, double speed, double seconds) {
        // Add Commands here:
        // e.g. addSequential(new Command1());
        //      addSequential(new Command2());
        // these will run in order.

        // To run multiple commands at the same time,
        // use addParallel()
        // e.g. addParallel(new Command1());
        //      addSequential(new Command2());
        // Command1 and Command2 will run in parallel.

        // A command group will require all of the subsystems that each member
        // would require.
        // e.g. if Command1 requires chassis, and Command2 requires arm,
        // a CommandGroup containing them would require both the chassis and the
        // arm.
    	addParallel(new DefenseManipulatorReset());
    	
    	if (shooterOrientation == RobotDirection.DOWN)
    		addParallel(new ShooterElevatorReset());
    	else if (shooterOrientation == RobotDirection.UP){
    		addParallel(new ShooterElevatorResetThenMoveUp(1));
    	} else {
    		Robot.logger.console("AutonomousDefenseCross: do not move shooter");
    	}
    	
    	//Convert the speed based on the robotOrientation
    	speed = robotOrientation == RobotDirection.FORWARD ? DriveTrain.forward(speed) : DriveTrain.reverse(speed);
    	addSequential(new DriveTrainMove(true, 0, 0.1));
    	addSequential(new DriveTrainMove(true, speed, seconds));
    }
}
