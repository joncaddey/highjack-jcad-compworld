package phyObj;
import javax.media.opengl.*;

import main.Rectangle;
import main.SceneGraphNode;
import main.Triangle;

public class PhyPolygon extends PhyObject {
	private static final float SIN_60 = (float)Math.sin(Math.PI / 3);
	private static final float[] VERTICES_RIGHT_TRIANGLE = {0, 0, 1, 0, 0, 1f};
	private static final float[] VERTICES_EQ_TRIANGLE = {-.5f, -SIN_60 / 3, .5f, -SIN_60 / 3, 0, SIN_60 * 2 / 3};
	private static final float[] VERTICES_SQUARE = {-.5f, -.5f, .5f, -.5f, .5f, .5f, -.5f, .5f};
	
	private static final Resizer SQUARE_RESIZER = new Resizer() {
		@Override
		public void resize(PhyObject obj, float size) {
			obj.area = obj.size * obj.size;
			obj.inverseMass = 1 / (float) (obj.density * obj.area);
			obj.inverseMomentOfInertia = 1 / (float)(Math.pow(size, 4) / 6) * obj.inverseMass;
		}
	};
	private static final Resizer RIGHT_TRIANGLE_RESIZER = new Resizer() {
		@Override
		public void resize(PhyObject obj, float size) {
			obj.area = .5f * obj.size * obj.size;
			obj.centerOfMass.x = obj.centerOfMass.y = obj.size / 3;
			//obj.centerOfMass.y = obj.size * 5/3f;
			if (obj.renderable != null) {
				obj.renderable.CoMX = obj.centerOfMass.x;
				obj.renderable.CoMY = obj.centerOfMass.y;
			}
			obj.inverseMass = 1 / (float) (obj.density * obj.area);
			obj.inverseMomentOfInertia = 1 / (float)(Math.pow(size, 4) / 18) * obj.inverseMass;
		}
	};
	private static final Resizer EQ_TRIANGLE_RESIZER = new Resizer() {
		@Override
		public void resize(PhyObject obj, float size) {
			obj.area =  .5f * obj.size * obj.size * SIN_60;
			obj.inverseMass = 1 / (float) (obj.density * obj.area);
			obj.inverseMomentOfInertia = 1 / (float)(Math.pow(size, 4) * SIN_60 * (.75 + SIN_60 * SIN_60) / 36) * obj.inverseMass;
		}
	};
	
	private Vector2f[] vertexCache;
	private Vector2f[] normalCache;
	private final float[] vertices;
	private final Resizer resizer;
	
	public static PhyPolygon getRightTriangle(final float the_size) {
		PhyPolygon r = new PhyPolygon(VERTICES_RIGHT_TRIANGLE, the_size, RIGHT_TRIANGLE_RESIZER);
		r.renderable = new Triangle(true, VERTICES_RIGHT_TRIANGLE);
		r.setSize(the_size);
		return r;
	}
	
	public static PhyPolygon getEqTriangle(final float the_size) {
		PhyPolygon r = new PhyPolygon(VERTICES_EQ_TRIANGLE, the_size, EQ_TRIANGLE_RESIZER);
		r.renderable = new Triangle(true, VERTICES_EQ_TRIANGLE);
		r.setSize(the_size);
		return r;
	}
	
	public static PhyPolygon getSquare(final float the_size) {
		PhyPolygon r = new PhyPolygon(VERTICES_SQUARE, the_size, SQUARE_RESIZER);
		r.renderable = new Rectangle(true);
		r.setSize(the_size);
		return r;
	}
	
	
	protected PhyPolygon(float[] vertices, float size, Resizer resizer) {
		super();
		this.size = size;
		this.vertices = vertices;
		this.resizer = resizer;
		resizer.resize(this,size);
	}
	
	public void setSize(float size) {
		super.setSize(size);
		resizer.resize(this, size);
	}
	
	public void clearCaches() {
		vertexCache = null;
		normalCache = null;
	}

	public Vector2f[] getVertices() {
		if (vertexCache == null) {
			vertexCache = new Vector2f[vertices.length / 2];
			Vector2f tmp = new Vector2f();
			for (int i = 0; i < vertices.length; i += 2) {
				tmp.x = vertices[i] * size;
				tmp.y = vertices[i+1] * size;
				tmp.rotate(originalOrientation);
				tmp.sumScale(centerOfMass, -1);
				tmp.rotate(orientation);
				vertexCache[i/2] = new Vector2f(tmp);
				vertexCache[i/2].sum(position);
			}
		}
		return vertexCache;
	}
	
	public Vector2f[] getNormals() {
		if (normalCache == null) {
			Vector2f[] vertices = getVertices();
			normalCache = new Vector2f[vertices.length];

			for (int i = 0; i < vertices.length; i++) {
				normalCache[i] = new Vector2f(vertices[(i+1)%vertices.length]);
				normalCache[i].sumScale(vertices[i], -1);
				normalCache[i].normalize();
				float tmp = normalCache[i].x;
				normalCache[i].x = normalCache[i].y;
				normalCache[i].y = -tmp;
			}
		}
		return normalCache;
	}
}
