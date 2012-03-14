package phyObj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import main.SceneGraphNode;
import main.Triangle;

public class Ship extends PhyComposite {

	private static final float ANGULAR_DECAY = 3f; // 1f
	private static final float LINEAR_DECAY = .15f; // .3f
	
	private static final float FORWARD_THRUST = LINEAR_DECAY + .3f; // .25
	private static final float MAX_VELOCITY = 20f;
	private static final float ANGULAR_THRUST = ANGULAR_DECAY +1f; // 1.5f
	private static final float MAX_ANGULAR_VELOCITY = 16;  // 15
	
	private static final float WEAK_KICKBACK = LINEAR_DECAY + .7f;
	private static final float STRONG_KICKBACK = LINEAR_DECAY + 1.3f;
	
	private static final float MY_SHIELD_MAX = 25;
	private static final float BROKEN_SHIELD_RECOVER_TIME = 60;
	
	
	private final SceneGraphNode my_center_flame, my_left_flame, my_right_flame;
	private final SceneGraphNode my_hull;
	
	private boolean my_forward_toggle;
	private boolean my_left_toggle;
	private boolean my_right_toggle;
	private boolean my_bullet_toggle;
	
	private boolean my_shield_toggle;
	
	private float my_shield = MY_SHIELD_MAX;
	private float my_shield_regen = .5f;
	
	
	private List<Bullet> my_bullets = new ArrayList<Bullet>();
	
