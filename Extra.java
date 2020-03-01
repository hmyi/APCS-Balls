/* Tim Yi
 * AP Computer Science
 * 05/07/2018
 * Project Balls - Extra
 */

package breakout;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;

public class Extra extends Target {

	private boolean gotHit = false;
	
	public Extra(double xLoc, double yLoc, int sz) {
		super(xLoc, yLoc, sz);
		gotHit = false;
	}
	
	public boolean isHit() { return gotHit; }
	
	@Override
	public void beenHit() {
		gotHit = true;
	}
	
	@Override
	public void beenExtra2ed() {
		//123
	}

	@Override
	public void draw(Graphics g) {
		Point2D.Double pt = getLoc();
		int size = getSize();
		g.setColor(Color.WHITE);
		for (int i = 0; i <= 2; i++) {
			g.drawOval((int) pt.x+i, (int) pt.y+i, size-2*i, size-2*i);
		}
		g.fillOval((int) pt.x+size / 4 + 1, (int) pt.y + size / 4 + 1, size / 2, size / 2);
	}

}
