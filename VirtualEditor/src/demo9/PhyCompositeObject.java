package demo9;



import java.util.List;


public class PhyCompositeObject extends PhysicsObject{

	public static PhysicsObject getPair(float size) {
		PhysicsObject o = new Circle(size); //
		o.position = new Vector2f(0, 0);
		SceneGraphNode sgn = new SceneGraphNode(true);
		sgn.addChild(o.renderable);
		PhyCompositeObject p = new PhyCompositeObject(sgn, new PhysicsObject[]{o}, size, 0, 0, (float)(Math.pow(size, 4) / 18));
		return p;
	}
	
	public PhyCompositeObject(SceneGraphNode renderable, PhysicsObject[] objects, float size, float CoMX, float CoMY, float momentOfInertia) {
		this.inverseMomentOfInertia = 1f / momentOfInertia;
		this.renderable = renderable;
		this.renderable.scale = size;
		centerOfMass.x = CoMX;
		centerOfMass.y = CoMY;
		this.renderable.CoMX = CoMX;
		this.renderable.CoMY = CoMY;
		this.objects = objects;
	}
	/*/ need a renderable, center of mass, list of phyObjects, moment of inertia, size, 
	
	
	
	public static PhyPolygon getEqTriangle(final float the_size) {
		PhyPolygon r = new PhyPolygon(VERTICES_EQ_TRIANGLE, the_size);
		r.inverseMomentOfInertia = 1 / (float)(Math.pow(the_size, 4) / 18); // TODO math
		r.renderable = r.new Renderable();
		r.renderable.scale = the_size;
		r.centerOfMass.x = r.centerOfMass.y = 0;
		r.renderable.CoMX = r.centerOfMass.x;
		r.renderable.CoMY = r.centerOfMass.y;
		re*/
	
	private static CollisionInfo getCollision(HalfSpace a, PhyCompositeObject b) {
		return null;
	}
}
