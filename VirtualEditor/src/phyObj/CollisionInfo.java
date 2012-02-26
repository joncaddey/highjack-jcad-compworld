package phyObj;

public class CollisionInfo {
	// where A (calling object) first would have made contact
	Vector2f positionA;
	
	// where B first would have made contact
	Vector2f positionB;
	
	// normal vector pointing from A to B TODO clarify
	Vector2f normal;
	
	// how far the objects have gone into each other
	float depth;
}
