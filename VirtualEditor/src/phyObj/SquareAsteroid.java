package phyObj;

import java.util.ArrayList;
import java.util.List;


import main.SceneGraphNode;

public class SquareAsteroid extends Asteroid{
	private static final long serialVersionUID = 42L;
	public SquareAsteroid(final long the_id, final float the_size, final float the_hp_density) {
		super(the_id, PhyPolygon.getSquare(the_size), the_hp_density);
	}
	
	@Override
	public List<Asteroid> getFragments(float greater_than_size, final float the_impulse) {
		List<Asteroid> r = new ArrayList<Asteroid>(4);
		final float size;
		if (my_object.getSize() / 4 > greater_than_size) {
			size = my_object.getSize() / 2;
			for (int i = 0; i < 4; i++) {
				SquareAsteroid a = new SquareAsteroid(getOriginator(), size, my_hp_density);
				a.getObject().setRotationDegrees(my_object.getRotationDegrees() + 90 * i + 90);
				a.getObject().setPosition(my_object.getPosition().x, my_object.getPosition().y);
				a.getObject().setAngularVelocity(my_object.getAngularVelocity());
				a.getObject().setVelocity(my_object.getVelocity());
				final SceneGraphNode renderable = my_object.getRenderable();
				a.getObject().getRenderable().setRGBf(renderable.getRed(), renderable.getGreen(), renderable.getBlue());
				r.add(a);
			}
			final float offset = size / 2;
			Vector2f tmp = new Vector2f(-offset, -offset);
			tmp.rotate(my_object.getRotation());
			r.get(0).getObject().position.sum(tmp);
			tmp = new Vector2f(offset, -offset);
			tmp.rotate(my_object.getRotation());
			r.get(1).getObject().position.sum(tmp);
			tmp = new Vector2f(offset, offset);
			tmp.rotate(my_object.getRotation());
			r.get(2).getObject().position.sum(tmp);
			tmp = new Vector2f(-offset, offset);
			tmp.rotate(my_object.getRotation());
			r.get(3).getObject().position.sum(tmp);
			
		} else if (my_object.getSize() > greater_than_size) {
			size = my_object.getSize();
			for (int i = 0; i < 2; i++) {
				RightTriangleAsteroid a = new RightTriangleAsteroid(getOriginator(), size, my_hp_density);
				a.getObject().setRotationDegrees(my_object.getRotationDegrees());
				a.getObject().setPosition(my_object.getPosition().x, my_object.getPosition().y);
				a.getObject().setAngularVelocity(my_object.getAngularVelocity());
				a.getObject().setVelocity(my_object.getVelocity());
				final SceneGraphNode renderable = my_object.getRenderable();
				a.getObject().getRenderable().setRGBf(renderable.getRed(), renderable.getGreen(), renderable.getBlue());
				r.add(a);
			}
			Vector2f tmp = new Vector2f(-size / 6, -size / 6);
			tmp.rotate(my_object.getRotation());
			r.get(0).getObject().position.sum(tmp);
			tmp = new Vector2f(size / 6, size / 6);
			tmp.rotate(my_object.getRotation());
			r.get(1).getObject().position.sum(tmp);
			r.get(1).getObject().setRotationDegrees(my_object.getRotationDegrees() + 180);
		}
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
