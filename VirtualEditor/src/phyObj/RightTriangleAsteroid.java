package phyObj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.SceneGraphNode;

public class RightTriangleAsteroid extends Asteroid {

	public RightTriangleAsteroid(final float the_size, final float the_hp_density) {
		super(PhyPolygon.getRightTriangle(the_size), the_hp_density);
	}

	@Override
	public List<Asteroid> getFragments(float greater_than_size) {
		final float size = my_object.getSize() / 2;
		if (size <= greater_than_size) {
			return Collections.EMPTY_LIST;
		}
		List<Asteroid> r = new ArrayList<Asteroid>(4);
		for (int i = 0; i < 4; i++) {
			RightTriangleAsteroid a = new RightTriangleAsteroid(size, my_hp_density);
			a.getObject().setRotationDegrees(my_object.getRotationDegrees());
			a.getObject().setPosition(my_object.getPosition().x, my_object.getPosition().y);
			a.getObject().setAngularVelocity(my_object.getAngularVelocity());
			a.getObject().setVelocity(my_object.getVelocity());
			final SceneGraphNode renderable = my_object.getRenderable();
			a.getObject().getRenderable().setRGBf(renderable.getRed(), renderable.getGreen(), renderable.getBlue());
			r.add(a);
		}
		Vector2f tmp = new Vector2f(-size / 3, 2 * size / 3);
		tmp.rotate(my_object.getRotation());
		r.get(0).getObject().position.sum(tmp);
		tmp = new Vector2f(2 * size / 3, -size / 3);
		tmp.rotate(my_object.getRotation());
		r.get(1).getObject().position.sum(tmp);
		tmp = new Vector2f(0, -size / 3);
		tmp.rotate(my_object.getRotation());
		r.get(2).getObject().position.sum(tmp);
		r.get(2).getObject().setRotationDegrees(my_object.getRotationDegrees() + 90);
		tmp = new Vector2f(-size / 3, 0);
		tmp.rotate(my_object.getRotation());
		r.get(3).getObject().position.sum(tmp);
		r.get(3).getObject().setRotationDegrees(my_object.getRotationDegrees() + 270);
		return r;
	}
}
