/* Tim Yi
 * AP Computer Science
 * 05/07/2018
 * Project Balls - Target
 */

package breakout;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

public abstract class Target {

	private Point2D.Double loc;
	private int size;

	public Target(double xLoc, double yLoc, int sz) {
		loc = new Point2D.Double(xLoc, yLoc);
		size = sz;
	}
	
	public abstract void beenHit();
	
	public abstract void beenExtra2ed();
	
	public abstract void draw(Graphics g);

	public void setLoc(double x, double y) {
		loc = new Point2D.Double(x, y);
	}
	
	public Point2D.Double getLoc() { return new Point2D.Double(loc.x, loc.y); }
	
	public void setSize(int sz) { size = sz; }
	
	public int getSize() { return size; }
	
	public Rectangle getBounds() { return new Rectangle((int)(loc.getX()), (int)(loc.getY()), size, size); }
}
