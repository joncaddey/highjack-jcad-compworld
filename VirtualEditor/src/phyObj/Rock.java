package phyObj;

import java.util.List;

public abstract class Rock extends PhyObject  {
	
	public static final int THE_HEALTH_PER_SIZE = 10;
	
	private int my_hp;
	
	public Rock (final float the_size, final float the_density){
		my_hp = calcHp(the_size);
	}
	
	private int calcHp(float the_size) {
		return (int)(the_size * THE_HEALTH_PER_SIZE);
	}

	public boolean isAlive()
	{
		return (my_hp > 0);
	}
	
	public void decrementHP(int the_dmg){
		my_hp = my_hp - the_dmg;
	}
	
	public abstract List<PhyObject> getFrag();
}
