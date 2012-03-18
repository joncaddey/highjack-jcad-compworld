package phyObj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sound.SoundPlayer;


import main.SceneGraphNode;
import main.Triangle;

public class Ship extends PhyComposite {

	
	
	private static final float ANGULAR_DECAY = 3f * 45; // 1f
	private static final float LINEAR_DECAY = .15f * 45; // .3f
	
	private static final float FORWARD_THRUST = LINEAR_DECAY + .3f * 45; // .25
	private static final float MAX_VELOCITY = 20f * 30f / 45;
	private static final float ANGULAR_THRUST = ANGULAR_DECAY +1f * 45; // 1.5f
	private static final float MAX_ANGULAR_VELOCITY = 16 * 30f / 45;  // 15
	
	private static final float WEAK_KICKBACK = 1f;
	private static final float STRONG_KICKBACK = 1.45f;
	
	private static final float SHIELD_MAX = 11.1f;
	private static final float SHIELD_RECOVER_TIME = 4;
	private static final float BROKEN_SHIELD_RECOVER_TIME = 4;
	private static float SHIELD_REGEN = SHIELD_MAX / SHIELD_RECOVER_TIME;
	private static final float AUTO_SHIELD_TIME = .4f;
	private static final float AUTO_SHIELD_PENALTY = .15f;
	
	//sound player
	private static final String LAZER_SOUND = "sound/lazersmall.wav";
	private static final String POWER_LAZER_SOUND = "sound/POWER.wav";
	private SoundPlayer my_music;
	private SoundPlayer my_music2;
	
	private final SceneGraphNode my_center_flame, my_left_flame, my_right_flame;
	private final SceneGraphNode my_hull;
	
	private boolean my_forward_toggle;
	private boolean my_left_toggle;
	private boolean my_right_toggle;
	private boolean my_bullet_toggle;
	private boolean my_auto_shield_toggle;
	
	private boolean my_shield_toggle;
	
	private float my_shield = SHIELD_MAX;
	private float my_auto_shield;
	
	
	
	private List<Bullet> my_bullets = new ArrayList<Bullet>();
	
