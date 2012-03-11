package phyObj;

public class Asteroid extends PhyComposite {
	public Asteroid() {
		super();
		PhyObject tri = PhyPolygon.getEqTriangle(1);
		tri.setSize(3);
		this.addObject(tri);
		this.moveToCenterOfMass();
	}

}
