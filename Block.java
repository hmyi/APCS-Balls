/* Tim Yi
 * AP Computer Science
 * 05/07/2018
 * Project Balls - Block
 */

package breakout;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;

public class Block extends Target {

	private int number;
	private int maximum;
	private Color col;
	
	public Block(double x, int y, int sz, int num, int max) {
		super(x, y, sz);
		number = num;
		maximum = max;
		if (Math.random() * 3 > 2) {
			col = new Color(0, 0, 30+225*num/maximum);
		} else if (Math.random() * 3 > 1) {
			col = new Color(0, 30+225*num/maximum, 0);
		} else {
			col = new Color(30+225*num/maximum, 0, 0);
		}
	}

	public void setNumber(int n) { number = n; }
	public int getNumber() { return number; }
	
	public int getMaximum() { return maximum; }
	public Color getColor() { return col; }
	public void setColor(Color c) { col = c; }
	
	@Override
	public void beenHit() {
		number--;
	}
	
	@Override
	public void beenExtra2ed() {
		number = 0;
	}

	@Override
	public void draw(Graphics g) {
		Point2D.Double pt = getLoc();
		int size = getSize();
		if (col.getGreen() == 0 && col.getRed() == 0) {
			col = new Color(0, 0, 30+225*number/maximum);
		} else if (col.getRed() == 0) {
			col = new Color(0, 30+225*number/maximum, 0);
		} else {
			col = new Color(30+225*number/maximum, 0, 0);
		}
		g.setColor(col);
		g.fillRect((int) pt.x, (int) pt.y, size, size);
		g.setColor(Color.YELLOW);
		g.drawString(((Integer)number).toString(),
				(int) pt.x + size / 2 - g.getFontMetrics().stringWidth(((Integer)number).toString()) / 2,
				(int) (pt.y + size / 2 + 0.5*g.getFont().getSize())); 
	}
}