	/**
	 * Its a shipssss it poilted by kirck cause he has funny hair. picard no hair
	 */
	public Ship() {
		//sound
		my_music = new SoundPlayer();
		my_music2 = new SoundPlayer();
		my_music.preLoad(LAZER_SOUND);
		my_music2.preLoad(POWER_LAZER_SOUND);
		
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
		decay(the_time);
		if (my_right_toggle) {
			right(the_time);
		}
		if (my_left_toggle) {
			left(the_time);
		}
		if (my_forward_toggle) {
			forward(the_time);
		}
		if (my_bullet_toggle) {
			fire();
		}
		if (my_reload_time > 0) {
			my_reload_time -= the_time;
		}
		if (my_heat > 0) {
			my_heat -= the_time;
		}
		
		if (my_auto_shield_toggle) {
			if (my_auto_shield > 0) {
				my_auto_shield -= the_time;
			} else {
				my_auto_shield_toggle = false;
				toggleShield(my_shield_toggle);
			}
		}
		if (my_shield_toggle || my_auto_shield_toggle) {
			if (my_shield > 0) {
				my_shield -= the_time;
				my_hull.setBrightness(.6f + .4f * (1 - my_shield / SHIELD_MAX));
			} else {
				my_shield = -BROKEN_SHIELD_RECOVER_TIME;
				toggleShield(false);
				my_hull.setBrightness(.3f);
			}
		} else {
			if (my_shield > 0) {
				my_shield += SHIELD_REGEN * the_time;
				if (my_shield > SHIELD_MAX) {
					my_shield = SHIELD_MAX;
				}
			} else {
				if (my_shield + the_time >= 0) {
					my_shield = SHIELD_MAX;
					my_hull.setBrightness(.5f);
				} else {
					my_shield += the_time;
				}
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
//		if (!my_left_toggle) {
//			right(); // TODO
//		}
		fixFlames();
	}
	
	public void toggleRight(boolean the_on) {
		my_right_toggle = the_on;
//		if (!my_right_toggle) { // TODO
//			left();
//		}
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
		if (!my_shield_toggle) {
			my_auto_shield_toggle = false;
			my_auto_shield = 0;
		}
	}
	
	public void toggleFire(boolean the_on) {
		my_bullet_toggle = the_on;
		if (!my_bullet_toggle) {
			my_heat += my_reload_time;
			my_reload_time = 0; // this is to encourage breaking your keyboard
		}
	}

	private void forward(final float the_time) {
		Vector2f temp = new Vector2f(0, FORWARD_THRUST * the_time);
		temp.rotate(orientation);
		velocity.sum(temp);
		if(temp.length() > MAX_VELOCITY) {
			velocity.setLength(MAX_VELOCITY);
		}
	}

	private void left(final float the_time) {
		angularVelocity += ANGULAR_THRUST * the_time;
		if (angularVelocity > MAX_ANGULAR_VELOCITY) {
			angularVelocity = MAX_ANGULAR_VELOCITY;
		}	
	}

	private void right(final float the_time) {
		angularVelocity -= ANGULAR_THRUST * the_time;
		if (angularVelocity < -MAX_ANGULAR_VELOCITY) {
			angularVelocity = -MAX_ANGULAR_VELOCITY;
		}
	}

//	private void reverse(final float the_time) {
//		Vector2f temp = new Vector2f(0, -FORWARD_THRUST);
//		temp.rotate(orientation);
//		temp.sum(velocity);
//		if(temp.length() < MAX_VELOCITY) {
//			velocity = temp;
//		}
//	}
	
	
	public void autoShield() {
		if (!my_shield_toggle && !my_auto_shield_toggle) {
			my_auto_shield_toggle = true;
			my_auto_shield = AUTO_SHIELD_TIME;
			if (my_shield - AUTO_SHIELD_PENALTY > 0) {
				my_shield -= AUTO_SHIELD_PENALTY;
			}
		}
	}
	
	public boolean isShielded() {
		return my_shield > 0 && (my_shield_toggle || my_auto_shield_toggle);
	}
	private void kickBack(final float the_oomph) {
		Vector2f temp = new Vector2f(0, -the_oomph);
		temp.rotate(orientation);
		velocity.sum(temp);
	}
	
	private void decay(final float the_time) {

		float angleSign = Math.signum(angularVelocity);
		if (Math.abs(angularVelocity) < ANGULAR_DECAY * the_time) {
			angularVelocity = 0;
		} else {
			angularVelocity -= ANGULAR_DECAY * angleSign * the_time;
		}
		if (velocity.length() < LINEAR_DECAY * the_time) {
			velocity.scale(0);
		} else {
			velocity.scale((velocity.length() - LINEAR_DECAY * the_time)
					/ velocity.length());
		}
	}
	
	
	// TODO move to appropriate constants when figured out
	private float my_reload_time = 0;
	private float my_heat = 0;
	
	
	private void fire() {
		if (my_reload_time > 0) {
			return;
		}
		
		

		
//		if (my_heat < 30f / 45) {
//			powerShot();
//			my_heat += 14f / 45;
//			kickBack(STRONG_KICKBACK);
//			my_reload_time = 3f / 45;
//		} else if (my_heat < 38 / 45f) {
//			my_heat += 12 / 45f;
//			my_reload_time = 10 / 45f;
//			kickBack(WEAK_KICKBACK);
//			weakShot();
//		} else if (my_heat < 60 / 45f) {
//			weakShot();
//			my_heat += 2 / 45f;
//			kickBack(WEAK_KICKBACK);
//			my_reload_time = 10 / 45f;
//		}
//		
//		
//	}
		if (my_heat < .3 * 2) {
			powerShot();
			my_heat += .3f + .07f;
			kickBack(STRONG_KICKBACK);
			my_reload_time = .07f;
			
			try{
				my_music2.pause(POWER_LAZER_SOUND);
				my_music2.play(POWER_LAZER_SOUND);
			}catch (Exception e) {
				System.out.print(":D");
			}
			
		} else if (my_heat < .9f) {
			my_heat += .3f;
			my_reload_time = .3f;
			
			try{
				my_music.pause(LAZER_SOUND);
				my_music.play(LAZER_SOUND);
			}catch (Exception e) {
				System.out.print(":D 1");
			}

			
			kickBack(WEAK_KICKBACK);
			weakShot();
			
		} else if (my_heat < 2f) {
			weakShot();
			my_heat += .05f;
			kickBack(WEAK_KICKBACK);
			my_reload_time = .3f;
			
			try{
			my_music.pause(LAZER_SOUND);
			my_music.play(LAZER_SOUND);
		}catch (Exception e) {
			System.out.print(":D 1");
		}
		}

		
	}
	
	private void powerShot() {

		
		final int bullet_spread = 6;
		final int max_bullet_spread = 64;
		for (int i = 0; i < bullet_spread; i++) {
			Bullet bullet = new Bullet(1, 0, 3, .2f, .5f); // old density .01
			bullet.position = new Vector2f(0, .3f);
			bullet.position.rotate(orientation);
			bullet.position.sum(position);
			bullet.velocity = new Vector2f(0, 13 * 30f / 45);
			bullet.velocity.rotate(orientation + (float) Math.PI / max_bullet_spread * 2 * (i + .5f - bullet_spread / 2));
			my_bullets.add(bullet);
		}
		
		Bullet bullet = new Bullet(1, 0, 3, .3f, 1f);
		bullet.renderable.setRGBi(200, 54, 42);
		bullet.position = new Vector2f(0, .4f);
		bullet.position.rotate(orientation);
		bullet.position.sum(position);
		bullet.velocity = new Vector2f(0, 14 * 30f / 45);
		bullet.velocity.rotate(orientation);
		my_bullets.add(bullet);
	}
	
	private void weakShot() {
		Bullet bullet = new Bullet(10, 10, 3, .2f, .0001f);
		bullet.position = new Vector2f(0, .4f);
		bullet.position.rotate(orientation);
		bullet.position.sum(position);
		bullet.velocity = new Vector2f(0, 10 * 30f / 45);
		bullet.velocity.rotate(orientation);
		my_bullets.add(bullet);
	}
	
	public List<Bullet> getBullets() {
		return Collections.unmodifiableList(my_bullets);
	}
}
