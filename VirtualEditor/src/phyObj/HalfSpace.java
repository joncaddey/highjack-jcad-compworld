package phyObj;
public class HalfSpace extends PhysicsObject {
	// JON: helpful: http://mathworld.wolfram.com/Point-LineDistance2-Dimensional.html
	Vector2f normal;
	// Right-hand side of the plane equation: Ax + By = C
	float intercept;
	
	public HalfSpace(Vector2f point, Vector2f normal) {
		inverseMass = 0;
		this.normal = new Vector2f(normal);
		this.normal.normalize();
		intercept = point.dot(this.normal);
	}
}