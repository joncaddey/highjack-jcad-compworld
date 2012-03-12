package phyObj;


public class Bullet extends PhyCircle{

	private int my_damage;
	private int my_time;
	
	
	private boolean my_alive = true;
	public Bullet(final int the_damage, final int the_time, final float the_radius, final float the_density) {
		super(the_radius * 2);
		my_damage = the_damage;
		my_time = the_time;
		density = the_density;
		setSize(the_radius * 2);
		renderable.setRGBf(1, .6f, .3f);
	}
	
	public Bullet() {
		this(10, 45, .2f, .0001f);
		
	}
	
	@Override
	public void updateState(final float the_time) {
		super.updateState(the_time);
		my_time--;
		if (my_time < 0) {
			my_alive = false;
		}
	}
	
	public boolean isAlive() {
		return my_alive;
	}
	
	public int getDamage() {
		return my_damage;
	}
}
