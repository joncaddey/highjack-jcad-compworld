
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

public class Arrow extends SceneGraphNode {
	private static final float BODY_RATIO = (float) 8 / 13;

	
	// TODO NEED AT LEAST 3 OBJECTS.
	public Arrow() {
		this(true);
	}
	
	public Arrow(boolean pickable) {
		super(pickable);
		Triangle head = new Triangle(false);
		head.translateY = Triangle.SIN_60 / 3;
		addChild(head);
		Rectangle body = new Rectangle(false);
		body.scale = -BODY_RATIO;
		body.translateY = -BODY_RATIO / 2;
		body.rotation = 90;
		addChild(body);
	}
}
