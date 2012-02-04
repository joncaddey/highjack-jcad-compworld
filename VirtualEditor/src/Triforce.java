
public class Triforce extends SceneGraphNode {
	public Triforce() {
		this(true);
	}

	public Triforce(boolean pickable) {
		super(pickable);
		SceneGraphNode child = new Triangle(false);
		child.translateX = -.5f;
		child.translateY = -Triangle.SIN_60 / 3;
		addChild(child);
		child = new Triangle(false);
		child.translateY = Triangle.SIN_60 * 2 / 3;
		addChild(child);
		child = new Triangle(false);
		child.translateX = .5f;
		child.translateY = -Triangle.SIN_60 / 3;
		addChild(child);
	}
}
