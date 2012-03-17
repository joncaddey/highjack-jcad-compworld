package phyObj;

import java.io.Serializable;
import java.util.List;


import main.SceneGraphNode;

public abstract class Asteroid implements Serializable{
	private static final long serialVersionUID = 42L;
	
	private int my_hp;
	private final int my_max_hp;
	protected final float my_hp_density;
	
	protected final PhyObject my_object;
	
	
	protected Asteroid(final PhyObject the_object, final float the_hp_density) {
		my_object = the_object;
		my_hp_density = the_hp_density;
		my_max_hp = (int) (the_hp_density * the_object.getMass());
		my_hp = my_max_hp; 
		decrementHP(0);
	}
	
	

	public boolean isAlive()
	{
		return (my_hp > 0);
	}
	
	public void decrementHP(int the_dmg){
		my_hp = my_hp - the_dmg;
		my_object.getRenderable().setBrightness((1 - (float)my_hp / my_max_hp) * .5f + .5f);
	}
	
	public PhyObject getObject() {
		return my_object;
	}
	
	public SceneGraphNode getRenderable() {
		return my_object.getRenderable();
	}
	
	public List<Asteroid> getFragments() {
		return getFragments(.25f, 30f / 45);
	}
	public abstract List<Asteroid> getFragments(final float the_min_size, final float the_impulse);



	public float getMaxHP() {
		return my_max_hp;
	}

}
