
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
  
  void drawOctopus() 
  {
     stroke(#ff5733); 
     fill(#ff5733); 
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
     stroke(#1fe464); 
     fill(#1fe464); 
     circle(leftEyeCenter.x - ( (size / 20) * cos(leftEyeAngle)), leftEyeCenter.y - ((size / 20) * sin(leftEyeAngle)), size / 10); 
     circle(rightEyeCenter.x - ((size / 20) * cos(rightEyeAngle)), rightEyeCenter.y - ((size / 20) * sin(rightEyeAngle)), size / 10); 
     stroke(#ff5733); 
     fill(#ff5733);
     for(int i = 0; i < 8; i++)
     {
         tentacles[i].drawTentacle(); 
     }
    
  }
  
  void updateKinematics()
  {
     for(int i = 0; i < 8; i++)
     {
        tentacles[i].updateKinematics();  
     }
    
  }
  
  void updateInverseKinematics(float x, float y)
  {
    for(int i = 0; i < 8; i++)
    {
       PVector target = new PVector(x, y); 
       float dist = PVector.sub(tentacles[i].root.anchorPoint, target).mag(); 
       if(dist < (tentacles[i].totalLength * 1.5))
       {
          tentacles[i].updateInverseKinematics(x, y);  
       }
       else tentacles[i].updateKinematics(); 
     }
  }
  
}
