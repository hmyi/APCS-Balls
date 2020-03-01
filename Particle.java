/* Tim Yi
 * AP Computer Science
 * 05/07/2018
 * Project Balls - Particle
 */

package breakout;

import java.awt.Graphics;
import java.awt.geom.Point2D;

public class Particle {

	private Point2D.Double loc;
	private double speed;
	private double angle;
	private double magnitude;
	private boolean active;

/*	
	public Particle(double x, double y, double xvel, double yvel, double ch) {
		loc = new Point2D.Double(x, y);
//		vel = new Point2D.Double(xvel, yvel);
		magnitude = ch;
		active = false;
	}
*/	

	public Particle(double x, double y, double spd, double ang, int size) {
		loc = new Point2D.Double(x, y);
		speed = spd;
		angle = ang;
		magnitude = size;
		active = false;
	}
	
	public Particle() {
		loc = new Point2D.Double();
		speed = 0;
		angle = 0.0;
		magnitude = 0.0;
		active = false;
	}
	
	//get and set pairs for private variables
	public void setLoc(double newX, double newY) { loc = new Point2D.Double(newX, newY); }
	public Point2D.Double getLoc() { return loc; }	
	public void setSpeed(double newSpd) { speed = newSpd; }
	public double getSpeed() { return speed; }
	public void setAngle(double newAng) { angle = newAng; }
	public double getAngle() { return angle; }
	public void setMag(double newCh) { magnitude = newCh; }
	public double getMag() { return magnitude; }

	public Point2D.Double getVel() { return new Point2D.Double(speed*Math.cos(angle), speed*Math.sin(angle)); }
	
	public void activate() { active = true; }
	public void deactivate() { active = false; }
	public boolean isActive() { return active; }
	
	public void draw(Graphics g) {
		g.fillOval((int)(getLoc().x), (int)(getLoc().y), (int)magnitude, (int)magnitude);
	}

	public void update() { loc = new Point2D.Double(loc.x + speed*Math.cos(angle), loc.y + speed*Math.sin(angle)); }
	public void bounce(double dev) { angle = angle + Math.random()*(dev) - dev / 2; }
}
