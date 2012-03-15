package phyObj;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.media.opengl.*;

import main.Circle;
import main.SceneGraphNode;


public class PhyCircle extends PhyObject {
	private static final long serialVersionUID = 42L;

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		setSize(size);
	}
	public PhyCircle(float diameter) {
		this(diameter, (float)Math.random(), (float)Math.random(), (float)Math.random());
	}
	
	public PhyCircle(float diameter, float red, float green, float blue) {
		this.size = diameter / 2;
		area = (float) (size * size * Math.PI);
		float inverseMass = 1 / (density * area);
		inverseMomentOfInertia = 1 / (float)(Math.PI * Math.pow(size, 4) / 4) * inverseMass;
		renderable = new Circle();
		renderable.setRGBf(red, green, blue);
		renderable.scale = diameter / 2;
	}
	
	public void setSize(float diameter) {
		super.setSize(diameter/2);
		area = (float) (size * size * Math.PI);
		inverseMass = 1 / (density * area);
		inverseMomentOfInertia = 1 / (float)(Math.PI * Math.pow(size, 4) / 4) * inverseMass;
	}
	
	/**
	 * @return the circle's diameter.
	 */
	public float getSize() {
		return size * 2;
	}
	
	public Vector2f getCenter() {
		if (centerOfMass.x == 0 && centerOfMass.y == 0) {
			return position;
		}
		Vector2f r = new Vector2f(centerOfMass);
		r.scale(-1);
		r.rotate(orientation);
		r.sum(position);
		return r;
	}
}