package main;
import java.util.*;
import javax.media.opengl.*;

public class SceneGraphNode {
	public float scale;
	public float rotation;
	public float translateX;
	public float translateY;
	public float CoMX;
	public float CoMY;
	private List<SceneGraphNode> children;
	private boolean pickable;
	
	public SceneGraphNode() {
		this(true);
	}
	
	public SceneGraphNode(boolean pickable) {
		scale = 1;
		children = new LinkedList<SceneGraphNode>();
		this.pickable = pickable;
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
		GL2 gl = drawable.getGL().getGL2();
		
		gl.glPushMatrix();
		gl.glTranslatef(translateX + CoMX, translateY + CoMY, 0);
		gl.glRotatef(rotation, 0, 0, 1);
		//gl.glTranslatef(-CoMX, -CoMY, 0);
		gl.glScalef(scale, scale, scale);
		if (children.size() == 0)
			renderGeometry(drawable);
		else
			for (SceneGraphNode child : children)
				child.render(drawable);
		gl.glPopMatrix();
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
}
