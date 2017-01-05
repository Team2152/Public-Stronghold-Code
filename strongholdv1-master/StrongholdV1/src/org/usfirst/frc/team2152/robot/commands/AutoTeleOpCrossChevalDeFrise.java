package org.usfirst.frc.team2152.robot.commands;

import org.usfirst.frc.team2152.robot.subsystems.DriveTrain;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoTeleOpCrossChevalDeFrise extends CommandGroup {
    
    public  AutoTeleOpCrossChevalDeFrise(int chevalSetPoint, double seconds1, double crossSpeed1, double seconds2, double crossSpeed2) {
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
    	addSequential(new DefenseManipulatorReset());
    	addSequential(new DefenseManipulatorMove(chevalSetPoint, 2, false));
    	addSequential(new DriveTrainMove(true, DriveTrain.reverse(crossSpeed1) , seconds1));
    	addParallel(new DriveTrainMove(true, DriveTrain.reverse(crossSpeed2) , seconds2));
    	addParallel(new DefenseManipulatorReset());
    }
}
