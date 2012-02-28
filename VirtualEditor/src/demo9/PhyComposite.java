package demo9;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;




public class PhyComposite extends PhyObject{
	private static final float BODY_RATIO = (float) 8 / 13;
	public static PhyObject getPair(float size) {
		//PhyObject[] obs = {PhyPolygon.getRightTriangle(size), PhyPolygon.getRightTriangle(size)};//};//, 
		PhyObject[] obs = {new Circle(size), PhyPolygon.getRightTriangle(size)};
		obs[0].centerOfMass = new Vector2f(-2 * size,0);
		//obs[0].orientation = 90;
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
		PhyComposite p = new PhyComposite(sgn, Arrays.asList(obs), size, 0, 0, obs[0].inverseMomentOfInertia / 10);
		return p;
	}
	
	public static PhyObject getRocket(float size) {
		PhyComposite p = new PhyComposite();
		p.inverseMomentOfInertia = 1 / (float)(Math.pow(size, 4) / 18 / 10);
		
		
		PhyObject head = PhyPolygon.getEqTriangle(size);
		head.position.y = Triangle.SIN_60 / 3 * size;
		p.addObject(head);
		
		PhyObject body = PhyPolygon.getSquare(size * BODY_RATIO);
		body.position.y = -BODY_RATIO / 2 * size;
		p.addObject(body);
		
		PhyObject finLeft = PhyPolygon.getEqTriangle(size * .3f);
		finLeft.orientation = radians(90);
		finLeft.position = new Vector2f(size * -.39f, size * -.4f);
		p.addObject(finLeft);
		
		PhyObject finRight = PhyPolygon.getEqTriangle(size * .3f);
		finRight.orientation = radians(-90);
		finRight.position = new Vector2f(size * .39f, size * -.4f);
		p.addObject(finRight);
		
		return p;
	}
	
	
	public void addObject(PhyObject obj) {
		// rendering
		obj.renderable.rotation = (float)(obj.orientation * 180 / Math.PI);
		obj.renderable.CoMX = obj.centerOfMass.x;
		obj.renderable.CoMY = obj.centerOfMass.y;
		SceneGraphNode wrapper = new SceneGraphNode(false);
		wrapper.addChild(obj.renderable);
		wrapper.CoMX = -obj.position.x;
		wrapper.CoMY = -obj.position.y;
		renderable.addChild(wrapper);
		objects.add(obj);
		
		// physics
		obj.originalOrientation = obj.orientation;
		obj.centerOfMass.rotate(obj.orientation);
		obj.centerOfMass.sumScale(obj.position, -1);
	}

	
	

	public PhyComposite(){
		super();
		objects = new ArrayList<PhyObject>();
		renderable = new SceneGraphNode(true);
	}
	
	public PhyComposite(SceneGraphNode renderable, List<PhyObject> objects, float size, float CoMX, float CoMY, float inverseMomentOfInertia) {
		super();
		this.inverseMomentOfInertia = inverseMomentOfInertia;
		this.renderable = renderable;
		//this.renderable.scale = size;
		this.objects = new ArrayList(objects);
		for (PhyObject o : objects) {
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
	
	
	
	
	public static float radians(float degrees) {
		return (float)(degrees / 180 * Math.PI);
	}
}
