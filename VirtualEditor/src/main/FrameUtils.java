package main;


import javax.swing.JFrame;


/**
 * 
 * @author www.rgagnon.com/javadetails/java-0622.html 
 * @author jack :D 
 *
 */
public class FrameUtils {

  private final static int VIBRATION_LENGTH = 30;
  private final static int VIBRATION_VELOCITY = 10;
  
  private FrameUtils() { }
  
  public static void vibrate(JFrame frame) { 
    try { 
      final int originalX = frame.getLocationOnScreen().x; 
      final int originalY = frame.getLocationOnScreen().y; 
      for(int i = 0; i < VIBRATION_LENGTH; i++) { 
        Thread.sleep(10); 
        frame.setLocation(originalX, originalY + VIBRATION_VELOCITY); 
        Thread.sleep(10); 
        frame.setLocation(originalX, originalY - VIBRATION_VELOCITY);
        Thread.sleep(10); 
        frame.setLocation(originalX + VIBRATION_VELOCITY, originalY);
        Thread.sleep(10); 
        frame.setLocation(originalX, originalY); 
      } 
    } 
    catch (Exception err) { 
      err.printStackTrace(); 
    } 
  }
}