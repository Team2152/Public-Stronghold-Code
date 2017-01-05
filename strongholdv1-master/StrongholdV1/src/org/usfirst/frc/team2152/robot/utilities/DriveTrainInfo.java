package org.usfirst.frc.team2152.robot.utilities;

import org.usfirst.frc.team2152.robot.Robot;
// test
public class DriveTrainInfo {

	private boolean reverseMode;

	public DriveTrainInfo() {
		reverseMode = false;
	}

	public void setReverseMode(boolean value) {
		reverseMode = value;
	}

	public boolean getReverseMode() {
		return reverseMode;
	}

	public boolean toggle() {
		if (reverseMode == true) {
			reverseMode = false;
		} else {
			reverseMode = true;
		}
		Robot.logger.console("Reverse Mode = " + reverseMode);

		return reverseMode;

	}

}
