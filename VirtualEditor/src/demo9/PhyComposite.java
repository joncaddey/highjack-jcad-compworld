package demo9;



import java.util.List;


public class PhyComposite extends PhyObject{

	public static PhyObject getPair(float size) {
		PhyObject[] obs = {PhyPolygon.getRightTriangle(size), PhyPolygon.getRightTriangle(size)};//};//, 
		obs[0].centerOfMass = new Vector2f(-2 * size,0);
		obs[0].orientation = 90;
		obs[1].centerOfMass = new Vector2f(2 * size, 0);
		SceneGraphNode sgn = new SceneGraphNode(true);
		for (PhyObject o : obs) {
			SceneGraphNode wrapper = new SceneGraphNode(false);
			//o.renderable.rotation = o.orientation;
			wrapper.addChild(o.renderable);
			o.renderable.rotation = o.orientation;
			wrapper.rotation = 0;
			wrapper.CoMX = o.centerOfMass.x;
			o.renderable.CoMX = 0;
			wrapper.CoMY = o.centerOfMass.y;
			o.renderable.CoMY = 0;
			
			sgn.addChild(wrapper);
		}
		PhyComposite p = new PhyComposite(sgn, obs, size, 0, 0, obs[0].inverseMomentOfInertia / 10);
		return p;
	}
	
	public PhyComposite(SceneGraphNode renderable, PhyObject[] objects, float size, float CoMX, float CoMY, float inverseMomentOfInertia) {
		super();
		this.inverseMomentOfInertia = inverseMomentOfInertia;
		this.renderable = renderable;
		//this.renderable.scale = size;
		velocity = new Vector2f(0, 0);
		this.objects = objects;
		for (PhyObject o : objects) {
			o.velocity = velocity;
			o.position = position;
			o.originalOrientation = o.orientation;
			//o.inverseMomentOfInertia = this.inverseMomentOfInertia;
		}
	}
	public void synchChildren() {
		for (PhyObject o : objects) {
			o.position = position;
			o.orientation = orientation;
			o.clearCaches();
		}
	}
	
}
