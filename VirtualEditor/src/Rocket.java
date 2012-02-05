

public class Rocket extends SceneGraphNode {
	private static final float BODY_RATIO = (float) 8 / 13;

	
	public Rocket() {
		this(true);
	}
	
	public Rocket(boolean pickable) {
		super(pickable);
		Triangle head = new Triangle(false);
		head.translateY = Triangle.SIN_60 / 3;
		addChild(head);
		Rectangle body = new Rectangle(false);
		body.scale = -BODY_RATIO;
		body.translateY = -BODY_RATIO / 2;
		body.rotation = 90;
		addChild(body);
		
		Triangle finleft = new Triangle(false);
		finleft.translateX = -.39f;
		finleft.translateY = -.4f;
		finleft.scale = .3f;
		finleft.rotation = 90f;
		addChild(finleft);
		
		Triangle finRight = new Triangle(false);
		finRight.translateX = .39f;
		finRight.translateY = -.4f;
		finRight.scale = .3f;
		finRight.rotation = 30f;
		addChild(finRight);
		
		
		
		
	}
}
