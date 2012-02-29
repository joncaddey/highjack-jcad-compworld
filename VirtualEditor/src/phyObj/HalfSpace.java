package phyObj;
public class HalfSpace extends PhyObject {
	// JON: helpful: http://mathworld.wolfram.com/Point-LineDistance2-Dimensional.html
	Vector2f normal;
	// Right-hand side of the plane equation: Ax + By = C
	float intercept;

	public void setPosition(float x, float y) {
		position.x = x;
		position.y = y;
		intercept = position.dot(this.normal);
	}
	
	public HalfSpace(Vector2f point, Vector2f normal) {
		inverseMass = 0;
		this.normal = new Vector2f(normal);
		this.normal.normalize();
		position = new Vector2f(point);
		intercept = position.dot(this.normal);
	}
}