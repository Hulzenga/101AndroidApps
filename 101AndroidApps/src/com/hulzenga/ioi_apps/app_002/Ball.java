package com.hulzenga.ioi_apps.app_002;

import android.util.Log;

public class Ball {

	private static final String TAG = "BALL";
	
	//fundamental physical properties
	private static final float MIN_RADIUS = 35.0f;
	private static final float MAX_RADIUS = 60.0f;
	private static final float RADIUS_RANGE = MAX_RADIUS - MIN_RADIUS;
	private static final float DENSITY = 1.0f;
	
	//space boundaries, shared between all balls
	private static float sXMin = 0.0f;
	private static float sXMax = 500.0f;
	private static float sYMin = 0.0f;
	private static float sYMax = 500.0f;
	
	//gravity vector components
	private static final float GX = 0.0f;
	private static final float GY = 400.1f;
	
	//dissipative forces
	private static final float FRICTION_COEFFICIENT = 10.0f; //friction
	private static final float WALL_BOUNCE_FACTOR = 0.75f; //energy loss during bounce against wall
	private static final float BALL_BOUNCE_FACTOR = 0.94f; //energy loss during bounce against other balls
	
	//to deal with floating point issues
	private static final float EPS = 0.5f;
	
	//position and radius
	public float x;
	public float y;
	public float r;
	
	//velocity vector components
	public float vX;
	public float vY;
	
	//not sure if needed
	private float mass;
	
	public Ball(float x, float y) {
		this.x = x;
		this.y = y;
		
		r = MIN_RADIUS + RADIUS_RANGE * ((float)Math.random());
		updateMass();
		
		vX = 800.0f*(float)(Math.random()-0.5f);
		vY = 800.0f*(float)(Math.random()-0.5f);
		
		rectify();
	}
	
	public static void setBounds(float xBound, float yBound) {
		sXMax = xBound;
		sYMax = yBound;
	}

	public void updatePosition(float dT) {		
		final float nX = x + vX*dT; //new X
		final float nY = y + vY*dT; //new Y
		
		final float fx = GX - FRICTION_COEFFICIENT*Math.signum(vX);
		final float fy = GY - FRICTION_COEFFICIENT*Math.signum(vY);
		
		//Check for bounces in the x coordinate
		if (nX-r < sXMin) {
			x = sXMin + r; 
			vX = -WALL_BOUNCE_FACTOR*vX;
		} else if (nX+r > sXMax) {
			x = sXMax - r;
			vX = -WALL_BOUNCE_FACTOR*vX;
		} else { //no bounce
			x = nX;
			vX += dT * fx;
		}
		
		//check for bounces in the y coordinate
		if (nY+r > sYMax - EPS) {			
			if (Float.compare(y, sYMax - r) == 0) { //this should fire if the ball is laying/rolling on the floor
				y = sYMax - r;
				vY = 0.0f;
			} else {
				y = sYMax - r;
				vY = -WALL_BOUNCE_FACTOR*vY + dT * fy/mass;
			}			
		} else if (nY-r < sYMin) {
			y = sYMin + r;
			vY = -WALL_BOUNCE_FACTOR*vY;
		} else {
			y = nY;
			vY += dT * fy;
		}
		
		//Log.d(TAG, "x: " + String.valueOf(x) + ", \t y: " + String.valueOf(y) + ", \t vX: " + String.valueOf(vX) + ", \t vY: " + String.valueOf(vY));
	}
	
	public void handleCollision(Ball otherBall) {
		final float dx = otherBall.x - this.x;
		final float dy = otherBall.y - this.y;
		final float dr = (float) Math.sqrt(dx*dx + dy*dy); 
		
		if (dr < r + otherBall.r) {
			
			final float totalMass = mass + otherBall.mass;
			final float m1Frac = mass / totalMass;
			final float m2Frac = otherBall.mass / totalMass;
			
			final float overlap = r + otherBall.r - dr;
			
			//define the unit collision vector (distance between the two balls)
			final float unitNormalX  = dx/dr;
			final float unitNormalY  = dy/dr;			
			final float unitTangentX = -unitNormalY;
			final float unitTangentY =  unitNormalX;
			
			//decompose ball velocities into normal and tangent components of the collision vector
			final float v1Normal   = unitNormalX * vX + unitNormalY * vY;
			final float v1Tangent  = unitTangentX* vX + unitTangentY* vY;
			final float v2Normal   = unitNormalX * otherBall.vX + unitNormalY * otherBall.vY;
			final float v2Tangent  = unitTangentX* otherBall.vX + unitTangentY* otherBall.vY;
			
			//normal velocities after collision using 1D collision formula
			final float v1Normal2  = v1Normal*(m1Frac-m2Frac)+2*m2Frac*v2Normal;
			final float v2Normal2  = v2Normal*(m2Frac-m1Frac)+2*m1Frac*v1Normal;
			
			//calculate velocity components of the balls after collision
			vX 			 = BALL_BOUNCE_FACTOR*(v1Normal2*unitNormalX + v1Tangent*unitTangentX);
			vY 			 = BALL_BOUNCE_FACTOR*(v1Normal2*unitNormalY + v1Tangent*unitTangentY);
			
			otherBall.vX = BALL_BOUNCE_FACTOR*(v2Normal2*unitNormalX + v2Tangent*unitTangentX);
			otherBall.vY = BALL_BOUNCE_FACTOR*(v2Normal2*unitNormalY + v2Tangent*unitTangentY);
			
			//displace both balls proportional to their mass fractions
			x -= m1Frac * overlap * unitNormalX;
			y -= m1Frac * overlap * unitNormalY;
			
			otherBall.x += m2Frac * overlap * unitNormalX;
			otherBall.y += m2Frac * overlap * unitNormalY;				
		}
	}
	
	public boolean isBallHere(float x, float y) {
		final float dx = x - this.x;
		final float dy = y - this.y;
		
		if (dx*dx + dy*dy < r*r) {
			return true;
		} else {
			return false;
		}
	}
	
	//when a ball is selected it's speed is set to 0
	public void select() {
		vX = 0.0f;
		vY = 0.0f;
	}
		
	public void dragBallTo(float x, float y) {
		
		//dragging a ball imparts speed, this transfer function is unphysical but "feels" nice
		vX = 0.6f*(vX + 30*(x - this.x));
		vY = 0.6f*(vY + 30*(y - this.y));
		
		this.x = x;
		this.y = y;
		rectify();
	}
	
	private void rectify() {
		if (x-r < sXMin) {
			x = sXMin + r; 
		} else if (x+r > sXMax) {
			x = sXMax - r;
		} 
		if (y-r < sYMin) {
			y = sYMin + r;
		} else if (y+r > sYMax - EPS) {			
			y = sYMax - r;			
		} 
	}
	
	public void inflate(float dR) {
		final float newR = r + dR;
		if (r + dR > MIN_RADIUS && r + dR < MAX_RADIUS) {
			r = newR;
		}
		updateMass();
	}
	
	private void updateMass() {
		mass = DENSITY * (float)Math.PI * r * r;
	}
}
