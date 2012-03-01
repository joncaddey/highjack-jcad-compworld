package main;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import phyObj.CollisionInfo;
import phyObj.HalfSpace;
import phyObj.PhyObject;
import phyObj.Vector2f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.FPSAnimator;


/**
 * 
 * @author Steven Cozart Jonathan Caddey
 *
 */
public class VirtualCanvas extends Observable implements GLEventListener {
	private static final int TARGET_FPS = 30;
	private static final int MAX_RESOLUTION_REPEATS = 50;
	
	private float my_speed_scale = 1;
	private float my_gravity = 10;
	private boolean my_collionToggle = true;
	
	private int resolution_repeats = MAX_RESOLUTION_REPEATS;
	
	private SceneGraphNode sceneGraphRoot;
	private ArrayList<PhyObject> objects;
	private boolean pickNextFrame;
	private Point pickedPoint;

	private float left, right, top, bottom;
	private HalfSpace leftWall, rightWall, topWall, bottomWall;
	private int displayListID = -1;
	private final GLCanvas my_canvas;


	private PhyObject my_selected;

	public VirtualCanvas() {
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(profile);
		my_canvas = new GLCanvas(capabilities);
		my_canvas.addGLEventListener(this);
		my_canvas.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				pickNextFrame = true;
				pickedPoint = new Point(e.getX(), e.getY());
				
			}
		});
		my_canvas.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				System.out.println(e.getX());
			}
		});
		sceneGraphRoot = new SceneGraphNode();
		objects = new ArrayList<PhyObject>();
		leftWall = new HalfSpace(new Vector2f(-5, 0), new Vector2f(1, 0));
		bottomWall = new HalfSpace(new Vector2f(0, -5), new Vector2f(0, 1));
		rightWall = new HalfSpace(new Vector2f(5, 0), new Vector2f(-1, 0));
		topWall = new HalfSpace(new Vector2f(0, 5), new Vector2f(0, -1));
		objects.add(rightWall);
		objects.add(topWall);
		objects.add(bottomWall);
		objects.add(leftWall);
		
		/*/ Add independent SceneGraphNode representing all the HalfSpaces.
		sceneGraphRoot.addChild(new SceneGraphNode(false) {
			public void renderGeometry(GLAutoDrawable drawable) {
				GL2 gl = drawable.getGL().getGL2();	
				gl.glColor3f(1, 1, 1);
				gl.glBegin(GL.GL_LINE_LOOP);
				gl.glVertex2f(-5, -5);
				gl.glVertex2f(5, -5);
				gl.glVertex2f(5, 5);
				gl.glVertex2f(-5, 5);
				gl.glEnd();
			}
		});
		//*/
		
		my_selected = null;
	}
	
	public void attachObject(final PhyObject object) {
		if (object.getRenderable() != null) {
			sceneGraphRoot.addChild(object.getRenderable());
		}
		objects.add(object);
		object.setGravity(my_gravity);
		refresh();
		my_selected = object;
		setChanged();
		notifyObservers(my_selected);
		
	}

	public Component getCanvas() {
		return my_canvas;
	}

	public PhyObject getSelected() {
		return my_selected;
	}
	
	public SceneGraphNode getRoot() {
		return sceneGraphRoot;
	}
	
	public float getGravity() {
		return my_gravity;
	}
	
	public void setGravity(final float the_gravity) {
		my_gravity = the_gravity;
		for (PhyObject o : objects) {
			if (!(o instanceof HalfSpace)) {
				o.setGravity(the_gravity);
			}
		}
	}
	
	public float getSpeedScale() {
		return my_speed_scale;
	}
	
	public void setSpeedScale(final float the_speed_scale) {
		if (the_speed_scale < 0) {
			throw new IllegalArgumentException("Can't go back in time");
		}
		my_speed_scale = the_speed_scale;
	}
	
	public void setCollisions(boolean the_collisions) {
		my_collionToggle = the_collisions;
	}
	
	public void launch(float power) {
		if (my_selected != null) {
			Vector2f tmp = new Vector2f(0, power);
			tmp.rotate(my_selected.getRotationRadians());
			tmp.sum(my_selected.getVelocity());
			my_selected.setVelocity(tmp);
		}
	}

	public void refresh() {
		displayListID = -1;


	}

	public void display(GLAutoDrawable drawable) {
//		notifyObservers(my_selected);
//		setChanged();
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		if (pickNextFrame) {
			GLU glu = GLU.createGLU(gl);
			int viewport[] = new int[4];
			gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			glu.gluPickMatrix(pickedPoint.x, (double)(viewport[3] - pickedPoint.y), 1, 1, viewport, 0);
			gl.glOrtho(left, right, bottom, top, -1, 1);
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			List<SceneGraphNode> picked = sceneGraphRoot.getPicked(drawable);
			
			if (!picked.isEmpty()) {
				SceneGraphNode sgn = picked.get(0);
				for (PhyObject o : objects) {
					if (o.getRenderable() != null && o.getRenderable().equals(sgn)) {
						my_selected = o;
						setChanged();
						notifyObservers(my_selected);
						break;
					}
				}
			}
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glPopMatrix();
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			pickNextFrame = false;
		}
		if (my_speed_scale > 0)
			for (PhyObject object : objects)
				object.updateState(1f / TARGET_FPS * my_speed_scale);
		boolean noCollisions = false;
		
		int repeat = 0;
		if (!my_collionToggle) {
			repeat = resolution_repeats;
		}
		for (; repeat < resolution_repeats && !noCollisions; repeat++) {
			noCollisions = true;
			for (int i = 0; i < objects.size(); i++) {
				PhyObject a = objects.get(i);
				for (int j = i + 1; j < objects.size(); j++) {
					PhyObject b = objects.get(j);
					CollisionInfo cInfo = a.getCollision(b);
					if (cInfo != null) {
						noCollisions = false;
						a.resolveCollision(b, cInfo);
					}
				}
			}

			
		}
		// TODO all this might be bunk.  just use res_rep for pausing.
		if (repeat < resolution_repeats) {
			resolution_repeats = repeat + 1;
		}
		if (repeat == resolution_repeats && resolution_repeats < MAX_RESOLUTION_REPEATS){
			resolution_repeats++;
		}
		
		for (PhyObject object : objects){
			object.updateRenderable();
		}
		sceneGraphRoot.render(drawable);
		
	}

	public void dispose(GLAutoDrawable drawable) {
	}

	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClearColor(0, 0, 0, 0);
		IntBuffer selectBuffer = Buffers.newDirectIntBuffer(3);
		gl.glSelectBuffer(selectBuffer.capacity(), selectBuffer);
		FPSAnimator fps = new FPSAnimator(drawable, 30);
		fps.start();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		final float UNIT = 10;

		if (width < height) {
			left = -UNIT / 2;
			right = UNIT / 2;
			top = (float) height / width * UNIT / 2;
			bottom = -top;
		} else {
			top = UNIT / 2;
			bottom = -UNIT / 2;
			right = (float) width / height * UNIT / 2;
			left = -right;
		}
		
		leftWall.setPosition(left, 0);
		rightWall.setPosition(right, 0);
		bottomWall.setPosition(0, bottom);
		topWall.setPosition(0, top);

		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(left, right, bottom, top, -1, 1);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}

	public void remove() {
		sceneGraphRoot.removeChild(my_selected.getRenderable());
		objects.remove(my_selected);
		my_selected = null;
		setChanged();
		notifyObservers();
	}
	
	public void removeAll() {
		Iterator<PhyObject> it = objects.iterator();
		while (it.hasNext()) {
			PhyObject next = it.next();
			if (!(next instanceof HalfSpace)) {
				sceneGraphRoot.removeChild(next.getRenderable());
				it.remove();
			}
		}
		my_selected = null;
		setChanged();
		notifyObservers();
	}
}
