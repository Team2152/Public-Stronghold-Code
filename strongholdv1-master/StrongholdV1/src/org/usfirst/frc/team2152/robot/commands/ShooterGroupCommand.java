package org.usfirst.frc.team2152.robot.commands;

import org.usfirst.frc.team2152.robot.Robot;
import org.usfirst.frc.team2152.robot.RobotDirection;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class ShooterGroupCommand extends CommandGroup {
    
    public  ShooterGroupCommand(Joystick js, int buttonId, RobotDirection dir) {
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
    	
    	if (dir == RobotDirection.LOAD) {
    		addParallel(new ShooterLauncherUse(js, buttonId, RobotDirection.LOAD));
    	} else if (dir == RobotDirection.LAUNCH) {
    		addParallel(new ShooterLauncherUse(js, buttonId, RobotDirection.LAUNCH));
    	} else {
    		Robot.logger.console("Unknown Direction: command canceled");
    	}
    }
}
