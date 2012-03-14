package phyObj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import main.SceneGraphNode;
import main.Triangle;

public class EqTriangleAsteroid extends Asteroid {
	
	public EqTriangleAsteroid(final float the_size, final float the_hp_density) {
		super(PhyPolygon.getEqTriangle(the_size), the_hp_density);
		
	}

	@Override
	public List<Asteroid> getFragments(final float greater_than_size, final float the_impulse) {
		final float size = my_object.getSize() / 2;
		if (size <= greater_than_size) {
			return Collections.EMPTY_LIST;
		}
		List<Asteroid> r = new ArrayList<Asteroid>(4);
		for (int i = 0; i < 4; i++) {
			EqTriangleAsteroid a = new EqTriangleAsteroid(size, my_hp_density);
			a.getObject().setRotationDegrees(my_object.getRotationDegrees());
			a.getObject().setPosition(my_object.getPosition().x, my_object.getPosition().y);
			a.getObject().setAngularVelocity(my_object.getAngularVelocity());
			a.getObject().setVelocity(my_object.getVelocity());
			final SceneGraphNode renderable = my_object.getRenderable();
			a.getObject().getRenderable().setRGBf(renderable.getRed(), renderable.getGreen(), renderable.getBlue());
			r.add(a);
		}
		Vector2f tmp = new Vector2f(-.5f * size, -Triangle.SIN_60 * size / 3);
		tmp.rotate(my_object.getRotation());
		r.get(0).getObject().position.sum(tmp);
		tmp = new Vector2f(.5f * size, -Triangle.SIN_60 * size / 3);
		tmp.rotate(my_object.getRotation());
		r.get(1).getObject().position.sum(tmp);
		tmp = new Vector2f(0, Triangle.SIN_60 * 2 * size / 3);
		tmp.rotate(my_object.getRotation());
		r.get(2).getObject().position.sum(tmp);
		r.get(3).getObject().setRotationDegrees(my_object.getRotationDegrees() + 180);
		
		// TODO this code is repeated 3 times.  should be function.
		for (Asteroid a : r) {
			Vector2f dv = new Vector2f(a.getObject().getPosition());
			dv.sumScale(my_object.getPosition(), -1);
			dv.setLength(the_impulse / a.getObject().getMass());
			dv.sum(a.getObject().getVelocity());
			a.getObject().setVelocity(dv);
		}
		
		return r;
	}

}
