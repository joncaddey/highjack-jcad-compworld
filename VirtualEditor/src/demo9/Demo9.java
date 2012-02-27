package demo9;

import java.awt.*;
import java.awt.event.*;
import java.nio.*;
import java.util.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import javax.swing.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.util.*;

public class Demo9 implements GLEventListener {
	private static final int TARGET_FPS = 30;
	
	private static final float GRAVITY = 10;
	private static final float SLOW_FACTOR = 10;
	private static final int MAX_RESOLUTION_REPEATS = 80;
	
	private static int resolution_repeats = MAX_RESOLUTION_REPEATS;
	private static JFrame appFrame;
	private static SceneGraphNode sceneGraphRoot;
	private static boolean pickNextFrame;
	private static Point pickedPoint;
	private static double left, right, top, bottom;
	private static ArrayList<PhysicsObject> objects;
	
	public static void main(String[] args) {
		GLProfile.initSingleton();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				(new Demo9()).createAndShowGUI();
			}
		});
	}

	private void createAndShowGUI() {
		// Fix for background flickering
		// System.setProperty("sun.awt.noerasebackground", "true");
		
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(profile);
		GLCanvas canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(this);
		canvas.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				pickNextFrame = true;
				pickedPoint = new Point(e.getX(), e.getY());
			}
		});

		objects = new ArrayList<PhysicsObject>();
		sceneGraphRoot = new SceneGraphNode(false);
		
		PhysicsObject obj = PhyPolygon.getRightTriangle(2);
		//PhysicsObject obj = new Circle(1f);
		//PhysicsObject obj = PhyComposite.getPair(1);
		obj.inverseMass = 1f / 20;
		obj.inverseMomentOfInertia *= obj.inverseMass;
		obj.position.x = 0;
		obj.position.y = -4;
		obj.angularVelocity = 2f;
		//obj.velocity.x = 3;
		obj.velocity.y = 16;
		obj.acceleration.y = -GRAVITY;
		attachObject(obj);
		
		obj.centerOfMass.x = 4;
		obj.renderable.CoMX = obj.centerOfMass.x;
		
		
		
		
		//obj = new Pair(1);
		
		obj = PhyPolygon.getRightTriangle(2);
		///obj = new Circle(1f);
		//PhysicsObject obj = PhyComposite.getPair(1);
		obj.inverseMass = 1f / 20;
		obj.inverseMomentOfInertia *= obj.inverseMass;
		obj.position.x = 0;
		obj.position.y = -4;
		obj.angularVelocity = 2f;
		obj.centerOfMass.x = 1;
		obj.renderable.CoMX = obj.centerOfMass.x;
		//obj.velocity.x = 3;
		obj.velocity.y = 16;
		obj.acceleration.y = -GRAVITY;
		//attachObject(obj);
		
		/*/ Add various shapes
		for (int y = 0; y < 7; y++) {
			for (int x = 0; x < 10; x++) {
				float mass = (float)(.7 * Math.random() + .1);
				obj = new Circle((float)(Math.sqrt(mass) * .5));
				//if (Math.random() < .3) 
					obj = PhyPolygon.getRightTriangle((float)(Math.sqrt(mass)));
				obj.inverseMass = 1 / mass;
				obj.inverseMomentOfInertia *= obj.inverseMass;
				obj.position.x = -4.5f + x;
				obj.position.y = 4.5f - y;
				obj.velocity.x = (float)(2 * Math.random() - 1);
				obj.velocity.y = (float)(2 * Math.random() - 1);
				obj.acceleration.y = -GRAVITY;
//				obj.angularVelocity = .5f;
				attachObject(obj);
			}
		}
		// end various shapes */
			
		attachObject(new HalfSpace(new Vector2f(-5, 0), new Vector2f(1, 0)));
		attachObject(new HalfSpace(new Vector2f(0, -5), new Vector2f(0, 1)));
		attachObject(new HalfSpace(new Vector2f(5, 0), new Vector2f(-1, 0)));
		attachObject(new HalfSpace(new Vector2f(0, 5), new Vector2f(0, -1)));
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
		
		appFrame = new JFrame("JOGL Demo 9");
		appFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		appFrame.setMinimumSize(new Dimension(256, 256));
		appFrame.add(canvas);
		appFrame.pack();	if (Toolkit.getDefaultToolkit().isFrameStateSupported(JFrame.MAXIMIZED_BOTH))
			appFrame.setExtendedState(appFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		appFrame.setVisible(true);
	}

	public void attachObject(PhysicsObject object) {
		if (object.renderable != null)
			sceneGraphRoot.addChild(object.renderable);
		objects.add(object);
	}

	public void detachObject(PhysicsObject object) {
		if (object.renderable != null)
			sceneGraphRoot.removeChild(object.renderable);
		int index = objects.indexOf(object);
		if (index == -1)
			throw new IllegalArgumentException();
		objects.set(index, objects.get(objects.size()-1));
		objects.remove(objects.size()-1);
	
	}

	public void display(GLAutoDrawable drawable) {
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
			System.out.println(sceneGraphRoot.getPicked(drawable));
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glPopMatrix();
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			pickNextFrame = false;
		}
		for (PhysicsObject object : objects)
			object.updateState(1f / TARGET_FPS / SLOW_FACTOR);
		boolean noCollisions = false;
		int repeat = 0;
		for (; repeat < resolution_repeats && !noCollisions; repeat++) {
			noCollisions = true;		
			for (int i = 0; i < objects.size(); i++) {
				PhysicsObject a = objects.get(i);
				for (int j = i + 1; j < objects.size(); j++) {
					PhysicsObject b = objects.get(j);
					CollisionInfo cInfo = a.getCollision(b);
					if (cInfo != null) {
						noCollisions = false;
						a.resolveCollision(b, cInfo);
					}
				}
			}
			
			
		}
		if (repeat < resolution_repeats) {
			resolution_repeats = repeat + 1;
		}
		if (repeat == resolution_repeats && resolution_repeats < MAX_RESOLUTION_REPEATS){
			resolution_repeats++;
		}
		
		for (PhysicsObject object : objects){
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
		FPSAnimator fps = new FPSAnimator(drawable, TARGET_FPS);
		fps.start();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		final double UNIT = 11;
		
		if (width < height) {
			left = -UNIT / 2;
			right = UNIT / 2;
			top = (double)height / width * UNIT / 2;
			bottom = -top;
		} else {
			top = UNIT / 2;
			bottom = -UNIT / 2;
			right = (double)width / height * UNIT / 2;
			left = -right;
		}
		
		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(left, right, bottom, top, -1, 1);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}
}

