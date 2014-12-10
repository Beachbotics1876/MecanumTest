package team1876.io;

import edu.wpi.first.wpilibj.DriverStationLCD;

/**
 *
 * @author Andrew Rowles
 */
public class DSFace {
    
    public void println (int i, int startingColumn, String msg) {
        DriverStationLCD.Line l;
        
        switch(i) {
            case 1: l = DriverStationLCD.Line.kUser1; break;
            case 2: l = DriverStationLCD.Line.kUser2; break;
            case 3: l = DriverStationLCD.Line.kUser3; break;
            case 4: l = DriverStationLCD.Line.kUser4; break;
            case 5: l = DriverStationLCD.Line.kUser5; break;
            case 6: l = DriverStationLCD.Line.kUser6; break;
            default: l = DriverStationLCD.Line.kUser1; break;
        };
        
        DriverStationLCD.getInstance().println(l, startingColumn, msg);
    }
    
    public void update () {
        DriverStationLCD.getInstance().updateLCD();
    }
}
