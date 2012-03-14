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
import java.util.ListIterator;
import java.util.Observable;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import phyObj.Asteroid;
import phyObj.Bullet;
import phyObj.CollisionInfo;
import phyObj.EqTriangleAsteroid;
import phyObj.HalfSpace;
import phyObj.PhyComposite;
import phyObj.PhyObject;
import phyObj.RightTriangleAsteroid;
import phyObj.Ship;
import phyObj.SquareAsteroid;
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
	private static final int RESOLUTION_REPEATS = 10;
	

	private static final int BOARD_SIZE = 10;

	// relating to clicking
	private SceneGraphNode my_root;
	private final SceneGraphNode my_asteroid_root;
	private final SceneGraphNode my_bullet_root;
	private ArrayList<PhyObject> objects; // TODO refactor out
	private boolean pickNextFrame;
	private Point pickedPoint;

	private float left, right, top, bottom;
	private float my_field_width, my_field_height;
	private int displayListID = -1;
	private final GLCanvas my_canvas;

	private Ship my_ship;
	private final List<Bullet> my_bullets;
	private final List<Asteroid> my_asteroids;

	public VirtualCanvas() {
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(profile);
		my_canvas = new GLCanvas(capabilities);
		my_canvas.setFocusable(true);
		my_canvas.addGLEventListener(this);
		my_canvas.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				pickNextFrame = true;
				pickedPoint = new Point(e.getX(), e.getY());

			}
		});
		my_root = new SceneGraphNode();
		my_asteroid_root = new SceneGraphNode();
		my_bullet_root = new SceneGraphNode();
		objects = new ArrayList<PhyObject>();
		my_bullets = new ArrayList<Bullet>();
		my_asteroids = new ArrayList<Asteroid>();
		

		
		// Add independent SceneGraphNode representing all the HalfSpaces.
