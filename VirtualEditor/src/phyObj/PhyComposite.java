package phyObj;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.Rocket;
import main.SceneGraphNode;
import main.Triangle;




public class PhyComposite extends PhyObject{
	private static final float BODY_RATIO = (float) 8 / 13;
	private final ArrayList<Vector2f> positions;
	private final ArrayList<Float> sizes;
	
	
	
	
	public static PhyObject getRocket(float size) {
		PhyComposite p = new PhyComposite();
		//p.inverseMomentOfInertia = 1 / (float)(Math.pow(size, 4) / 18 / 10);
		
		PhyObject head = PhyPolygon.getEqTriangle(1);
		head.position.y = Triangle.SIN_60 / 3;
		p.addObject(head);
		
		PhyObject body = PhyPolygon.getSquare(BODY_RATIO);
		body.position.y = -BODY_RATIO / 2;
		p.addObject(body);
		
		PhyObject finLeft = PhyPolygon.getEqTriangle(.3f);
		finLeft.orientation = radians(90);
		finLeft.position = new Vector2f(-.39f, -.4f);
		p.addObject(finLeft);
		
		PhyObject finRight = PhyPolygon.getEqTriangle(.3f);
		finRight.orientation = radians(-90);
		finRight.position = new Vector2f(.39f, -.4f);
		p.addObject(finRight);
		
		p.renderable = new Rocket(true);
		p.setSize(5);

		p.setSize(size);
		return p;
	}
	
	
	public void addObject(PhyObject obj) {
		if (obj instanceof PhyComposite) {
			throw new IllegalArgumentException("A composite in a composite?  You must go deeper!");
		}
		
		// rendering
		obj.renderable.rotation = (float)(obj.orientation * 180 / Math.PI);
		obj.renderable.CoMX = obj.centerOfMass.x;
		obj.renderable.CoMY = obj.centerOfMass.y;
		SceneGraphNode wrapper = new SceneGraphNode(false);
		wrapper.addChild(obj.renderable);
		wrapper.CoMX = -obj.position.x;
		wrapper.CoMY = -obj.position.y;
		renderable.addChild(wrapper);
		
		
		// physics
		obj.originalOrientation = obj.orientation;
		obj.centerOfMass.rotate(obj.orientation);
		obj.centerOfMass.sumScale(obj.position, -1);
		objects.add(obj);
		positions.add(new Vector2f(obj.centerOfMass));
		sizes.add(obj.size);
	}

	
	

	public PhyComposite(){
		super();
		positions = new ArrayList<Vector2f>();
		sizes = new ArrayList<Float>();
		objects = new ArrayList<PhyObject>();
		renderable = new SceneGraphNode(true);
	}
	
	
	
	public void setSize(float the_size) {
		super.setSize(the_size);
		inverseMomentOfInertia = the_size / (float)(Math.PI * Math.pow(the_size, 4) / 4) * inverseMass; // FUCKFUCKFUCK
		for (int i = 0; i < objects.size(); i++) {
			objects.get(i).size = sizes.get(i) * the_size;
			Vector2f tmp = new Vector2f(positions.get(i));
			tmp.scale(the_size);
			objects.get(i).centerOfMass = tmp;
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
