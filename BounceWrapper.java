/* Tim Yi
 * AP Computer Science
 * 05/07/2018
 * Project Balls - Bounce Wrapper
 */

package breakout;

import java.awt.EventQueue;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class BounceWrapper extends JFrame {

	public final int PANEL_SIZE = 1500;
	public final int BORDER_SPACE = 0;
	public final int NUM_BLOCKS = 8;
	
	public BounceWrapper() {
        setSize(PANEL_SIZE / 2, PANEL_SIZE + BORDER_SPACE);
		add(new BouncePanel(PANEL_SIZE / 2, PANEL_SIZE, NUM_BLOCKS));
        setResizable(false);
        setTitle("Blocks and balls");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                BounceWrapper go = new BounceWrapper();
                go.setVisible(true);
            }
        });
	}

}