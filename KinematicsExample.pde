Tentacle testTentacle; 
Tentacle tentacle2; 
Tentacle tentacle3; 
Tentacle tentacle4; 
KinematicBone bone1;
Octopus testOctopus; 

void setup()
{
   size(600, 600); 
   background(#001b93); 
   testTentacle = new Tentacle(400, 0, 200, 90, 20);
   tentacle2 = new Tentacle(200, 0, 200, 90, 20);
   tentacle3 = new Tentacle(0, 200, 200, 0, 20);
   tentacle4 = new Tentacle(600, 200, 200, 180, 20);
   bone1 = new KinematicBone(300, 300, 100, 3, 0);
   testOctopus = new Octopus(300, 300, 200); 
}

void draw()
{
  clear(); 
  background(#001b93); 
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
