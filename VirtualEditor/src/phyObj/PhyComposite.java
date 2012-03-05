package phyObj;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import main.Circle;
import main.Rocket;
import main.SceneGraphNode;
import main.Triangle;




public class PhyComposite extends PhyObject{
	private static final float BODY_RATIO = (float) 8 / 13;
	private final ArrayList<Vector2f> positions;
	private final ArrayList<Float> sizes;
	
	
	
	
	public static PhyObject getRocket(float size) {
		PhyComposite p = new PhyComposite();
		
		int variability = 20;
		PhyPolygon finLeft = PhyPolygon.getEqTriangle(.3f);
		finLeft.density = .1f;
		finLeft.renderable.setRGBi(
				93 + (int)(Math.random() * variability),
				0 + (int)(Math.random() * variability),
				131 + (int)(Math.random() * variability));
		finLeft.orientation = radians(90);
		finLeft.position = new Vector2f(-.39f, -.4f);
		// added after finRight
		
		PhyPolygon finRight = PhyPolygon.getEqTriangle(.3f);
		finRight.density = .1f;
		finRight.renderable.red = finLeft.renderable.red;
		finRight.renderable.green = finLeft.renderable.green;
		finRight.renderable.blue = finLeft.renderable.blue;
		finRight.orientation = radians(-90);
		finRight.position = new Vector2f(.39f, -.4f);
		p.addObject(finLeft);
		p.addObject(finRight);
		
		PhyPolygon body = PhyPolygon.getSquare(BODY_RATIO);
		body.density = 1;
		body.renderable.setRGBi(
				64 + (int)(Math.random() * variability),
				89 + (int)(Math.random() * variability),
				122 + (int)(Math.random() * variability));
		body.position.y = -BODY_RATIO / 2;
		p.addObject(body);
		
		PhyPolygon head = PhyPolygon.getEqTriangle(1);
		head.density = 3;
		head.renderable.setRGBi(
				218 + (int)(Math.random() * variability),
				8 + (int)(Math.random() * variability),
				16 + (int)(Math.random() * variability));
		head.position.y = Triangle.SIN_60 / 3 - .01f;
		p.addObject(head);
		
		SceneGraphNode flame = new Triangle(false);
		flame.rotation = 180;
		flame.scale = .25f;
		flame.translateY = -.8f;
		flame.setRGBf(1, .6f, 0);
		p.renderable.addChild(flame);
		
		flame = new Triangle(false);
		flame.rotation = 180;
		flame.scale = .25f;
		flame.translateY = -.7f;
		flame.translateX = -.1f;
		flame.setRGBf(1, .6f, 0);
		p.renderable.addChild(flame);
		
		flame = new Triangle(false);
		flame.rotation = 180;
		flame.scale = .25f;
		flame.translateY = -.7f;
		flame.translateX = .1f;
		flame.setRGBf(1, .6f, 0);
		p.renderable.addChild(flame);
		
		//p.renderable = new Rocket(true);

		p.moveToCenterOfMass();
		p.setSize(size);
		return p;
	}
	
	public static PhyObject getStation(float size) {
		final float[] armVertices = {-.5f, 0, .5f, 0, 0, 1.7f}; 
		PhyComposite p = new PhyComposite();
		
		final int boueys = 3;
		
		for (int i = 0; i < boueys; i++) {
			Triangle arm = new Triangle(false, armVertices);
			arm.setRGBi(64, 180, 120);
			arm.rotation = (float)(i * 2 * 180 / boueys);
			arm.CoMY = -.58f;
			
			PhyCircle bouey = new PhyCircle(1);
			bouey.centerOfMass.y = 2;
			bouey.density = 100;
			bouey.orientation = (float)(i * 2 * Math.PI / boueys + Math.PI);
			bouey.renderable.setRGBi(47, 54, 153);
			p.addObject(bouey);
			p.renderable.addChild(arm);
			
			Circle dome = new Circle(false);
			dome.rotation = (float)(i * 2 * 180 / boueys + 180);
			dome.CoMY = 1.96f;
			dome.scale = .32f;
			dome.setRGBi(84, 109, 142);
			p.renderable.addChild(dome);
		}
		
		Circle center = new Circle(false);
		center.scale = 1.1f;
		center.setRGBi(111, 49, 152);
		p.renderable.addChild(center);
		
		
		Circle dome = new Circle(false);
		dome.scale = .9f;
		dome.setRGBi(153, 217, 234);
		p.renderable.addChild(dome);
		
		p.moveToCenterOfMass();
		p.setSize(size);
		
		
		return p;
		
	}
	
	public PhyComposite(){
		super();
		size = 1;
		positions = new ArrayList<Vector2f>();
		sizes = new ArrayList<Float>();
		objects = new ArrayList<PhyObject>();
		renderable = new SceneGraphNode(true);
	}
	
	public void addObject(PhyObject obj) {
		if (obj instanceof PhyComposite) {
			throw new IllegalArgumentException("A composite in a composite?  You must go deeper!");
		}
		
		// rendering
		obj.renderable.setPickable(false);
		obj.renderable.rotation = (float)(obj.orientation * 180 / Math.PI);
		obj.renderable.CoMX = obj.centerOfMass.x;
		obj.renderable.CoMY = obj.centerOfMass.y;
		SceneGraphNode wrapper = new SceneGraphNode(false);
		wrapper.addChild(obj.renderable);
		obj.renderable = null;
		wrapper.CoMX = -obj.position.x;
		wrapper.CoMY = -obj.position.y;
		renderable.addChild(wrapper);
		
		
		// physics
		obj.originalOrientation = obj.orientation;
		obj.centerOfMass.rotate(obj.orientation);
		obj.centerOfMass.sumScale(obj.position, -1);
		objects.add(obj);
		positions.add(new Vector2f(obj.centerOfMass));
		sizes.add(obj.getSize());
	}

	public void moveToCenterOfMass() {
		float scale = this.size;
		setSize(1 / scale);
		float totalMass = 0;
		Vector2f move = new Vector2f();
		for (PhyObject o : objects) {
			Vector2f tmp = new Vector2f(o.centerOfMass);
			final float mass = 1 / o.inverseMass;
			totalMass += mass;
			tmp.scale(mass);
			move.sum(tmp);
		}
		move.scale(1 / totalMass);
		
		for (Vector2f v : positions) {
			v.sumScale(move, -1);
		}
		for (SceneGraphNode wrapper : renderable.getChildren()) {
			wrapper.translateX += move.x;
			wrapper.translateY += move.y;
		}
		setSize(scale);
	}
	

	
	
	
	
	public final void setSize(float the_size) {
		super.setSize(the_size);
		float totalMomentOfInertia = 0;
		float totalMass = 0;
		for (int i = 0; i < objects.size(); i++) {
			final PhyObject o = objects.get(i);
			// scale appropriately based on original scale and positions
			o.setSize(sizes.get(i) * the_size);
			Vector2f tmp = new Vector2f(positions.get(i));
			tmp.scale(the_size);
			o.centerOfMass = tmp;
			
			totalMass += 1 / o.inverseMass;
			
			//I_z = I_cm + mr^2
			tmp = new Vector2f(o.centerOfMass);
			//tmp.sum(new Vector2f(0, size * 1f)); // TODO this is specific to rocket.
			totalMomentOfInertia += 1 / (o.inverseMomentOfInertia / o.inverseMass)  + o.area * Math.pow(tmp.length(), 2);
			
			
		}
		this.inverseMass = 1 / totalMass;
		this.inverseMomentOfInertia = 1 / totalMomentOfInertia;
		this.inverseMomentOfInertia *= this.inverseMass;
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
