package demo9;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;




public class PhyComposite extends PhyObject{

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
		PhyObject obj = PhyPolygon.getEqTriangle(size);
		obj.position.y = Triangle.SIN_60 / 3;
		p.addObject(obj);
		return p;
	}
	
	
	public void addObject(PhyObject obj) {
	
	}


	public PhyComposite(){}; // TODO
	public PhyComposite(SceneGraphNode renderable, List<PhyObject> objects, float size, float CoMX, float CoMY, float inverseMomentOfInertia) {
		super();
		this.inverseMomentOfInertia = inverseMomentOfInertia;
		this.renderable = renderable;
		//this.renderable.scale = size;
		velocity = new Vector2f(0, 0);
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
	
	
	
	/*/
	 	super(pickable);
		Triangle head = new Triangle(false);
		head.translateY = Triangle.SIN_60 / 3;
		addChild(head);
		Rectangle body = new Rectangle(false);
		body.scale = -BODY_RATIO;
		body.translateY = -BODY_RATIO / 2;
		body.rotation = 90;
		addChild(body);
		
		Triangle finleft = new Triangle(false);
		finleft.translateX = -.39f;
		finleft.translateY = -.4f;
		finleft.scale = .3f;
		finleft.rotation = 90f;
		addChild(finleft);
		
		Triangle finRight = new Triangle(false);
		finRight.translateX = .39f;
		finRight.translateY = -.4f;
		finRight.scale = .3f;
		finRight.rotation = 30f;
		addChild(finRight);
	
	 //*/
	
}
