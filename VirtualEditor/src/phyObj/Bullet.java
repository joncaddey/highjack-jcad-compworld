package phyObj;



public class Bullet extends PhyCircle{

	public static int MAX_TIME = 4;
	private int my_damage;
	private float my_time;
	private int my_bounces;
	
	
	private boolean my_alive = true;
	public Bullet(final int the_damage, final int the_bounces, final float the_time, final float the_radius, final float the_density) {
		super(the_radius * 2);
		my_damage = the_damage;
		my_bounces = the_bounces;
		my_time = the_time;
		if (my_time > MAX_TIME) {
			my_time = MAX_TIME;
		}
		density = the_density;
		setSize(the_radius * 2);
		renderable.setRGBf(1, .6f, .3f);
	}
	
	
	
	@Override
	public void updateState(final float the_time) {
		super.updateState(the_time);
		my_time -= the_time;
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
	
	public void bounce() {
		my_bounces--;
		my_time = MAX_TIME;
		if (my_bounces < 0) {
			my_alive = false;
		}
	}
}
