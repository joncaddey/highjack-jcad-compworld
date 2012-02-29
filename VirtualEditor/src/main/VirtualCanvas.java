package main;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.IntBuffer;
import java.util.ArrayList;
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
	private static final float GRAVITY = 10;
	private static final float SLOW_FACTOR = 1;
	private static final int MAX_RESOLUTION_REPEATS = 50;
	
	private int resolution_repeats = MAX_RESOLUTION_REPEATS;
	
	private SceneGraphNode sceneGraphRoot;
	private ArrayList<PhyObject> objects;
	private boolean pickNextFrame;
	private Point pickedPoint;

	private double left, right, top, bottom;
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
		sceneGraphRoot = new SceneGraphNode();
		objects = new ArrayList<PhyObject>();
		
		objects.add(new HalfSpace(new Vector2f(-5, 0), new Vector2f(1, 0)));
		objects.add(new HalfSpace(new Vector2f(0, -5), new Vector2f(0, 1)));
		objects.add(new HalfSpace(new Vector2f(5, 0), new Vector2f(-1, 0)));
		objects.add(new HalfSpace(new Vector2f(0, 5), new Vector2f(0, -1)));
		// Add independent SceneGraphNode representing all the HalfSpaces.
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
		
		my_selected = null;
	}
	
	public void attachObject(final PhyObject object) {
		if (object.getRenderable() != null) {
			sceneGraphRoot.addChild(object.getRenderable());
		}
		objects.add(object);
		object.setGravity(new Vector2f(0, -GRAVITY));
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
		if (resolution_repeats > 0)
			for (PhyObject object : objects)
				object.updateState(1f / TARGET_FPS / SLOW_FACTOR);
		boolean noCollisions = false;
		int repeat = 0;
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
		if (repeat == resolution_repeats && resolution_repeats < MAX_RESOLUTION_REPEATS && resolution_repeats > 0){
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
		final double UNIT = 10;

		if (width < height) {
			left = -UNIT / 2;
			right = UNIT / 2;
			top = (double) height / width * UNIT / 2;
			bottom = -top;
		} else {
			top = UNIT / 2;
			bottom = -UNIT / 2;
			right = (double) width / height * UNIT / 2;
			left = -right;
		}

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
}
