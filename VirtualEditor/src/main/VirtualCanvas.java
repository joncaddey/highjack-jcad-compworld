package main;


import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import phyObj.Asteroid;
import phyObj.Bullet;
import phyObj.CollisionInfo;
import phyObj.EqTriangleAsteroid;
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
	private static final float NANO = 1f / 1000000000;
	private static final int TARGET_FPS = 90;
	private static final int FRAMES_TO_AVERAGE = 10;
	private static final float MINIMUM_TIME_BETWEEN_FRAMES = 1f / 30;
	private static final int RESOLUTION_REPEATS = 10;
	

	private static final int BOARD_SIZE = 10;

	private int my_frame_count;
	private float my_average_time_between_frames = 1f / TARGET_FPS;
	private long my_time;
	
	private SceneGraphNode my_root;
	private  SceneGraphNode my_asteroid_root; // TODO final
	private final SceneGraphNode my_bullet_root;

	private float left, right, top, bottom;
	private float my_field_width, my_field_height;
	private final GLCanvas my_canvas;

	private Ship my_ship;
	private final List<Bullet> my_bullets;
	private  List<Asteroid> my_asteroids; // TODO final

	public VirtualCanvas() {
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(profile);
		my_canvas = new GLCanvas(capabilities);
		my_canvas.setFocusable(true);
		my_canvas.addGLEventListener(this);
		my_canvas.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {


			}
		});
		my_root = new SceneGraphNode();
		my_asteroid_root = new SceneGraphNode();
		my_bullet_root = new SceneGraphNode();
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
					case KeyEvent.VK_Q:
						System.out.println("Saving");
						writeObjects();
					break;
					case KeyEvent.VK_P:
						System.out.println("Loading");
						readObjects();
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

	}
	
	
	private void writeObjects() {
		try {
			FileOutputStream fos = new FileOutputStream("data.txt");
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(my_asteroids);
			out.writeObject(my_asteroid_root);
			out.close();
		} catch (Exception e) {
			System.err.println(e);

		}

	}

	private void readObjects() {
		try {
			FileInputStream fis = new FileInputStream("data.txt");
			ObjectInputStream in = new ObjectInputStream(fis);
			my_asteroids = (List<Asteroid>) in.readObject();
			my_asteroid_root = (SceneGraphNode) in.readObject();
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	private Vector2f pixelToWorld(Point pixel) {
		Vector2f r = new Vector2f(pixel.x, pixel.y);
		r.x = r.x * (right - left) / this.my_canvas.getWidth() - right;
		r.y = r.y * (bottom - top) / this.my_canvas.getHeight() + top;
		return r;
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
		
		// predict time per frame
		my_frame_count++;
		if (my_frame_count == FRAMES_TO_AVERAGE) {
			my_frame_count = 0;
			final long old = my_time;
			my_time = System.nanoTime();
			my_average_time_between_frames = Math.min((float)(my_time - old) * NANO / FRAMES_TO_AVERAGE, MINIMUM_TIME_BETWEEN_FRAMES);
		}
		
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		
		// update bullets
		for (Bullet b : my_bullets) {
			b.updateState(my_average_time_between_frames);
		}
		
		
		// update ship
		my_ship.updateState(my_average_time_between_frames);
		Vector2f position = my_ship.getPosition();
		float radius = .2f;
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
			phy.updateState(my_average_time_between_frames);
			position = phy.getPosition();
			final float diameter = a.getObject().getSize() * 1.44f;
			if (position.x < -(my_field_width + diameter) / 2) {
				phy.setPosition((my_field_width + diameter) + position.x, position.y);
			} else if (position.x > (my_field_width + diameter) / 2) {
				phy.setPosition(-(my_field_width + diameter) + position.x, position.y);
			}
			if (position.y < -(my_field_height + diameter) / 2) {
				phy.setPosition(position.x, (my_field_height + diameter) + position.y);
			} else if (position.y > (my_field_height + diameter) / 2) {
				phy.setPosition(position.x, -(my_field_height + diameter) + position.y);
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
					my_ship.resolveCollision(a.getObject(), c);
					my_ship.autoShield();
					if (!my_ship.isShielded()) {
						System.out.println("BOOM!  You LOSE");
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

		
		
		

		
		
		my_ship.updateRenderable();
		for (Bullet b : my_bullets) {
			b.updateRenderable();
		}
		for (Asteroid a : my_asteroids) {
			a.getObject().updateRenderable();
		}
		my_bullet_root.render(drawable);
		my_asteroid_root.render(drawable);
		my_root.render(drawable);
		my_ship.getRenderable().render(drawable);

	}

	public void dispose(GLAutoDrawable drawable) {
	}

	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClearColor(0, 0, 0, 0);
		IntBuffer selectBuffer = Buffers.newDirectIntBuffer(3);
		gl.glSelectBuffer(selectBuffer.capacity(), selectBuffer);
		my_time = System.nanoTime();
		FPSAnimator fps = new FPSAnimator(drawable, TARGET_FPS);
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