//		my_root.addChild(new SceneGraphNode(false) {
//			public void renderGeometry(GLAutoDrawable drawable) {
//				GL2 gl = drawable.getGL().getGL2();
//				gl.glColor3f(1, 1, 1);
//				gl.glBegin(GL.GL_LINE_LOOP);
//				gl.glVertex2f(-5, -5);
//				gl.glVertex2f(5, -5);
//				gl.glVertex2f(5, 5);
//				gl.glVertex2f(-5, 5);
//				gl.glEnd();
//			}
//		}); 
		
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
						my_ship.toggleShield(true);
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
						my_ship.toggleShield(false);
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
		Vector2f r = new Vector2f(pixel.x, pixel.y);
		r.x = r.x * (right - left) / this.my_canvas.getWidth() - right;
		r.y = r.y * (bottom - top) / this.my_canvas.getHeight() + top;
		return r;
	}

	public void attachObject(final PhyObject object) {
		if (object.getRenderable() != null) {
			my_root.addChild(object.getRenderable());
		}
		objects.add(object);

	}

	public Component getCanvas() {
		return my_canvas;
	}


	public SceneGraphNode getRoot() {
		return my_root;
	}
	
	// TODO this is hackey.  Should parameterize it for difficulty.
	private void addRandomAsteroid() {
		double type = Math.random();
		float size = 1 + 4 * (float)Math.random();
		final Asteroid a;
		final float hp = 10;
		if (type <= .3) {
			a = new EqTriangleAsteroid(size, hp);
		} else if (type <= .6) {
			a = new RightTriangleAsteroid(size, hp);
		} else {
			a = new SquareAsteroid(size, hp);
		}
		final float position1 = (float) Math.random() - .5f;
		final float position2 = Math.random() < .5 ? -.5f : .5f;
		if (Math.random() < .5) {
			a.getObject().setPosition(position1 * (my_field_width + size),  position2 * (my_field_height + size));
		} else {
			a.getObject().setPosition(position2 * (my_field_width + size),  position1 * (my_field_height + size));
		}
		Vector2f tmp = new Vector2f(0, 1 + (float) Math.random() * 5f);
		tmp.rotate((float)(Math.random() * 2 * Math.PI));
		a.getObject().setVelocity(tmp);
		my_asteroids.add(a);
		my_asteroid_root.addChild(a.getRenderable());
	}


	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		final float time = 1f / TARGET_FPS;
		
		// update bullets
		for (Bullet b : my_bullets) {
			b.updateState(time);
		}
		
		
		// update ship
		for (PhyObject object : objects) {
			object.updateState(time);
		}
		Vector2f position = my_ship.getPosition();
		float radius = .3f;
		if (position.x < -my_field_width / 2 - radius) {
			my_ship.setPosition(my_field_width + 2 * radius + position.x, position.y);
		} else if (position.x > my_field_width / 2 + radius) {
			my_ship.setPosition(-my_field_width - 2 * radius + position.x, position.y);
		}
		if (position.y < -my_field_height / 2 - radius) {
			my_ship.setPosition(position.x, my_field_height + 2 * radius + position.y);
		} else if (position.y > my_field_height / 2 + radius) {
			my_ship.setPosition(position.x, -my_field_height - 2 * radius + position.y);
		}
		
		
		// add any bullets fired since updating the ships state
		for (Bullet bill : my_ship.getBullets()) {
			my_bullet_root.addChild(bill.getRenderable());
			my_bullets.add(bill);
		}
		
		// update asteroids
		for (Asteroid a : my_asteroids) {
			PhyObject phy = a.getObject();
			phy.updateState(time);
			position = phy.getPosition();
			final float max_size = 5;
			if (position.x < -(my_field_width + max_size) / 2) {
				phy.setPosition((my_field_width + max_size) + position.x, position.y);
			} else if (position.x > (my_field_width + max_size) / 2) {
				phy.setPosition(-(my_field_width + max_size) + position.x, position.y);
			}
			if (position.y < -(my_field_height + max_size) / 2) {
				phy.setPosition(position.x, (my_field_height + max_size) + position.y);
			} else if (position.y > (my_field_height + max_size) / 2) {
				phy.setPosition(position.x, -(my_field_height + max_size) + position.y);
			}
		}
		
		// check for collisions between asteroids and asteroids
		boolean noCollisions = false;
		for (int repeat = 0; repeat < RESOLUTION_REPEATS && !noCollisions; repeat++) {
			noCollisions = true;
			for (int i = 0; i < my_asteroids.size(); i++) {
				Asteroid a = my_asteroids.get(i);
				for (int j = i + 1; j < my_asteroids.size(); j++) {
					Asteroid b = my_asteroids.get(j);
					CollisionInfo cInfo = a.getObject().getCollision(b.getObject());
					if (cInfo != null) {
						noCollisions = false;
						a.getObject().resolveCollision(b.getObject(), cInfo);
					}
				}
			}

		}
		
		// check for collisions between bullets and asteroids
		for (Bullet bill : my_bullets) {
			for (Asteroid a : my_asteroids) {
				CollisionInfo c = bill.getCollision(a.getObject());
				if (c != null) {
					noCollisions = false;
					final float prevSpeed = bill.getVelocity().length();
					bill.resolveCollision(a.getObject(), c);
					final Vector2f velocity = bill.getVelocity();
					velocity.setLength(prevSpeed);
					bill.setVelocity(velocity);
					a.decrementHP(bill.getDamage());
					bill.bounce();
				}
			}
		}
		
		// check for collision between ship and asteroids
		noCollisions = false;
		for (int repeat = 0; repeat < RESOLUTION_REPEATS && !noCollisions; repeat++) {
			for (Asteroid a : my_asteroids) {
				CollisionInfo c = my_ship.getCollision(a.getObject());
				if (c != null) {
					Vector2f dv = new Vector2f(my_ship.getVelocity());
					my_ship.resolveCollision(a.getObject(), c);
					dv.sumScale(my_ship.getVelocity(), -1);
					if (dv.length() > 2) {
						System.out.println(dv.length());
					}

				}
			}
		}

		// remove any bullets that need to be removed
		Iterator<Bullet> bit = my_bullets.iterator();
		while (bit.hasNext()) {
			final Bullet b = bit.next();
			if (!b.isAlive()) {
				bit.remove();
				my_bullet_root.removeChild(b.getRenderable());
			}
		}
		
		// remove any asteroids that need to be removed
		ListIterator<Asteroid> ait = my_asteroids.listIterator();
		while (ait.hasNext()) {
			final Asteroid a = ait.next();
			if (!a.isAlive()) {
				ait.remove();
				my_asteroid_root.removeChild(a.getRenderable());
				for (Asteroid b : a.getFragments()) {
					ait.add(b);
					my_asteroid_root.addChild(b.getRenderable());
				}
			}
		}
		if (my_asteroids.size() < 14) {
			addRandomAsteroid();
			addRandomAsteroid();
		}
		

		noCollisions = false;

		
		
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
		for (Asteroid a : my_asteroids) {
			a.getObject().updateRenderable();
		}
		my_bullet_root.render(drawable);
		my_asteroid_root.render(drawable);
		my_root.render(drawable);

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
		if (width < height) {
			left = -BOARD_SIZE / 2;
			right = -left;
			top = (float) height / width * BOARD_SIZE / 2;
			bottom = -top;
		} else {
			top = BOARD_SIZE / 2;
			bottom = -top;
			right = (float) width / height * BOARD_SIZE / 2;
			left = -right;
		}
		my_field_width = right - left;
		my_field_height = top - bottom;


		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(left, right, bottom, top, -1, 1);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}
	
	


}
