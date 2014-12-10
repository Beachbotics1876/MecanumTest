// 1.1
//  - handled cae where magnitude was 0
//  - switch joysticks to take raw data
// 1.0 Created



package team1876.drive;




import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import com.sun.squawk.util.MathUtils;



/**
 *
 * @author Andrew Rowles
 */
public class Drive {
    
    public final int JAGUAR_LF = 3;
    public final int JAGUAR_RF = 4;
    public final int JAGUAR_LR = 1;
    public final int JAGUAR_RR = 2;
    
    
    public final int JOYSTICK_ID = 1;
 
    public Jaguar  jaguar_lf;
    public Jaguar  jaguar_rf;
    public Jaguar  jaguar_lr;
    public Jaguar  jaguar_rr;
    
    public Joystick joystick;
    
    //Intermediate values 
    //- Sign correct
    //- Smoothed and scaled
    //- Limited to range
    public double fr = 0;   //Forward (+1) to Reverse (-1)
    public double lr = 0;   //Left (+1) to Right (-1)
    public double ccw = 0;  // Spin CCW (+1) to CW (-1) 
    
    //Mecanum directions
    public double mag = 0;   //Amount of power 0-1
    public double dir = 0;   //Dir 0-2PI (0 is straight ahead)
    public double rot = 0;   
    
    public final int STATE_IDLE = 0;
    public final int STATE_SHOOTING = 1;
    public final int STATE_RETURNING = 2;   //Returning to idle position
    public final int STATE_MANUAL = 3;      //Manual override in effect
    
    public int state = STATE_IDLE;
    //Dead zone settings
    public double dz_min  = 0.035;  //Anything smaller is 0
    public double dz_max = 0.970;  //Anything larger is   1
    
    //Vector and Angle adjusted intermediates
    public double p = 0;
    public double s = 0;
    
    //Speed settings
    public double speed_LF  = 0.0;
    public double speed_RF  = 0.0;
    public double speed_LR  = 0.0;
    public double speed_RR  = 0.0;
    
    public double SPEED_IDLE  = 0.0;
    public double SPEED_LAUNCH = 0.5;
    public double SPEED_RETURN = -0.1;
    
    //Position Settings - pay attention to direction
    public int    MAX_POSITION = 90;  //90 degrees
    public int    MIN_POSITION =  0;
    
    public final double SHOOTER_SENSITIVITY = 1;
    
   // public double speed = 0;   //This is the ACTUAL SPEED applied to motor 
    public final double POWER_INCREMENT = 0.05;
    
    public boolean PRESSED_UP_POWER = false;
    public boolean PRESSED_DOWN_POWER = false;
     
    /* Axis IDs */
    private static final int A_LEFT_X = 1;
    private static final int A_LEFT_Y = 2;
    private static final int A_TRIGGER = 3;
    private static final int A_RIGHT_X = 4;
    private static final int A_RIGHT_Y = 5;
    
  
    public Drive () {
        jaguar_lf   = new Jaguar(JAGUAR_LF);
        jaguar_rf   = new Jaguar(JAGUAR_RF);
        jaguar_lr   = new Jaguar(JAGUAR_LR);
        jaguar_rr   = new Jaguar(JAGUAR_RR);
        joystick = new Joystick (JOYSTICK_ID);
        
    }
    
