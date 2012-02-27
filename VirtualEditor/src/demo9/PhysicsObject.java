package demo9;
public class PhysicsObject {
	float inverseMass;
	Vector2f position;
	Vector2f velocity;
	Vector2f acceleration;
	Vector2f centerOfMass;
	float inverseMomentOfInertia;
	float orientation;
	float angularVelocity;
	SceneGraphNode renderable;
	
	public PhysicsObject() {
		inverseMass = 1;
		position = new Vector2f();
		velocity = new Vector2f();
		acceleration = new Vector2f();
		centerOfMass = new Vector2f();
		// inverseMomentOfInertia needs to be set in subclasses
	}

	public void updateState(float timePeriod) {
		position.sumScale(velocity, timePeriod);
		position.sumScale(acceleration, timePeriod * timePeriod / 2);
		velocity.sumScale(acceleration, timePeriod);
		orientation += angularVelocity * timePeriod;
		clearCaches();
	}
	
	public void updateRenderable() {
		if (renderable != null) {
			renderable.translateX = position.x;
			renderable.translateY = position.y;
			renderable.rotation = (float)(180 * orientation / Math.PI);
		}
	}

	public void clearCaches() {
	}

	public CollisionInfo getCollision(PhysicsObject other) {
		if (inverseMass == 0 && other.inverseMass == 0) {
			return null;
		}
		// HalfSpace, Circle, Triangle, Composite
		if (this instanceof HalfSpace) {
			if (other instanceof Circle) {
				return getCollision((HalfSpace)this, (Circle)other);
			} else if (other instanceof PhyPolygon) {
				return getCollision((HalfSpace)this, (PhyPolygon)other);
			}			
		} else if (this instanceof Circle) {
			if (other instanceof HalfSpace) {
				CollisionInfo cInfo = getCollision((HalfSpace)other, (Circle)this);
				if (cInfo != null)
					cInfo.normal.scale(-1);
				return cInfo;
			} else if (other instanceof Circle) {
				return getCollision((Circle)this, (Circle)other);
			} else if (other instanceof PhyPolygon) {
				return getCollision((Circle)this, (PhyPolygon)other);
			}
		} else if (this instanceof PhyPolygon) {
			if (other instanceof HalfSpace) {
				CollisionInfo cInfo = getCollision((HalfSpace)other, (PhyPolygon)this);
				if (cInfo != null)
					cInfo.normal.scale(-1);
				return cInfo;
			} else if (other instanceof Circle) {
				CollisionInfo cInfo = getCollision((Circle)other, (PhyPolygon)this);
				if (cInfo != null)
					cInfo.normal.scale(-1);
				return cInfo;
			} else if (other instanceof PhyPolygon) {
				return getCollision((PhyPolygon)this, (PhyPolygon)other);
			}
		}			
		return null;
	}
	
	private static CollisionInfo getCollision(HalfSpace a, Circle b) {
		float distance = a.normal.dot(b.position);
		distance -= a.intercept; // distance is same as norm of line dot (center of circle - point on line)
		if (distance >= b.radius)
			return null;
		CollisionInfo cInfo = new CollisionInfo();
		cInfo.depth = b.radius - distance;
		cInfo.normal = new Vector2f(a.normal);
		cInfo.positionA = new Vector2f(b.position);
		cInfo.positionA.sumScale(cInfo.normal, -b.radius + cInfo.depth);
		cInfo.positionB = new Vector2f(b.position);
		cInfo.positionB.sumScale(cInfo.normal, -b.radius);
		return cInfo;
	}
	
	private static CollisionInfo getCollision(HalfSpace a, PhyPolygon b) {
		Vector2f[] vertices = b.getVertices();
		float[] distances = new float[vertices.length];
		
		
		for (int i = 0; i < vertices.length; i++)
			distances[i] = a.normal.dot(vertices[i]) - a.intercept;
		
		int minIndex = 0;
		for (int i = 1; i < distances.length; i++)
			if (distances[i] < distances[minIndex])
				minIndex = i;
		if (distances[minIndex] >= 0)
			return null;
		
		CollisionInfo cInfo = new CollisionInfo();
		cInfo.depth = -distances[minIndex];
		cInfo.normal = new Vector2f(a.normal);
		cInfo.positionA = new Vector2f(vertices[minIndex]);
		cInfo.positionA.sumScale(cInfo.normal, cInfo.depth);
		cInfo.positionB = new Vector2f(vertices[minIndex]);
		return cInfo;
	}
	
