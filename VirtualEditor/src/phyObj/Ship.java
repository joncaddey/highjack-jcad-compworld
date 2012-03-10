package phyObj;

import main.SceneGraphNode;
import main.Triangle;

public class Ship extends PhyComposite {

	private static final float ANGULAR_DECAY = 1f;
	private static final float LINEAR_DECAY = .7f;
	
	private static final float FORWARD_THRUST = LINEAR_DECAY + 1f;
	private static final float MAX_VELOCITY = 8f;
	
	private static final float ANGULAR_THRUST = ANGULAR_DECAY + 1.5f;
	private static final float MAX_ANGULAR_VELOCITY = 15;
	
	
	
	private boolean my_forward_toggle;
	private boolean my_left_toggle;
	private boolean my_right_toggle;
	private boolean my_reverse_toggle;
	
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
		finRight.renderable.red = finLeft.renderable.red;
		finRight.renderable.green = finLeft.renderable.green;
		finRight.renderable.blue = finLeft.renderable.blue;
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
		
		SceneGraphNode flame = new Triangle(false);
		flame.rotation = 180;
		flame.scale = .25f;
		flame.translateY = -.8f;
		flame.setRGBf(1, .6f, 0);
		renderable.addChild(flame);
		
		flame = new Triangle(false);
		flame.rotation = 180;
		flame.scale = .25f;
		flame.translateY = -.7f;
		flame.translateX = -.1f;
		flame.setRGBf(1, .6f, 0);
		renderable.addChild(flame);
		
		flame = new Triangle(false);
		flame.rotation = 180;
		flame.scale = .25f;
		flame.translateY = -.7f;
		flame.translateX = .1f;
		flame.setRGBf(1, .6f, 0);
		renderable.addChild(flame);
		
		
		Triangle glass = new Triangle(false);
		glass.scale = .55f;
		glass.translateY = .1f;
		glass.setRGBi(15, 21, 23);
		renderable.addChild(glass);
		
		
		moveToCenterOfMass();
		setSize(.5f);

	}
	
	@Override
	public void updateState(final float the_time) {
		decay();
		if (my_forward_toggle) {
			forward();
		}
		if (my_right_toggle) {
			right();
		}
		if (my_left_toggle) {
			left();
		}
		if (my_reverse_toggle) {
			reverse();
		}
		
		
		super.updateState(the_time);
	}
	public void toggleForward(boolean the_on) {
		my_forward_toggle = the_on;
	}
	
	public void toggleLeft(boolean the_on) {
		my_left_toggle = the_on;
	}
	
	public void toggleRight(boolean the_on) {
		my_right_toggle = the_on;
	}
	
	public void toggleReverse(boolean the_on) {
		my_reverse_toggle = the_on;
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
	
	private void decay() {
		
		///if (!my_left_toggle && !my_right_toggle) {
			float angleSign = Math.signum(angularVelocity);
			if (Math.abs(angularVelocity) < ANGULAR_DECAY) {
				angularVelocity = 0;
			} else {
				angularVelocity -= ANGULAR_DECAY * angleSign;
			}
		//}
		//if (!my_forward_toggle && !my_reverse_toggle) {
			if (velocity.length() < LINEAR_DECAY) {
				velocity.scale(0);
			} else {
				velocity.scale((velocity.length() - LINEAR_DECAY) / velocity.length());
			}
		//}
	}
}
