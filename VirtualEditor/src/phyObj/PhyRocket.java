package phyObj;

public class PhyRocket extends PhysicsObject { 


	PhyTriangle my_leftWing;
	
	PhyTriangle my_rightWinTriangle;
	
	PhyTriangle my_top;
	
	PhyRectangle my_body;
	
		
	public PhyRocket(){
//		my_leftWing = new PhyTriangle(size);
//		my_rightWinTriangle = new PhyTriangle(size);
//		my_top = new PhyTriangle(size);
		my_body = new PhyRectangle();
	}
	
	
	
	
	
}