	private static CollisionInfo getCollision(Circle a, Circle b) {
		Vector2f tmp = new Vector2f(b.position);
		tmp.sumScale(a.position, -1); // reaches from A center to B center
		float distance = tmp.length() - a.radius - b.radius; // negative overlap along tmp
		if (distance >= 0)
			return null;
		CollisionInfo cInfo = new CollisionInfo();
		cInfo.depth = -distance; // length of overlap
		tmp.normalize(); // normal from A center to B
		cInfo.normal = tmp;
		cInfo.positionA = new Vector2f(a.position);
		cInfo.positionA.sumScale(cInfo.normal, a.radius); // where A would kiss B
		cInfo.positionB = new Vector2f(b.position);
		cInfo.positionB.sumScale(cInfo.normal, -b.radius); // where B would kiss A
		return cInfo;
	}
	
	private static CollisionInfo getCollision(Circle a, PhyPolygon b) {
		Vector2f[] vertices = b.getVertices();
		Vector2f[] normals = b.getNormals();
		float[] distances = new float[vertices.length];
		
		for (int i = 0; i < vertices.length; i++) {
			Vector2f tmp = new Vector2f(a.position);
			tmp.sumScale(vertices[i], -1);
			distances[i] = tmp.dot(normals[i]) - a.radius;
		}
		int maxIndex = 0;
		for (int i = 1; i < distances.length; i++)
			if (distances[i] > distances[maxIndex])
				maxIndex = i;
		if (distances[maxIndex] >= 0)
			return null;
		
		// our code
		int nextIndex = (maxIndex + 1) % vertices.length;
		Vector2f side = new Vector2f(-normals[maxIndex].y, normals[maxIndex].x);
		float left = side.dot(vertices[maxIndex]);
		float right = side.dot(vertices[nextIndex]);
		float center = side.dot(a.position);
		if ((left <= center && center <= right) || (right <= center && center <= left)) {
			// circle to side collision
			CollisionInfo cInfo = new CollisionInfo();
			cInfo.depth = -distances[maxIndex];
			cInfo.normal = new Vector2f(normals[maxIndex]);
			cInfo.normal.scale(-1);
			cInfo.positionA = new Vector2f(a.position);
			cInfo.positionA.sumScale(cInfo.normal, a.radius);
			cInfo.positionB = new Vector2f(a.position);
			cInfo.positionB.sumScale(cInfo.normal, a.radius - cInfo.depth);
			return cInfo;
		} else if (a.radius > 0){
			// circle to corner collision (corner is a Circle with 0 radius)
			Circle corner = new Circle(0);
			corner.position = vertices[maxIndex];
			CollisionInfo cInfo = getCollision(a, corner);
			corner.position = vertices[nextIndex];
			CollisionInfo tmp = getCollision(a, corner);
			if (cInfo == null || (tmp != null && tmp.depth > cInfo.depth)) {
				cInfo = tmp;
			}
			return cInfo;
		} else {
			return null;
		}
	}
	
	private static CollisionInfo getCollision(PhyPolygon a, PhyPolygon b) {
		Vector2f[] verticesA = a.getVertices();
		Vector2f[] verticesB = b.getVertices();
		
		CollisionInfo c = getCollision(verticesA, verticesB, b.getNormals());
		if (c != null) {
			CollisionInfo d = getCollision(verticesB, verticesA, a.getNormals());
			if (d == null) {
				return null;
			}
			if (d != null && d.depth < c.depth) {
				c = d;
				c.normal.scale(-1);
				Vector2f t = c.positionA;
				c.positionA = c.positionB;
				c.positionB = t;
			}
		}
		return c;
	}
	

