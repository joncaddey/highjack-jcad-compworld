package phyObj;

import java.util.List;

import main.SceneGraphNode;

public abstract class Asteroid {
	
	private int my_hp;
	private final int my_max_hp;
	protected final float my_hp_density;
	
	protected final PhyObject my_object;
	
	private final float my_red, my_blue, my_green;
	
	
	protected Asteroid(final PhyObject the_object, final float the_hp_density) {
		my_object = the_object;
		my_hp_density = the_hp_density;
		my_max_hp = (int) (the_hp_density * the_object.getMass());
		my_hp = my_max_hp; 
		my_red = the_object.getRenderable().red;
		my_blue = the_object.getRenderable().blue;
		my_green = the_object.getRenderable().green;
		decrementHP(0);
	}
	
	

	public boolean isAlive()
	{
		return (my_hp > 0);
	}
	
	public void decrementHP(int the_dmg){
		my_hp = my_hp - the_dmg;
		final float p = 1 - (float)my_hp / my_max_hp;
		my_object.getRenderable().setRGBf(p * (1 - my_red) + my_red, p * (1 - my_green) + my_green, p * (1 - my_blue) + my_blue);
	}
	
	public PhyObject getObject() {
		return my_object;
	}
	
	public SceneGraphNode getRenderable() {
		return my_object.getRenderable();
	}
	
	public List<Asteroid> getFragments() {
		return getFragments(.25f);
	}
	public abstract List<Asteroid> getFragments(final float the_min_size);

}
