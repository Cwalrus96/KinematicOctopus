import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class KinematicsExample extends PApplet {

Tentacle testTentacle; 
Tentacle tentacle2; 
Tentacle tentacle3; 
Tentacle tentacle4; 
KinematicBone bone1;
Octopus testOctopus; 

public void setup()
{
    
   background(0xff001b93); 
   testTentacle = new Tentacle(400, 0, 200, 90, 20);
   tentacle2 = new Tentacle(200, 0, 200, 90, 20);
   tentacle3 = new Tentacle(0, 200, 200, 0, 20);
   tentacle4 = new Tentacle(600, 200, 200, 180, 20);
   bone1 = new KinematicBone(300, 300, 100, 3, 0);
   testOctopus = new Octopus(300, 300, 200); 
}

public void draw()
{
  clear(); 
  background(0xff001b93); 
  /**testTentacle.updateInverseKinematics(mouseX, mouseY); 
  testTentacle.drawTentacle(); 
  tentacle2.updateInverseKinematics(mouseX, mouseY);
  tentacle3.updateInverseKinematics(mouseX, mouseY);
  tentacle4.updateInverseKinematics(mouseX, mouseY);
  tentacle2.drawTentacle(); 
  tentacle3.drawTentacle(); 
  tentacle4.drawTentacle(); 
  bone1.updateInverseKinematics(mouseX, mouseY); 
  bone1.drawBone(); 
  /**
  KinematicBone bone2 = new KinematicBone(bone1, 100, 2, 0); 
  KinematicBone bone3 = new KinematicBone(bone2, 100, 1, 0); 
  bone1.drawBone(); 
  bone2.drawBone(); 
  bone3.drawBone(); 
  **/
  testOctopus.drawOctopus(); 
  if(mousePressed) 
  {
    testOctopus.updateInverseKinematics(mouseX, mouseY);
  }
  else testOctopus.updateKinematics(); 
}

/**The KinematicBone class forms the basic 2D segments that wil make up the Octopus's tentacles.
These bones are line segments that are linked to one another using a doubly-linked list, with references 
to both a parent and a child. These KinematicBones basically exist in 2 states - Kinematics mode and 
Inverse Kinematics mode. In Kinematics mode all child 
bones will react based on the movement of their parents. In Inverse Kinematics mode the bones will try to 
align their positions and rotations based on a target, and each bone's position will depend on the position 
of their child. 
**/

class KinematicBone {
  
  KinematicBone parent; 
  KinematicBone child; 
  
  //This point should remain fixed relative to the position of the Parent bone. 
  //If the Parent bone is null, this position should remain absolutely fixed
  PVector anchorPoint; 
  private PVector endPoint; 
  float boneLength;
  /**Need to keep track of 2 different angles - an absolute (world) angle, and
  Relative (based on parent) angle. Absolute angle should be calculated based on
  the relative angle and the parent **/
  float angleAbs;
  float angleRel; 
  float strokeWeight; 
  //angleBase is the initial "base" angle relative to the parent
  private float angleBase; 
  static final int speed = 2; 
  
  /**This function will create a new KinematicBone with the given parameters.
  The initialAngle parameter is relative to the parent, and the absolute
  Angle (and the endpoint) will be calculated based on these parameters. 
  Because this constructor does not take a parent as an argument the parent is 
  null, and the absolute and relative angles will be identical. Angles should be
  given in degrees (conversion to radians will be applied later)**/
  KinematicBone(float x, float y, float len, float weight, float initialAngle)
  {
     anchorPoint = new PVector(); 
     anchorPoint.x = x; 
     anchorPoint.y = y; 
     boneLength = len; 
     strokeWeight = weight;
     angleRel = initialAngle; 
     angleAbs = angleRel; 
     endPoint = new PVector(); 
     calculateEndpoint(); 
     parent = null; 
     child = null; 
     angleBase = angleAbs; 
  }
  
  /**This constructor will create a new KinematicBone that is anchored to the
  bone provided as the parent **/
  KinematicBone(KinematicBone newParent, float len, float weight, float initialAngle) 
  {
      parent = newParent; 
      parent.child = this; 
      anchorPoint = new PVector(); 
      anchorPoint.x = parent.endPoint.x; 
      anchorPoint.y = parent.endPoint.y; 
      boneLength = len; 
      strokeWeight = weight; 
      angleBase = initialAngle;
      angleRel = 0; 
      calculateAbsoluteAngle();
      endPoint = new PVector(); 
      calculateEndpoint();  
  }
  
  
  //This function will calculate the absolute position of the bone relative to its parent. 
  public void updateKinematics() {  
    calculateAbsoluteAngle();   
    calculateEndpoint(); 
    if(child != null) {
       child.anchorPoint = endPoint.copy(); 
       child.updateKinematics();  
    }
  }
  
  //This function will adjust the position and angle of the bone based on the position of its child
  public void updateInverseKinematics(float targetX, float targetY) {
     PVector target = new PVector(targetX, targetY); 
     PVector dir = PVector.sub(target, anchorPoint); 
     angleAbs = degrees(dir.heading());
     if(child != null) {
       dir.setMag(boneLength); 
       dir.mult(-1); 
       anchorPoint = PVector.add(target, dir);
     }
     else {
       dir.setMag(speed); 
       anchorPoint = PVector.add(anchorPoint, dir); 
     }
     calculateEndpoint(); 
      
  }
  
  //This function will draw the bone to the screen
  public void drawBone() 
  {
      //sstroke(255); 
      strokeWeight(strokeWeight); 
      line(anchorPoint.x, anchorPoint.y, endPoint.x, endPoint.y); 
  }
  
  public void calculateAbsoluteAngle()
  {
     if(angleRel > 15) 
     {
        angleRel = 15;  
     }
     if(angleRel < -15)
     {
        angleRel = -15;  
     }
     if(parent != null) {
         angleAbs = parent.angleAbs + angleRel + angleBase;
     }
     //If bone has no parent, absolute angle is the "base" angle relative to the world, does not change
     else angleAbs = angleBase + angleRel;
  }
  
  public void calculateEndpoint()
  {
      endPoint.x = anchorPoint.x + boneLength * cos(radians(angleAbs)); 
      endPoint.y = anchorPoint.y + boneLength * sin(radians(angleAbs));
      return; 
  }
    
    
  
}

//This class will create an octopus with 8 kinematic tentacles arrayed around it
class Octopus {
  float size; 
  float centerX; 
  float centerY; 
  Tentacle[] tentacles; 
  
  //These parameters will determine where the octopus is drawn, and how large
  Octopus(float centerX, float centerY, float size)
  {
      this.centerX = centerX; 
      this.centerY = centerY; 
      this.size = size; 
      tentacles = new Tentacle[8];
      float currentAngle = 0; 
      for(int i = 0; i < 8; i++)
      {   
        Tentacle t = new Tentacle(centerX + ((size / 4) * cos(radians(currentAngle))), centerY + ((size / 4) * sin(radians(currentAngle))), size, currentAngle, 10); 
        tentacles[i] = t; 
        currentAngle += (180 / 8); 
      }
  }
  
  public void drawOctopus() 
  {
     stroke(0xffff5733); 
     fill(0xffff5733); 
     circle(centerX, centerY, size / 2);
     circle(centerX, centerY - (size / 3), size - (size / 3)); 
     fill(255);
     PVector leftEyeCenter = new PVector(); 
     PVector rightEyeCenter = new PVector(); 
     leftEyeCenter.x = centerX - (size / 8); 
     leftEyeCenter.y = centerY + (size / 10) - (size / 3); 
     circle(leftEyeCenter.x, leftEyeCenter.y, size / 5); 
     rightEyeCenter.x = centerX + (size / 8); 
     rightEyeCenter.y = centerY + (size / 10) - (size / 3); 
     circle(rightEyeCenter.x, rightEyeCenter.y, size / 5); 
     PVector mousePos = new PVector(mouseX, mouseY); 
     float leftEyeAngle = PVector.sub(leftEyeCenter, mousePos).heading(); 
     float rightEyeAngle = PVector.sub(rightEyeCenter, mousePos).heading(); 
     stroke(0xff1fe464); 
     fill(0xff1fe464); 
     circle(leftEyeCenter.x - ( (size / 20) * cos(leftEyeAngle)), leftEyeCenter.y - ((size / 20) * sin(leftEyeAngle)), size / 10); 
     circle(rightEyeCenter.x - ((size / 20) * cos(rightEyeAngle)), rightEyeCenter.y - ((size / 20) * sin(rightEyeAngle)), size / 10); 
     stroke(0xffff5733); 
     fill(0xffff5733);
     for(int i = 0; i < 8; i++)
     {
         tentacles[i].drawTentacle(); 
     }
    
  }
  
  public void updateKinematics()
  {
     for(int i = 0; i < 8; i++)
     {
        tentacles[i].updateKinematics();  
     }
    
  }
  
  public void updateInverseKinematics(float x, float y)
  {
    for(int i = 0; i < 8; i++)
    {
       PVector target = new PVector(x, y); 
       float dist = PVector.sub(tentacles[i].root.anchorPoint, target).mag(); 
       if(dist < (tentacles[i].totalLength * 1.5f))
       {
          tentacles[i].updateInverseKinematics(x, y);  
       }
       else tentacles[i].updateKinematics(); 
     }
  }
  
}


/**This class will make a tentacle out of Kinematic Bones. The tentacle will "sway with the current"
when in Kinematics mode, and will reach for the mouse in Inverse Kinematics mode **/

class Tentacle {
  
  /**root is the bone at the base of the tentacle (parent of all others). end is the bone at the tip of the tentacle**/
  KinematicBone root; 
  KinematicBone tip; 
  int segments; 
  float totalLength;
  //These parameters will be used to vary the motion of the tentacles, and make the octopus seem more lifelike;  
  //InitialPeriod can be anywhere from 0.8 to 1.2
  float initialPeriod; 
  //InitialOffset can be anywhere from 0 to 2 * PI
  float phaseOffset; 
  
  /**This constructor will produce a new tentacle with the desired anchor position, angle, total length, and number of segments**/
  Tentacle(float x, float y, float totalLength, float initialAngle, int segments) 
  {
    float segmentLength = totalLength / segments;  
    root = new KinematicBone(x, y, segmentLength, segments, initialAngle);
    KinematicBone current = root;
    float angle; 
    for(int i = 1; i < segments; i++)
    {
       angle = random(60) - 30;
       KinematicBone newBone = new KinematicBone(current, segmentLength, segments - (i), angle); 
       current = newBone; 
    }
    tip = current; 
    this.segments = segments; 
    this.totalLength = totalLength;
    //the random portion should give a number between 0.2 and -0.2
    initialPeriod = 1 + ((random(40) - 20) / 100);
    //This will roughly give a number between 0 and 2 * PI (~6.28)
    phaseOffset = (random(628) / 100); 
  }
  
    
  /**This function should draw all of the KinematicBones that make up the tentacle **/
  public void drawTentacle() 
  {
    KinematicBone current = root; 
    while(current.child != null) 
    {
       current.drawBone(); 
       current = current.child; 
    }
    current.drawBone(); 
    
  }
  
  /**This function will update the angles of all of the tentacle segments based on a sine wave to produce a nice swaying motion **/
  public void updateKinematics()
  {
      float range = 90 / segments; 
      KinematicBone current = root; 
      int time = millis() / 15;
      //offset prevents every segment of the tentacle from being perfectly in-sync  
      while(current.child != null) 
      { 
         current.angleRel = range *  sin(radians( initialPeriod * (time + phaseOffset))); 
         current.updateKinematics(); 
         current = current.child; 
      }
      current.angleRel = range * sin(radians(initialPeriod * (time + phaseOffset)));
      current.updateKinematics(); 
  }
  
  /**This function will make the tip of the tentacle follow the target if the total distance from the base of the tentacle 
  to the target is less than the tentacle's total length **/ 
  public void updateInverseKinematics(float targetX, float targetY)
  {
     PVector target = new PVector(targetX, targetY); 
      KinematicBone current = tip;
      //Get all segments to follow the child segment
      while(current.parent != null) 
      {
         current.updateInverseKinematics(target.x, target.y); 
         target = current.anchorPoint.copy();
         if(current.child != null) {
            current.child.angleRel = 0; 
            current.child.angleBase = current.child.angleAbs - current.angleAbs; 
         }
         current = current.parent; 
      }
      //reposition all segments so that the tentacle remains fixed to it's base
       current.angleBase = current.angleAbs;
       PVector savedAnchor = current.anchorPoint.copy(); 
       current.updateInverseKinematics(target.x, target.y);
       current.anchorPoint = savedAnchor;
       current.calculateEndpoint(); 
       while(current.child != null)
       {
           current = current.child; 
           current.anchorPoint = current.parent.endPoint; 
           current.calculateEndpoint(); 
       }
       current.calculateEndpoint(); 
       if(current.child != null) {
          current.child.angleRel = 0; 
          current.child.angleBase = current.child.angleAbs - current.angleAbs; 
       }  
       phaseOffset = -1 * (millis() / 15); 
  }
  
  
}
  public void settings() {  size(600, 600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "KinematicsExample" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