    public void update ()
        {
        //Load in raw values
        double x = joystick.getRawAxis (A_LEFT_X);
        double y = joystick.getRawAxis (A_LEFT_Y);
        double z = joystick.getRawAxis (A_RIGHT_X);
        
        //Call the smooth function to adjust them
        //the range is -1.0 to 1.0, includes the dead zone
        //and more precise at the low end
        fr  = Smooth (-y);
        lr  = Smooth (-x);
        ccw = Smooth (-z);
        
        //Convert the smoothed values into a vector
        //This is always positive, range 0-1.0
        mag =  Math.sqrt(fr*fr + lr*lr);
        if (mag > 1.0) mag=1.0;
        
        //Calculate angle (in radians PI to -PI 
        // 0 is straight ahead
        // Note: For this to work, lr, fr cannot both be 0
        // This is also the same as mag=0
        dir = 0;
        if (mag > 0) dir = MathUtils.atan2(lr, fr);
        
        
        //Convert to primary and secondary wheels
        //The wheels spin forward or reverse depending on the 
        //direction.  Note that diagonal wheels rotate same way
        p = mag;
        s = mag * Math.cos(2*dir);
        
        ApplyDirection (-s,p);
        if (dir >= Math.PI/2)       {ApplyDirection(-p,-s);}
        else if (dir >= 0.0)        {ApplyDirection( s, p);}
        else if (dir >= -Math.PI/2) {ApplyDirection( p, s);}
        
        //The last step is to calculate rotation.  To make this work
        //smoothly there are a few factors to consider
        // - If standing still, rotate quickly.  
        // - If moving fast in a particular direction, rotate more slowly
        // - To rotate CCW, speed up right wheels, slow down left wheels
        // - To rotate CW, slow down right wheels, speed up left wheels
        
        double spin_effect = (1.0 - 0.5 * mag);  //Slower when moving fast
        spin_effect *= ccw;                      //Postive ccw, negative cw
        
        //Add effect to each wheel
        speed_LF = AddEffect (speed_LF, -spin_effect); //Slow down left wheels
        speed_LR = AddEffect (speed_LR, -spin_effect); //Slow down left wheels
        
        speed_RF = AddEffect (speed_RF,  spin_effect); //Speed up right wheels 
        speed_RR = AddEffect (speed_RR,  spin_effect); 
        
        // Apply to CIM motors
        // Forward is Positive (Left Side)
        // Forward is Negative (Right Side)
        jaguar_lf.set (speed_LF);
        jaguar_rf.set (-speed_RF);
        jaguar_lr.set (speed_LR);
        jaguar_rr.set (-speed_RR);
        }        

    //Apply spin is used to set targets for cross pattern
    //At this point, the diagonal wheels move the same amount
    //That is, there is no rotation
    public void ApplyDirection (double lf, double rf)
    {
        speed_LF = lf;
        speed_RR = lf;
        speed_RF = rf;
        speed_LR = rf;
  
    }
    
    public void ActivateMotor (double new_speed)
    {
     //   speed = new_speed;
     //   jaguar.set (speed);
        }
        
       
    //It is a good idea to convert user actions into simple functions.
    //Why?  Because sometimes switches return 0 when pressed, also
    //to use the same functions in auto mode makes it easier to override
    //the actual value
    public boolean IsTriggerPressed () 
    {
        boolean ret_val = false;
        if (joystick.getTrigger()) ret_val = true;
        return ret_val;
    }
  public boolean IsManualPressed () 
    {
        boolean ret_val = false;
   //     if (joystick.getRawButton(BUTTON_MANUAL)) ret_val = true;
        return ret_val;
    }
   
  //Manual mode - read the setting from the joystick
  //return positive to move forward
  public double GetSpeed ()
  {
        double ret_val;
        ret_val = -joystick.getY ();
        return ret_val;
  }
  //Given a Raw (but correctly signed value)
  // return the smoothed version
  public double Smooth (double raw)
  {
  double ret_val;
  //If raw value is in the dead-zones, then return
  //the maz value
  if ((raw <= dz_min) && (raw >= -dz_min))  return 0.0;
  if (raw >= dz_max) return 1.0;
  if (raw <= -dz_max) return -1.0;
  
  //In the power band, so convert it
  double X = Math.abs(raw);
  ret_val = (X-dz_min)/(dz_max-dz_min);
  ret_val *= ret_val;
  //Set sign 
  if (raw < 0) ret_val = -ret_val;
  
  return ret_val;
  }
 public double AddEffect (double src, double bias)
 {
     src += bias;
     if (src < -1.0) src = -1.0;
     if (src > 1.0) src = 1.0;
     return src; 
 }
}