	/**
	 * Gives collision, assuming the collision occurs from the deepest vertex relative to a side penetrating that side,
	 * where the side is the side with the most shallow deepest vertex among all the sides.  The depth of a vertex
	 * is its negative distance from the normal.
	 * @param verA vertices forming a convex polygon, assuming to penetrate B.
	 * @param verB vertices forming a convex polygon.
	 * @param normalB normals of the edges of a convex polygon.
	 * @return a collision, or null if there is no collision.
	 */
	private static CollisionInfo getCollision(Vector2f[] verA, Vector2f[] verB, Vector2f[] normalB) {
		float shallowestDistance = Float.NEGATIVE_INFINITY;
		int shallowestSide = -1;
		int shallowestVer = -1;
		for (int side = 0; side < normalB.length; side++) {
			int deepestVer = -1;
			float deepestDistance = 1;
			for (int ver = 0; ver < verA.length; ver++) {
				Vector2f tmp = new Vector2f(verA[ver]);
				tmp.sumScale(verB[side], -1);
				float distance = tmp.dot(normalB[side]);
				if (distance < deepestDistance) {
					deepestDistance = distance;
					deepestVer = ver;
				}
			}
			if (deepestDistance > 0) {
				return null;
			}
			if (deepestDistance > shallowestDistance) {
				shallowestDistance = deepestDistance;
				shallowestSide = side;
				shallowestVer = deepestVer;
			}
		}
		
		CollisionInfo c = new CollisionInfo();
		c.depth = -shallowestDistance * 1f;
		c.normal = new Vector2f(normalB[shallowestSide]);
		c.normal.scale(-1);
		c.positionA = new Vector2f(verA[shallowestVer]);
		c.positionB = new Vector2f(verA[shallowestVer]);
		c.positionB.sumScale(c.normal, -c.depth);
		return c;
	}
	
	public void resolveCollision(PhysicsObject other, CollisionInfo cInfo) {
		// Calculate the velocity of the collision point on the calling object.
		Vector2f relativeCollisionPositionA = new Vector2f(cInfo.positionA);
		relativeCollisionPositionA.sumScale(position, -1);
		relativeCollisionPositionA.sumScale(centerOfMass, -1);
		Vector2f linearVelocityA = new Vector2f(-relativeCollisionPositionA.y, relativeCollisionPositionA.x);
		linearVelocityA.scale(angularVelocity);
		linearVelocityA.sum(velocity);
		// Calculate the velocity of the collision point on the other object.
		Vector2f relativeCollisionPositionB = new Vector2f(cInfo.positionB);
		relativeCollisionPositionB.sumScale(other.position, -1);
		relativeCollisionPositionB.sumScale(other.centerOfMass, -1);
		Vector2f linearVelocityB = new Vector2f(-relativeCollisionPositionB.y, relativeCollisionPositionB.x);
		linearVelocityB.scale(other.angularVelocity);
		linearVelocityB.sum(other.velocity);
		// Calculate the relative velocity between the calling object and
		// other object, as if the calling object were stationary and only
		// the other object were moving.
		Vector2f relativeVelocity = new Vector2f(linearVelocityB);
		relativeVelocity.sumScale(linearVelocityA, -1);
		// Calculate the component of the relative velocity that lays along
		// the collision normal.
		float compRelVelAlongNormal = relativeVelocity.dot(cInfo.normal);
		// Calculate the resulting impulse per unit mass.
		float impulse = (float)(1.7 * compRelVelAlongNormal / (
				inverseMass + other.inverseMass + 
				Math.pow(relativeCollisionPositionA.cross(cInfo.normal), 2) * inverseMomentOfInertia +
				Math.pow(relativeCollisionPositionB.cross(cInfo.normal), 2) * other.inverseMomentOfInertia));
		// Adjust the linear and angular velocities of each object in proportion
		// to their effective masses.
		velocity.sumScale(cInfo.normal, impulse * inverseMass);
		other.velocity.sumScale(cInfo.normal, -impulse * other.inverseMass);
		angularVelocity += relativeCollisionPositionA.cross(cInfo.normal) * impulse * inverseMomentOfInertia;
		other.angularVelocity -= relativeCollisionPositionB.cross(cInfo.normal) * impulse * other.inverseMomentOfInertia;

		// Calculate the amount of object overlap per unit mass.
		float depth = (float)(cInfo.depth / (
				inverseMass + other.inverseMass + 
				Math.pow(relativeCollisionPositionA.cross(cInfo.normal), 2) * inverseMomentOfInertia +
				Math.pow(relativeCollisionPositionB.cross(cInfo.normal), 2) * other.inverseMomentOfInertia));
		// Adjust the position and orientation  of each object in proportion
		// to their effective masses to remove overlap.
		position.sumScale(cInfo.normal, -depth * inverseMass);
		other.position.sumScale(cInfo.normal, depth * other.inverseMass);
		orientation -= relativeCollisionPositionA.cross(cInfo.normal) * depth * inverseMomentOfInertia;
		other.orientation += relativeCollisionPositionB.cross(cInfo.normal) * depth * other.inverseMomentOfInertia;
		
		clearCaches();
		other.clearCaches();
	}
}
