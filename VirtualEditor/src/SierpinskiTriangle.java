
public class SierpinskiTriangle {
	public static SceneGraphNode create(int depth) {
		if (depth <= 1)
			return new Triangle();
		
		SceneGraphNode parent = new SceneGraphNode(false);
		SceneGraphNode child = SierpinskiTriangle.create(depth-1);
		child.scale = .5f;
		child.translateX = -.25f;
		child.translateY = -Triangle.SIN_60 / 6;
		parent.addChild(child);
		child = SierpinskiTriangle.create(depth-1);
		child.scale = .5f;
		child.translateY = Triangle.SIN_60 / 3;
		parent.addChild(child);
		child = SierpinskiTriangle.create(depth-1);
		child.scale = .5f;
		child.translateX = .25f;
		child.translateY = -Triangle.SIN_60 / 6;
		parent.addChild(child);
		
		return parent;
	}
}
