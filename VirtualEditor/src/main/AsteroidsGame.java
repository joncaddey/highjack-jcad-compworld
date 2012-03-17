package main;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import network.Peer;

import phyObj.Asteroid;
import phyObj.Bullet;
import phyObj.CollisionInfo;
import phyObj.EqTriangleAsteroid;
import phyObj.PhyObject;
import phyObj.RightTriangleAsteroid;
import phyObj.Ship;
import phyObj.SquareAsteroid;
import phyObj.Vector2f;
import sound.SoundPlayer;

public class AsteroidsGame extends Observable implements Observer{
	private static final int RESOLUTION_REPEATS = 10;
	
	//sound player
	private static final String DEATH_SOUND = "sound/death.wav";
	private SoundPlayer my_music;
	
	private SceneGraphNode my_asteroid_root;
	private SceneGraphNode my_bullet_root;

	private float my_field_width, my_field_height;

	private Ship my_ship;
	private final List<Bullet> my_bullets;
	private final List<Asteroid> my_asteroids;
	
	private boolean my_game_over;
	private float my_score;
	private int my_level;
	private Peer my_peer;
	
	
	
	public AsteroidsGame() {
		my_music = new SoundPlayer();
		my_music.preLoad(DEATH_SOUND);
		
		my_asteroid_root = new SceneGraphNode();
		my_bullet_root = new SceneGraphNode();
		my_bullets = new ArrayList<Bullet>();
		my_asteroids = new ArrayList<Asteroid>();
		my_game_over = true;
		
		
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
	}
	
	public void startGame() {
		my_score = 0;
		my_level = 0;
		my_asteroid_root = new SceneGraphNode();
		my_bullet_root = new SceneGraphNode();
		my_bullets.clear();
		my_asteroids.clear();
		my_ship = new Ship();
		setChanged();
		notifyObservers(new Long((long)my_score));
		setChanged();
		notifyObservers(new Integer(my_level));
		my_game_over = false;
		
	}
	
	public void keyPressed(KeyEvent the_e) {
		if (my_game_over) return;
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
		if (my_game_over) return;
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
	
	
	
	public void display(final GLAutoDrawable the_drawable, final float the_time_passed) {
		GL2 gl = the_drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		
		// update bullets
		for (Bullet b : my_bullets) {
			b.updateState(the_time_passed);
		}
		
		
		// update ship
		if (!my_game_over) {
				my_ship.updateState(the_time_passed);
			Vector2f position = my_ship.getPosition();
			float radius = .2f;
			if (position.x < -getWidth() / 2 - radius) {
				my_ship.setPosition(getWidth() + 2 * radius + position.x, position.y);
			} else if (position.x > getWidth() / 2 + radius) {
				my_ship.setPosition(-getWidth() - 2 * radius + position.x, position.y);
			}
			if (position.y < -getHeight() / 2 - radius) {
				my_ship.setPosition(position.x, getHeight() + 2 * radius + position.y);
			} else if (position.y > getHeight() / 2 + radius) {
				my_ship.setPosition(position.x, -getHeight() - 2 * radius + position.y);
			}
		}
		
		
		// add any bullets fired since updating the ships state
		if (!my_game_over) {
			for (Bullet bill : my_ship.getBullets()) {
				my_bullet_root.addChild(bill.getRenderable());
				my_bullets.add(bill);
			}
		}
		
		// update asteroids
		for (Asteroid a : my_asteroids) {
			PhyObject phy = a.getObject();
			phy.updateState(the_time_passed);
			Vector2f position = phy.getPosition();
			final float diameter = a.getObject().getSize() * 1.44f;
			if (position.x < -(getWidth() + diameter) / 2) {
				phy.setPosition((getWidth() + diameter) + position.x, position.y);
				if (my_peer != null) {
					System.out.println("MSG SENT");
					my_peer.sendObject("HELLO", 0);
				}
			} else if (position.x > (getWidth() + diameter) / 2) {
				phy.setPosition(-(getWidth() + diameter) + position.x, position.y);
			}
			if (position.y < -(getHeight() + diameter) / 2) {
				phy.setPosition(position.x, (getHeight() + diameter) + position.y);
			} else if (position.y > (getHeight() + diameter) / 2) {
				phy.setPosition(position.x, -(getHeight() + diameter) + position.y);
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
					if (!a.isAlive()) {
						my_score += a.getMaxHP();
						setChanged();
						notifyObservers(new Long((long)my_score));
					}
					
					bill.bounce();
					
				}
			}
		}
		
		// check for collision between ship and asteroids
		noCollisions = false;
		if (!my_game_over) {
			for (int repeat = 0; repeat < RESOLUTION_REPEATS && !noCollisions; repeat++) {
				for (Asteroid a : my_asteroids) {
					CollisionInfo c = my_ship.getCollision(a.getObject());
					if (c != null) {
						my_ship.resolveCollision(a.getObject(), c);
						my_ship.autoShield();
						if (!my_ship.isShielded()) {
							gameOver();
						}
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

		
		
		

		
		if (!my_game_over) {
			my_ship.updateRenderable();
		}
		
		for (Bullet b : my_bullets) {
			b.updateRenderable();
		}
		for (Asteroid a : my_asteroids) {
			a.getObject().updateRenderable();
		}
		my_bullet_root.render(the_drawable);
		my_asteroid_root.render(the_drawable);
		if (!my_game_over) {
			my_ship.getRenderable().render(the_drawable);
		}
		
	}

	
	
	// TODO this is hackey. Should parameterize it for difficulty.
	private void addRandomAsteroid() {
		double type = Math.random();
		float size = 1 + 4 * (float) Math.random();
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
			a.getObject().setPosition(position1 * (getWidth() + size),
					position2 * (getHeight() + size));
		} else {
			a.getObject().setPosition(position2 * (getWidth() + size),
					position1 * (getHeight() + size));
		}
		Vector2f tmp = new Vector2f(0, 1 + (float) Math.random() * 5f);
		tmp.rotate((float) (Math.random() * 2 * Math.PI));
		a.getObject().setVelocity(tmp);
		my_asteroids.add(a);
		my_asteroid_root.addChild(a.getRenderable());
	}
	
	private void sendAsteroid() {
	
	}
	private void receiveAsteroid() {
		
	}
	
	private void gameOver() {
		if (!my_game_over) {
			my_game_over = true;
			
			//sound
			my_music.play(DEATH_SOUND);
			
			setChanged();
			notifyObservers(new Boolean(true));
		}
	}

	public float getWidth() {
		return my_field_width;
	}

	public void setWidth(float the_field_width) {
		this.my_field_width = the_field_width;
	}

	public float getHeight() {
		return my_field_height;
	}

	public void setHeight(float the_field_height) {
		this.my_field_height = the_field_height;
	}
	
	public void setPeer(final Peer the_peer) {
		if (my_peer != null) {
			my_peer.deleteObserver(this);
		}
		my_peer = the_peer;
		if (my_peer != null) {
			my_peer.addObserver(this);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o.equals(my_peer)) {
			System.out.println(arg);
		}
		
	}

}
