/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package team1876.robot;



import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Timer;
import team1876.io.DSFace;
import team1876.drive.Drive;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotBase extends SimpleRobot {
    
    DSFace   dsf;
    Drive    drive;
    
 
  public RobotBase () {
        drive = new Drive();
        dsf = new DSFace ();
    }

    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() {
        
    }

   /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        
        boolean freeze = false;
        while (true && isOperatorControl() && isEnabled()) {
            // There is only one class in this robot
            drive.update();
            
            //This sends key informtion to the display so
            //you can see what is happening.  Be sure to format
            //your output to prevent ghosting, meaning old text
            //Example:
            // #1:  Status:  false
            // #2:  Status:  true
            // The second output will actually display:
            //      Status:  truee 
            // (the old printing)
           
           /* * This block is used to test/display controller settings 
           dsf.println(1, 1, "X: " + drive.joystick.getX());
           dsf.println(2, 1, "Y: " + drive.joystick.getY());
           dsf.println(3, 1, "Z: " + drive.joystick.getRawAxis(4));
           // */
            
           // This block is used to displayed smoothed values
           /* 
           //DecimalFormat df;
           //String msg = String.format ("%2.2f",drive.fr);
           dsf.println(1, 1, "F<>R: " + drive.fr +"  ");
           dsf.println(2, 1, "L<>R: " + drive.lr + "  ");
           dsf.println(3, 1, "CCW <> CW: " + drive.ccw + "  ");
           dsf.println(4, 1, "Y: " + drive.joystick.getX() + "  ");
          // */
          /*       
           dsf.println(1, 1, "Mag " + drive.mag + " |");
           dsf.println(2, 1, "Dir " + drive.dir + " |");
           dsf.println(3, 1, "CCW <> CW: " + drive.ccw + "  ");
           dsf.println(4, 1, "Y: " + drive.joystick.getX() + "  ");
          / / */
           /*
           dsf.println(1, 1, "P " + drive.p + " |");
           dsf.println(2, 1, "S " + drive.s + " |");
           dsf.println(3, 1, "dir: " + drive.dir + " |");
           dsf.println(4, 1, "Y: " + drive.joystick.getX() + "  ");
           / */
        
               // *
           dsf.println(1, 1, "LF " + drive.speed_LF + " |");
           dsf.println(2, 1, "RF " + drive.speed_RF + " |");
           dsf.println(3, 1, "LR " + drive.speed_LR + " |");
           dsf.println(4, 1, "RR " + drive.speed_RR + " |");
           dsf.println(5, 1, "dir: " + drive.dir + " |");

        
           dsf.update();
                
   
            
            Timer.delay(0.01);
        }
    }   
    
}