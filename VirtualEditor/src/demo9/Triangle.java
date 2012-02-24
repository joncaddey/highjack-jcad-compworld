package demo9;
import javax.media.opengl.*;

public class Triangle extends PhysicsObject {
	private static final float SIN_60 = (float)Math.sin(Math.PI / 3);
	private static final float[] VERTICES = {-.5f, -SIN_60 / 3, .5f, -SIN_60 / 3, 0, SIN_60 * 2 / 3};//{0, 0, 1, 0, 0, 1};
	private Vector2f[] vertexCache;
	private Vector2f[] normalCache;
	
	float red;
	float green;
	float blue;
	
	private class Renderable extends SceneGraphNode {
		public void renderGeometry(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();

			gl.glColor3f(red, green, blue);
			gl.glBegin(GL.GL_TRIANGLES);
			for (int i = 0; i < VERTICES.length; i += 2) {
				gl.glVertex2f(VERTICES[i], VERTICES[i+1]);
			}
			gl.glEnd();
		}
	}

	public Triangle(float size) {
		this(size, (float)Math.random(), (float)Math.random(), (float)Math.random());
	}
	public Triangle(float size, float red, float green, float blue) {
		centerOfMass.x = centerOfMass.y = 0;//size / 3;
		inverseMomentOfInertia = 1 / (float)(Math.pow(size, 4) / 18); // TODO must recalculate (look it up, that's what engineers do).  currently is for right triangle.
		renderable = new Renderable();
		renderable.scale = size;
		renderable.CoMX = centerOfMass.x;
		renderable.CoMY = centerOfMass.y;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public void clearCaches() {
		vertexCache = null;
		normalCache = null;
	}

	public Vector2f[] getVertices() {
		if (vertexCache == null) {
			vertexCache = new Vector2f[VERTICES.length / 2];
			Vector2f tmp = new Vector2f();
			for (int i = 0; i < VERTICES.length; i += 2) {
				tmp.x = VERTICES[i] * renderable.scale;
				tmp.y = VERTICES[i+1] * renderable.scale;
				tmp.sumScale(centerOfMass, -1);
				vertexCache[i/2] = new Vector2f();
				vertexCache[i/2].x = (float)(Math.cos(orientation) * tmp.x - Math.sin(orientation) * tmp.y);
				vertexCache[i/2].y = (float)(Math.sin(orientation) * tmp.x + Math.cos(orientation) * tmp.y);
				vertexCache[i/2].sum(centerOfMass);
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
