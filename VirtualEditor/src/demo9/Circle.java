package demo9;
import javax.media.opengl.*;


public class Circle extends PhysicsObject {
	private class Renderable extends SceneGraphNode {
		private static final int POINTS = 20;	

		public Renderable() {
			scale = radius;
		}
	
		public void renderGeometry(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			
			gl.glBegin(GL.GL_TRIANGLE_FAN);
			gl.glColor3f(red, green, blue);
			gl.glVertex2f(0, 0);
			gl.glColor3f(1, 1, 1);
			gl.glVertex2f(1, 0);
			gl.glColor3f(red, green, blue);
			for (int i = 1; i < POINTS; i++) {
				double radians = 2 * Math.PI * i / POINTS;
				gl.glVertex2d(Math.cos(radians), Math.sin(radians));
			}
			gl.glColor3f(1, 1, 1);
			gl.glVertex2f(1, 0);
			gl.glEnd();
		}
	}
	
	float radius;
	float red;
	float green;
	float blue;

	public Circle(float radius) {
		this(radius, (float)Math.random(), (float)Math.random(), (float)Math.random());
	}
	
	public Circle(float radius, float red, float green, float blue) {
		inverseMomentOfInertia = 1 / (float)(Math.PI * Math.pow(radius, 4) / 4);
		this.radius = radius;
		this.red = red;
		this.green = green;
		this.blue = blue;
		renderable = new Renderable();
	}
	
	public Vector2f getCenter() {
		if (centerOfMass.x == 0 && centerOfMass.y == 0) {
			return position;
		}
		Vector2f r = new Vector2f(position);
		Vector2f tmp = new Vector2f(centerOfMass);
		tmp.scale(-1);
		r.x += (float)(Math.cos(orientation) * tmp.x - Math.sin(orientation) * tmp.y);
		r.y += (float)(Math.sin(orientation) * tmp.x + Math.cos(orientation) * tmp.y);
		return r;
		
//		if (vertexCache == null) {
//			vertexCache = new Vector2f[vertices.length / 2];
//			Vector2f tmp = new Vector2f();
//			for (int i = 0; i < vertices.length; i += 2) {
//				tmp.x = vertices[i] * renderable.scale;
//				tmp.y = vertices[i+1] * renderable.scale;
//				tmp.sumScale(centerOfMass, -1);
//				vertexCache[i/2] = new Vector2f();
//				vertexCache[i/2].x = (float)(Math.cos(orientation) * tmp.x - Math.sin(orientation) * tmp.y);
//				vertexCache[i/2].y = (float)(Math.sin(orientation) * tmp.x + Math.cos(orientation) * tmp.y);
//				//vertexCache[i/2].sum(centerOfMass);
//				vertexCache[i/2].sum(position);
//			}
//		}
	}
}