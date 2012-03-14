package main;
import java.util.*;
import javax.media.opengl.*;
// sup
public class SceneGraphNode {
	public float scale;
	public float rotation;
	public float translateX;
	public float translateY;
	public float CoMX;
	public float CoMY;
	private List<SceneGraphNode> children;
	private boolean pickable;
	
	private boolean my_visible;
	
	/**
	 * RGB after brightness is applied.
	 */
	public float current_red, current_blue, current_green;
	private float red, green, blue;
	
	public SceneGraphNode() {
		this(true);
	}
	
	public List<SceneGraphNode> getChildren() {
		return children;
	}
	
	public SceneGraphNode(boolean pickable) {
		my_visible = true;
		scale = 1;
		children = new LinkedList<SceneGraphNode>();
		red = (float) Math.random();
		green = (float) Math.random();
		blue = (float) Math.random();
		this.pickable = pickable;
		setBrightness(.5f);
	}
	
	public void addChild(SceneGraphNode node) {
		if (node == this)
			throw new IllegalArgumentException();
		children.add(node);
	}
	
	public void removeChild(SceneGraphNode node) {
		children.remove(node);
	}
	
	public final void render(GLAutoDrawable drawable) {
		if (my_visible) {
			GL2 gl = drawable.getGL().getGL2();
			
			gl.glPushMatrix();
			gl.glTranslatef(translateX, translateY, 0);
			gl.glRotatef(rotation, 0, 0, 1);
			gl.glTranslatef(-CoMX, -CoMY, 0);
			gl.glScalef(scale, scale, scale);
			if (children.size() == 0)
				renderGeometry(drawable);
			else
				for (SceneGraphNode child : children)
					child.render(drawable);
			gl.glPopMatrix();
		}
	}

	public void renderGeometry(GLAutoDrawable drawable) {
	}
	
	public final List<SceneGraphNode> getPicked(GLAutoDrawable drawable) {
		List<SceneGraphNode> picked = new LinkedList<SceneGraphNode>();
		GL2 gl = drawable.getGL().getGL2();
		gl.glRenderMode(GL2.GL_SELECT);		
		getPicked(drawable, picked);
		gl.glRenderMode(GL2.GL_RENDER);
		return picked;
	}

	private boolean getPicked(GLAutoDrawable drawable, List<SceneGraphNode> picked) {
		GL2 gl = drawable.getGL().getGL2();
		boolean pickPending = false;
		
		gl.glPushMatrix();
		gl.glTranslatef(translateX, translateY, 0);
		gl.glRotatef(rotation, 0, 0, 1);
		gl.glTranslatef(-CoMX, -CoMY, 0);
		gl.glScalef(scale, scale, scale);
		if (children.size() == 0) {
			renderGeometry(drawable);
			int hits = gl.glRenderMode(GL2.GL_SELECT);
			if (hits > 0)
				if (pickable)
					picked.add(this);
				else
					pickPending = true;
		} else {
			for (SceneGraphNode child : children)
				pickPending = child.getPicked(drawable, picked) || pickPending;
			if (pickable && pickPending) {
				picked.add(this);
				pickPending = false;
			}
		}
		gl.glPopMatrix();
		return pickPending;
	}
	
	public void setPickable(boolean the_pickable) {
		this.pickable = the_pickable;
	}
	
	public void setRGBf(float r, float g, float b) {
		red = r;
		green = g;
		blue = b;
		setBrightness(.5f);
	}
	
	public void setRGBi(int r, int g, int b) {
		setRGBf(r / 256f, g / 256f, b / 256f);
	}
	
	public void setBrightness(final float the_brightness) {
		if (children.isEmpty()) {
			if (the_brightness < .5) {
				current_red = red * the_brightness * 2;
				current_green = green * the_brightness * 2;
				current_blue = blue * the_brightness * 2;
			} else {
				current_red = (1 - red) * (the_brightness - .5f) * 2 + red;
				current_green = (1 - green) * (the_brightness - .5f) * 2 + green;
				current_blue = (1 - blue) * (the_brightness - .5f) * 2 + blue;
			}
		} else {
			for (SceneGraphNode s : children) {
				s.setBrightness(the_brightness);
			}
		}
	}
	
	public void setVisible(final boolean the_visible) {
		my_visible = the_visible;
	}
	
	public float getRed() {
		return red;
	}
	
	public float getBlue() {
		return blue;
	}
	
	public float getGreen() {
		return green;
	}
}
