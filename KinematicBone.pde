
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
  void updateKinematics() {  
    calculateAbsoluteAngle();   
    calculateEndpoint(); 
    if(child != null) {
       child.anchorPoint = endPoint.copy(); 
       child.updateKinematics();  
    }
  }
  
  //This function will adjust the position and angle of the bone based on the position of its child
  void updateInverseKinematics(float targetX, float targetY) {
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
  void drawBone() 
  {
      //sstroke(255); 
      strokeWeight(strokeWeight); 
      line(anchorPoint.x, anchorPoint.y, endPoint.x, endPoint.y); 
  }
  
  void calculateAbsoluteAngle()
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
  
  void calculateEndpoint()
  {
      endPoint.x = anchorPoint.x + boneLength * cos(radians(angleAbs)); 
      endPoint.y = anchorPoint.y + boneLength * sin(radians(angleAbs));
      return; 
  }
    
    
  
}