	/**
	 * Its a shipssss it poilted by kirck cause he has funny hair. picard no hair
	 */
	public Ship() {
		
		
		int variability = 20;
		PhyPolygon finLeft = PhyPolygon.getEqTriangle(.5f);
		finLeft.density = .1f;
		finLeft.renderable.setRGBi(
				93 + (int)(Math.random() * variability),
				0 + (int)(Math.random() * variability),
				131 + (int)(Math.random() * variability));
		finLeft.orientation = radians(90);
		finLeft.position = new Vector2f(-.39f, -.3f);
		// added after finRight

		PhyPolygon finRight = PhyPolygon.getEqTriangle(.5f);
		finRight.density = .1f;
		finRight.renderable.setRGBf(finLeft.getRenderable().getRed(), finLeft
				.getRenderable().getGreen(), finLeft.getRenderable().getBlue());
		finRight.orientation = radians(-90);
		finRight.position = new Vector2f(.39f, -.3f);
		addObject(finLeft);
		addObject(finRight);

		PhyPolygon body = PhyPolygon.getSquare(BODY_RATIO);
		body.density = 1;
		body.renderable.setRGBi(
				218 + (int)(Math.random() * variability),
				8 + (int)(Math.random() * variability),
				16 + (int)(Math.random() * variability));
		body.position.y = -BODY_RATIO / 2;
		addObject(body);
		
		PhyPolygon head = PhyPolygon.getEqTriangle(1.1f);
		head.density = 3;
		head.renderable.setRGBi(
				218 + (int)(Math.random() * variability),
				8 + (int)(Math.random() * variability),
				16 + (int)(Math.random() * variability));
		head.position.y = Triangle.SIN_60 / 3 - 0f;
		addObject(head);
		
		Triangle glass = new Triangle(false);
		glass.scale = .55f;
		glass.translateY = .1f;
		glass.setRGBi(15, 21, 23);
		renderable.addChild(glass);
		
		my_hull = renderable;
		renderable = new SceneGraphNode();
		
		my_center_flame = new Triangle(false);
		my_center_flame.rotation = 180;
		my_center_flame.scale = .25f;
		my_center_flame.translateY = -.8f;
		my_center_flame.setRGBf(1, .6f, 0);
		renderable.addChild(my_center_flame);
		
		my_left_flame = new Triangle(false);
		my_left_flame.rotation = 180;
		my_left_flame.scale = .25f;
		my_left_flame.translateY = -.7f;
		my_left_flame.translateX = -.1f;
		my_left_flame.setRGBf(1, .6f, 0);
		renderable.addChild(my_left_flame);
		
		my_right_flame = new Triangle(false);
		my_right_flame.rotation = 180;
		my_right_flame.scale = .25f;
		my_right_flame.translateY = -.7f;
		my_right_flame.translateX = .1f;
		my_right_flame.setRGBf(1, .6f, 0);
		renderable.addChild(my_right_flame);
		
		renderable.addChild(my_hull);
		
		moveToCenterOfMass();
		setSize(.5f);
		fixFlames();
		

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateState(final float the_time) {
		my_bullets.clear();
		if (my_reload_time > 0) my_reload_time--;
		if (my_heat > 0) my_heat--;
		decay();
		if (my_right_toggle) {
			right();
		}
		if (my_left_toggle) {
			left();
		}
		if (my_forward_toggle) {
			forward();
		}
		if (my_bullet_toggle) {
			fire();
		}
		if (my_shield_toggle) {
			if (my_shield > 0) {
				my_shield--;
				my_hull.setBrightness(.6f + .4f * (1 - my_shield / MY_SHIELD_MAX));
			} else {
				my_shield = -BROKEN_SHIELD_RECOVER_TIME;
				my_hull.setBrightness(.4f);
			}
		} else {
			if (my_shield < MY_SHIELD_MAX) {
				my_shield += my_shield_regen;
			}
			if (my_shield == 0) {
				my_shield = MY_SHIELD_MAX;
				my_hull.setBrightness(.5f);
			}
		}
		
		super.updateState(the_time);
	}
	public void toggleForward(boolean the_on) {
		my_forward_toggle = the_on;
		fixFlames();
	}
	
	public void toggleLeft(boolean the_on) {
		my_left_toggle = the_on;
		if (!my_left_toggle) {
			right();
		}
		fixFlames();
	}
	
	public void toggleRight(boolean the_on) {
		my_right_toggle = the_on;
		if (!my_right_toggle) {
			left();
		}
		fixFlames();
	}
	
	private void fixFlames() {
		my_right_flame.setVisible(my_forward_toggle || my_left_toggle);
		my_left_flame.setVisible(my_forward_toggle || my_right_toggle);
		my_center_flame.setVisible(my_forward_toggle);
	}
	
	public void toggleShield(boolean the_on) {
		my_shield_toggle = the_on;
		if (!my_shield_toggle && my_shield > 0) {
			my_hull.setBrightness(.5f);
		}
	}
	
	public void toggleFire(boolean the_on) {
		my_bullet_toggle = the_on;
		if (!my_bullet_toggle) {
			my_heat += my_reload_time / 2;
			my_reload_time = 0; // this is to encourage breaking your keyboard
		}
	}

	private void forward() {
		Vector2f temp = new Vector2f(0, FORWARD_THRUST);
		temp.rotate(orientation);
		temp.sum(velocity);
		if(temp.length() < MAX_VELOCITY) {
			velocity = temp;
		}
	}

	private void left() {
		if (angularVelocity + ANGULAR_THRUST < MAX_ANGULAR_VELOCITY) {
			angularVelocity += ANGULAR_THRUST;
		}	
	}

	private void right() {
		if (angularVelocity - ANGULAR_THRUST > -MAX_ANGULAR_VELOCITY) {
			angularVelocity -= ANGULAR_THRUST;
		}
	}

	private void reverse() {
		Vector2f temp = new Vector2f(0, -FORWARD_THRUST);
		temp.rotate(orientation);
		temp.sum(velocity);
		if(temp.length() < MAX_VELOCITY) {
			velocity = temp;
		}
	}
	
	private void kickBack(final float the_oomph) {
		Vector2f temp = new Vector2f(0, -the_oomph);
		temp.rotate(orientation);
		velocity.sum(temp);
	}
	
	private void decay() {

		float angleSign = Math.signum(angularVelocity);
		if (Math.abs(angularVelocity) < ANGULAR_DECAY) {
			angularVelocity = 0;
		} else {
			angularVelocity -= ANGULAR_DECAY * angleSign;
		}
		if (velocity.length() < LINEAR_DECAY) {
			velocity.scale(0);
		} else {
			velocity.scale((velocity.length() - LINEAR_DECAY)
					/ velocity.length());
		}
	}
	
	
	// TODO move to appropriate constants when figured out
	private int my_reload_time = 0;
	private int my_heat = 0;
	
	private void fire() {
		if (my_reload_time > 0) {
			return;
		}
		
//		if (my_heat < 1) {
//			powerShot();
//			my_heat += 11;
//			reverse();
//			my_reload_time = 10;
//		} else if (my_heat < 10) {
//			my_heat += 8;
//			my_reload_time = 8;
//			weakShot();
//		} else if (my_heat < 50) {
//			weakShot();
//			my_heat += 1;
//			reverse();
//			my_reload_time = 10;
//		}
		
		if (my_heat < 30) {
			powerShot();
			my_heat += 14;
			kickBack(STRONG_KICKBACK);
			my_reload_time = 3;
		} else if (my_heat < 38) {
			my_heat += 12;
			my_reload_time = 10;
			kickBack(WEAK_KICKBACK);
			weakShot();
		} else if (my_heat < 60) {
			weakShot();
			my_heat += 2;
			kickBack(WEAK_KICKBACK);
			my_reload_time = 10;
		}
		
		
	}
	
	private void powerShot() {

		
		final int bullet_spread = 6;
		final int max_bullet_spread = 64;
		for (int i = 0; i < bullet_spread; i++) {
			Bullet bullet = new Bullet(1, 0, 45, .2f, .5f); // old density .01
			bullet.position = new Vector2f(0, .4f);
			bullet.position.rotate(orientation);
			bullet.position.sum(position);
			bullet.velocity = new Vector2f(0, 13);
			bullet.velocity.rotate(orientation + (float) Math.PI / max_bullet_spread * 2 * (i + .5f - bullet_spread / 2));
			my_bullets.add(bullet);
		}
		
		Bullet bullet = new Bullet(1, 0, 45, .3f, 1f);
		bullet.renderable.setRGBi(200, 54, 42);
		bullet.position = new Vector2f(0, .4f);
		bullet.position.rotate(orientation);
		bullet.position.sum(position);
		bullet.velocity = new Vector2f(0, 14);
		bullet.velocity.rotate(orientation);
		my_bullets.add(bullet);
	}
	
	private void weakShot() {
		Bullet bullet = new Bullet(10, 4, 90, .2f, .0001f);
		bullet.position = new Vector2f(0, .4f);
		bullet.position.rotate(orientation);
		bullet.position.sum(position);
		bullet.velocity = new Vector2f(0, 10);
		bullet.velocity.rotate(orientation);
		my_bullets.add(bullet);
	}
	
	public List<Bullet> getBullets() {
		return Collections.unmodifiableList(my_bullets);
	}
}
