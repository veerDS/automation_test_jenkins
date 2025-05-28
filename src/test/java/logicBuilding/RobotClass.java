package logicBuilding;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class RobotClass {

	public static void main(String[] args) throws Exception {
		
		String fileCommand="C:\\Users\\Priyanka\\MyFile.exe";
		
		Runtime run=Runtime.getRuntime();
		
		run.exec(fileCommand);
		
		
		
		Robot robot=new Robot();
		
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);

	}

}
