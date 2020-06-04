

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
  void drawTentacle() 
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
  void updateKinematics()
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
  void updateInverseKinematics(float targetX, float targetY)
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
