package main;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
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

import phyObj.Bullet;
import phyObj.CollisionInfo;
import phyObj.HalfSpace;
import phyObj.PhyComposite;
import phyObj.PhyObject;
import phyObj.Ship;
import phyObj.Vector2f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * 
 * @author Steven Cozart Jonathan Caddey
 * 
 */
public class VirtualCanvas implements GLEventListener {
	private static final int TARGET_FPS = 45;
	private static final int RESOLUTION_REPEATS = 40;
	
	private static final int SIDE_THRUST = 2;
	public static final int FORWARD_THRUST = 4;



	// relating to clicking
	private SceneGraphNode sceneGraphRoot;
	private ArrayList<PhyObject> objects;
	private List<Bullet> my_bullets;
	private boolean pickNextFrame;
	private Point pickedPoint;

	private float left, right, top, bottom;
	private HalfSpace leftWall, rightWall, topWall, bottomWall;
	private int displayListID = -1;
	private final GLCanvas my_canvas;

	private Ship my_ship;

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
		// my_canvas.addMouseListener(new MouseAdapter() {
		// @Override
		// public void mousePressed(MouseEvent the_e) {
		//
		// }
		// @Override
		// public void mouseReleased(MouseEvent the_e) {
		// System.out.println(pixelToWorld(new Point(the_e.getX(),
		// the_e.getY())));
		// }
		// });
		// my_canvas.addMouseMotionListener(new MouseMotionAdapter() {
		// public void mouseDragged(MouseEvent e) {
		// System.out.println(pixelToWorld(new Point(e.getX(), e.getY())));
		// }
		// });
		sceneGraphRoot = new SceneGraphNode();
		objects = new ArrayList<PhyObject>();
		my_bullets = new ArrayList<Bullet>();
		
		leftWall = new HalfSpace(new Vector2f(-5, 0), new Vector2f(1, 0));
		bottomWall = new HalfSpace(new Vector2f(0, -5), new Vector2f(0, 1));
		rightWall = new HalfSpace(new Vector2f(5, 0), new Vector2f(-1, 0));
		topWall = new HalfSpace(new Vector2f(0, 5), new Vector2f(0, -1));
		objects.add(rightWall);
		objects.add(topWall);
		objects.add(bottomWall);
		objects.add(leftWall);

		/*
		 * / Add independent SceneGraphNode representing all the HalfSpaces.
		 * sceneGraphRoot.addChild(new SceneGraphNode(false) { public void
		 * renderGeometry(GLAutoDrawable drawable) { GL2 gl =
		 * drawable.getGL().getGL2(); gl.glColor3f(1, 1, 1);
		 * gl.glBegin(GL.GL_LINE_LOOP); gl.glVertex2f(-5, -5); gl.glVertex2f(5,
		 * -5); gl.glVertex2f(5, 5); gl.glVertex2f(-5, 5); gl.glEnd(); } }); //
		 */
		
		my_canvas.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent the_e) {
				int code = the_e.getKeyCode();
				switch (code) {
					case KeyEvent.VK_UP:
						my_ship.toggleForward(true);
					break;
					case KeyEvent.VK_LEFT:
						my_ship.toggleLeft(true);
					break;
					case KeyEvent.VK_RIGHT:
						my_ship.toggleRight(true);
					break;
					case KeyEvent.VK_DOWN:
						my_ship.toggleReverse(true);
					break;
					case KeyEvent.VK_SPACE:
						my_ship.toggleFire(true);
					break;
				}
			}
			
			public void keyReleased(KeyEvent the_e) {
				int code = the_e.getKeyCode();
				switch (code) {
					case KeyEvent.VK_UP:
						my_ship.toggleForward(false);
					break;
					case KeyEvent.VK_LEFT:
						my_ship.toggleLeft(false);
					break;
					case KeyEvent.VK_RIGHT:
						my_ship.toggleRight(false);
					break;
					case KeyEvent.VK_DOWN:
						my_ship.toggleReverse(false);
					break;
					case KeyEvent.VK_SPACE:
						my_ship.toggleFire(false);
					break;
				}
			}
		});
		my_ship = new Ship();
		attachObject(my_ship);

	}

	private Vector2f pixelToWorld(Point pixel) {
		// pixel * world / pixel = world
		Vector2f r = new Vector2f(pixel.x, pixel.y);
		r.x = r.x * (right - left) / this.my_canvas.getWidth() - right;
		r.y = r.y * (bottom - top) / this.my_canvas.getHeight() + top;
		return r;
	}

	public void attachObject(final PhyObject object) {
		if (object.getRenderable() != null) {
			sceneGraphRoot.addChild(object.getRenderable());
		}
		objects.add(object);

	}

	public Component getCanvas() {
		return my_canvas;
	}


	public SceneGraphNode getRoot() {
		return sceneGraphRoot;
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
			glu.gluPickMatrix(pickedPoint.x,
					(double) (viewport[3] - pickedPoint.y), 1, 1, viewport, 0);
			gl.glOrtho(left, right, bottom, top, -1, 1);
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			List<SceneGraphNode> picked = sceneGraphRoot.getPicked(drawable);

//			if (picked.isEmpty()) {
//				my_selected = null;
//			} else {
//				SceneGraphNode sgn = picked.get(0);
//				for (PhyObject o : objects) {
//					if (o.getRenderable() != null
//							&& o.getRenderable().equals(sgn)) {
//						my_selected = o;
//						break;
//					}
//				}
//			}

			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glPopMatrix();
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			pickNextFrame = false;
		}
		for (PhyObject object : objects) {
			object.updateState(1f / TARGET_FPS);
		}
		
		for (Bullet bill : my_ship.getBullets()) {
			sceneGraphRoot.addChild(bill.getRenderable());
			my_bullets.add(bill);
		}
		Iterator<Bullet> bit = my_bullets.iterator();
		while (bit.hasNext()) {
			final Bullet b = bit.next();
			b.updateState(1f / TARGET_FPS);
			if (!b.isAlive()) {
				bit.remove();
				sceneGraphRoot.removeChild(b.getRenderable());
			}
		}
		

		boolean noCollisions = false;

		
		
		for (int repeat = 0; repeat < RESOLUTION_REPEATS && !noCollisions; repeat++) {
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

		for (PhyObject object : objects) {
			object.updateRenderable();
		}
		for (Bullet b : my_bullets) {
			b.updateRenderable();
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


}
