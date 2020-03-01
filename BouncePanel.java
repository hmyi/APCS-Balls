/* Tim Yi
 * AP Computer Science
 * 05/07/2018
 * Project Balls - Bounce Panel
 */

package breakout;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class BouncePanel extends JPanel implements ActionListener {

	private static final int PARTICLE_SIZE = 17;
	private static final int PARTICLE_SPEED = 6;
	private static final int PARTICLE_DELAY = 12;

	private static final double BLOCK_DOUBLE_CHANCE = 0.3;
	private static final double BLOCK_CHANCE = 0.55;
	private static final int BLOCK_PADDING = 5;

	private static final double ANGLE_NOISE = Math.PI / 10;
	private static final double ANGLE_NOISE_CHANCE = 0.2;
	private static final double ANGLE_TOL = Math.PI / 12;

	private Timer timer;
	private Point mouseCoords;
	private ArrayList<Particle> particles;
	private int particleIndex;
	private int particleTick;
	private int activeParticles;
	private int particleCount;
	private int blockSize;
	private int blocksPerRow;
	private int blockLevel;
	private int newExtra;
	private int modeIndex;
	private int cleanIndex;
	private double newExtra2Poss;
	private double newExtra3Poss;
	private boolean extra2Mode;
	private boolean extra2Added;
	private boolean extra3Added;
	private boolean isClean;
	private boolean getInput;
	private boolean canShoot;
	private boolean firstBack;
	private boolean canSave = true;
	private int addParticles;
	private double origin;
	private int bottom;
	private int top;
	private double startingAngle;
	private ArrayList<Target> targets;
	
	private JButton resetBtn;
	private JButton modeBtn;
	private JButton cleanBtn;
	private JButton saveBtn;
	private JButton loadBtn;

	public BouncePanel(int xDim, int yDim, int numPerRow) {
		setBackground(Color.BLACK);
		addMouseListener(new MAdapter());
		addMouseMotionListener(new MAdapter());
		setFocusable(true);
		setDoubleBuffered(true);

		timer = new Timer(1, this);
		timer.start();
		
		extra2Mode = false;
		extra2Added = false;
		extra3Added = false;
		
		isClean = true;

		getInput = true;
		canShoot = true;
		firstBack = true;
		
		mouseCoords = new Point();

		particles = new ArrayList<Particle>();

		particleIndex = 0;
		particleTick = 0;
		particleCount = 1;
		addParticles = 0;
		
		blocksPerRow = numPerRow;
		blockSize = (int)((xDim - 2 * BLOCK_PADDING) / blocksPerRow);	
		blockLevel = 1;
		top = yDim / 15;
		bottom = 8 * yDim / 9;
		origin = xDim / 2;
		for (int i = 0; i < particleCount; i++) {
			particles.add(new Particle(origin, bottom, PARTICLE_SPEED, 0, PARTICLE_SIZE));
		}

		targets = new ArrayList<Target>();
		newExtra = (int)(Math.random()*blocksPerRow);
		newExtra2Poss = 0.02;
		newExtra3Poss = 0.02;
		
		for (int i = 0; i < blocksPerRow; i++) {
			addNewTarget(i, blockLevel, newExtra, newExtra2Poss, newExtra3Poss);
		}
		
		modeIndex = 0;
		
		initBtns();
		add(resetBtn);
		add(modeBtn);
		add(cleanBtn);
    	add(saveBtn);
    	add(loadBtn);
	}

	public void paintComponent(Graphics g) { // draw graphics in the panel
		super.paintComponent(g); // call superclass to make panel display correctly
		g.drawLine(0, bottom, getWidth(), bottom);
		g.setColor(Color.WHITE);
		g.drawString("" + particleCount, 100, bottom + 20);
		drawTargets(g);
		if (getInput) {
			if (canShoot) { //the line being white (outside of extreme angles, vise versa)
				g.setColor(Color.WHITE);
			} else {
				g.setColor(Color.RED);
			}
			g.fillOval((int) origin, bottom, PARTICLE_SIZE, PARTICLE_SIZE); //the balls
			g.drawLine((int) origin + PARTICLE_SIZE / 2, bottom + PARTICLE_SIZE / 2, mouseCoords.x, mouseCoords.y); //the line
		} else {
			g.setColor(Color.CYAN);
			drawParticles(g);
		}
	}

	public void drawParticles(Graphics g) { //draw particles
		for (Particle p : particles) {
			p.draw(g);
		}
	}
	
	public void drawTargets(Graphics g) { //draw targets
		for (Target t : targets) {
			t.draw(g);
		}
	}
	
	public boolean blockHit(Particle p, Target t) { //if hit the wall, bounce off
		if (t.getBounds().intersects(new Rectangle((int)(p.getLoc().x + p.getVel().x), (int)(p.getLoc().y + p.getVel().y), PARTICLE_SIZE, PARTICLE_SIZE))) {
			if (Math.abs(p.getLoc().x + PARTICLE_SIZE / 2 - t.getLoc().x - t.getSize() / 2) < 
					Math.abs(p.getLoc().y + PARTICLE_SIZE / 2 - t.getLoc().y - t.getSize() / 2)) {
				p.setAngle(-p.getAngle());
				p.bounce((int)(Math.random() + ANGLE_NOISE_CHANCE) * ANGLE_NOISE);
			} else {
				p.setAngle(Math.PI - p.getAngle());
				p.bounce((int)(Math.random() + ANGLE_NOISE_CHANCE) * ANGLE_NOISE);
			}
			if (extra2Mode) { //my special mode
				t.beenExtra2ed();
			} else {
				t.beenHit();
			}
		}
		return ((Block)t).getNumber() == 0;
	}
	
	public boolean extraHit(Particle p, Target t) { //if hit a white ball, number of balls + 1
		if (t.getBounds().intersects(new Rectangle((int)(p.getLoc().x + p.getVel().x), (int)(p.getLoc().y + p.getVel().y), PARTICLE_SIZE, PARTICLE_SIZE))) {
			if (t instanceof Extra) {
				addParticles++;
				return true;
			}
		}
		return false;
	}
	
	public boolean extra2Hit(Particle p, Target t) { //if hit a red ball, all other blocks hit in this round disappears
		if (t.getBounds().intersects(new Rectangle((int)(p.getLoc().x + p.getVel().x), (int)(p.getLoc().y + p.getVel().y), PARTICLE_SIZE, PARTICLE_SIZE))) {
			if (t instanceof Extra2) {
				extra2Mode = true;
				return true;
			}
		}
		return false;
	}
	
	public boolean extra3Hit(Particle p, Target t) { //if hit a green ball, number of balls + 10
		if (t.getBounds().intersects(new Rectangle((int)(p.getLoc().x + p.getVel().x), (int)(p.getLoc().y + p.getVel().y), PARTICLE_SIZE, PARTICLE_SIZE))) {
			if (t instanceof Extra3) {
				addParticles += 10;
				return true;
			}
		}
		return false;
	}
	
	public void updateTargets() { //update targets
		Point2D.Double trash = new Point2D.Double(1000, 1000);
		for (int i = 0; i < targets.size(); i++) {
			if (targets.get(i).getLoc().equals(trash)) {
				targets.remove(i);
			}
		}
	}
	
	public int updateParticles() {
		activeParticles = 0;
		for (Particle p : particles) {
			if (p.isActive()) {
				activeParticles++;
				for (int i = 0; i < targets.size(); i++) {
					if (targets.get(i) instanceof Extra || targets.get(i) instanceof Extra2 || targets.get(i) instanceof Extra3) { //if hit the extras, execute special function
						if (extraHit(p, targets.get(i))) {
							targets.remove(i);
						} else if (extra2Hit(p, targets.get(i))) {
							targets.remove(i);
						} else if (extra3Hit(p, targets.get(i))) {
							targets.remove(i);
						}
					} else { //if hit a block, number on the block - 1
						if (blockHit(p, targets.get(i))) {
							targets.remove(i);
						}
					}
				}
				if ((p.getLoc().x + PARTICLE_SIZE + p.getVel().x) > getWidth() || (p.getLoc().x + p.getVel().x < 0)) {
					p.setAngle(Math.PI - p.getAngle());
//					p.bounce((int)(Math.random() + ANGLE_NOISE_CHANCE) * ANGLE_NOISE);
				}
				if ((p.getLoc().y + p.getVel().y < 0)) {
					p.setAngle(-p.getAngle());
//					p.bounce((int)(Math.random() + ANGLE_NOISE_CHANCE) * ANGLE_NOISE);
				}
				if (p.getLoc().y + p.getVel().y > bottom) {
					if (firstBack) {
						origin = p.getLoc().x + p.getVel().x * (p.getLoc().y - bottom) / p.getVel().y;
						firstBack = false;
					}
					p.setLoc(origin, bottom);
					p.deactivate();
				} else {
					p.update();
				}
			}
		}
		return activeParticles;
	}

	public void addNewTarget(int index, int level, int getExtra, double extra2Poss, double extra3Poss) {
		if (index == getExtra) { //clean : messy
			targets.add(new Extra(BLOCK_PADDING - 1 + index*blockSize * (isClean ? 1 :(Math.random() * 0.8 + 0.6)), top, blockSize)); //add white ball
		} else if (Math.random() < extra2Poss && !extra2Added) {
			targets.add(new Extra2(BLOCK_PADDING - 1 + index*blockSize * (isClean ? 1 :(Math.random() * 0.8 + 0.6)), top, blockSize)); //add red ball
			extra2Added = true;
		} else if (Math.random() < extra3Poss && !extra3Added) {
			targets.add(new Extra3(BLOCK_PADDING - 1 + index*blockSize * (isClean ? 1 :(Math.random() * 0.8 + 0.6)), top, blockSize)); //add green ball
			extra3Added = true;
		} else {
			if (Math.random() < BLOCK_CHANCE) { //add blocks
				targets.add(new Block(BLOCK_PADDING - 1 + index*blockSize * (isClean ? 1 :(Math.random() * 0.8 + 0.6)), top, blockSize, level + level*(int)(Math.random() + BLOCK_DOUBLE_CHANCE), 2*level));
			}
		}
	}
	
	public void prepareNextRound() { //prepare next round
		for (Target t : targets) {
			t.setLoc(t.getLoc().x, t.getLoc().y + blockSize);
		}
		for (int i = 0; i < targets.size(); i++) {
			if (targets.get(i).getLoc().y > bottom) {
				System.out.println("You lose");
				System.exit(0);
				targets.remove(i);
			}
		}
		blockLevel++;
		newExtra = (int)(Math.random() * getWidth() / blockSize);
		
		extra2Mode = false;
		extra2Added = false;
		extra3Added = false;
		
		
		
		for (int i = 0; i < blocksPerRow /*getWidth() / blockSize*/; i++) {
			addNewTarget(i, blockLevel, newExtra, newExtra2Poss, newExtra3Poss);
		}
		particleIndex = 0;
		particleTick = 0;
		for (int i = 0; i < addParticles; i++) {
			particleCount++;
			particles.add(new Particle(origin, bottom, PARTICLE_SPEED, 0, PARTICLE_SIZE));
		}
		addParticles = 0;
	}

	public void resetGame() { //reset the game
		extra2Mode = false;
		extra2Added = false;
		extra3Added = false;

		getInput = true;
		canShoot = true;
		firstBack = true;
		
		mouseCoords = new Point();

		particles = new ArrayList<Particle>();

		particleIndex = 0;
		particleTick = 0;
		particleCount = 1;
		addParticles = 0;
		
		blockLevel = 1;
		for (int i = 0; i < particleCount; i++) {
			particles.add(new Particle(origin, bottom, PARTICLE_SPEED, 0, PARTICLE_SIZE));
		}

		targets = new ArrayList<Target>();
		newExtra = (int)(Math.random()*blocksPerRow);
		
		for (int i = 0; i < blocksPerRow; i++) {
			addNewTarget(i, blockLevel, newExtra, newExtra2Poss, newExtra3Poss);
		}
	}
	
	public void reloadGame(int hardness, int cleanness, int particleNum, int blockLvl, double particleLocX, ArrayList<Target> part) { //reload the game
		modeIndex = hardness;
		switch (hardness) {
		case 0: {
			modeBtn.setText("Easy");
			newExtra2Poss = 0.02;
			newExtra3Poss = 0.02;
			break;
		}
		case 1: {
			modeBtn.setText("Medium");
			newExtra2Poss = 0;
			newExtra3Poss = 0.02;
			break;
		}
		case 2: {
			modeBtn.setText("Hard");
			newExtra2Poss = 0;
			newExtra3Poss = 0;
			break;
		}
		}
		
		cleanIndex = cleanness;
		switch (cleanness) {
		case 0: {
			cleanBtn.setText("Clean");
			isClean = true;
			break;
		}
		case 1: {
			cleanBtn.setText("Messy");
			isClean = false;
			break;
		}
		}
		
		extra2Mode = false;
		extra2Added = false;
		extra3Added = false;

		getInput = true;
		canShoot = true;
		firstBack = true;
		
		mouseCoords = new Point();

		particles = new ArrayList<Particle>();

		particleIndex = 0;
		particleTick = 0;
		particleCount = particleNum;
		addParticles = 0;
		
		blockLevel = blockLvl;
		
		origin = particleLocX;
		for (int i = 0; i < particleCount; i++) {
			particles.add(new Particle(origin, bottom, PARTICLE_SPEED, 0, PARTICLE_SIZE));
		}

		targets = part;
	}
	
	public void initBtns() { //initiate the buttons
		resetBtn = new JButton("Reset");
		resetBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetGame();
			}
		});
		
		modeBtn = new JButton("Easy");
		modeIndex = 0;
		modeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modeIndex = (modeIndex + 1) % 3;
				switch (modeIndex) {
				case 0: {
					modeBtn.setText("Easy"); //easy: red and green balls
					newExtra2Poss = 0.02;
					newExtra3Poss = 0.02;
					resetGame();
					break;
				}
				case 1: {
					modeBtn.setText("Medium"); //medium: green balls
					newExtra2Poss = 0;
					newExtra3Poss = 0.02;
					resetGame();
					break;
				}
				case 2: {
					modeBtn.setText("Hard"); //hard: only white balls
					newExtra2Poss = 0;
					newExtra3Poss = 0;
					resetGame();
					break;
				}
				}
			}
		});
		
		cleanBtn = new JButton("Clean"); //shift between clean and messy
		cleanIndex = 0;
		cleanBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cleanIndex = (cleanIndex + 1) % 2;
				switch (cleanIndex) {
				case 0: {
					cleanBtn.setText("Clean");
					isClean = true;
					resetGame();
					break;
				}
				case 1: {
					cleanBtn.setText("Messy");
					isClean = false;
					resetGame();
					break;
				}
				}
			}
		});
		
		saveBtn = new JButton("Save Progress"); //save game data in a txt file
		saveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (canSave) {
					try {
						PrintWriter outputStream = new PrintWriter("gameData--balls.txt");
						outputStream.println(modeIndex);
						outputStream.println(cleanIndex);
						outputStream.println(particleCount);
						outputStream.println(blockLevel);
						outputStream.println(particles.get(0).getLoc().getX());
						
						for (Target tar : targets) {
							if (tar instanceof Block) {
								outputStream.print("B");
								outputStream.print("!" + tar.getLoc().getX() + "@" + tar.getLoc().getY() + "#" + tar.getSize());
								outputStream.println("$" + ((Block) tar).getNumber() + "%" + ((Block) tar).getMaximum() + "^" + ((Block) tar).getColor().getRed() + "&" + ((Block) tar).getColor().getGreen() + "*" + ((Block) tar).getColor().getBlue());
								
							} else {
								if (tar instanceof Extra) {
									outputStream.print("E");
								} else if (tar instanceof Extra2) {
									outputStream.print("2");
								} else if (tar instanceof Extra3) {
									outputStream.print("3");
								}
								outputStream.println("!" + tar.getLoc().getX() + "@" + tar.getLoc().getY() + "#" + tar.getSize());
							}
						}
						outputStream.close();
					} catch (FileNotFoundException e1) {
					}
				}
			}
		});
		
		loadBtn = new JButton("Load Game"); //load the game data from the txt file
		loadBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> tempReader = new ArrayList<String>();
				try {
					File gameData = new File("gameData--balls.txt");
					BufferedReader buf = new BufferedReader(new FileReader(gameData));
					String readLine = "";
					while ((readLine = buf.readLine()) != null) {
						tempReader.add(readLine);
					}
					buf.close();
				} catch (FileNotFoundException e1) {
				} catch (IOException e1) {
				}
				
				ArrayList<Target> pa = new ArrayList<Target>();
				
				int pos1 = 0;
				int pos2 = 0;
				int pos3 = 0;
				int pos4 = 0;
				int pos5 = 0;
				int pos6 = 0;
				int pos7 = 0;
				int pos8 = 0;
				
				for (int i = 5; i < tempReader.size(); i++) {
					String line = tempReader.get(i);
					if (line.substring(0, 1).equals("B")) {
						for (int j = 1; j < line.length(); j++) {
							if (line.substring(j, j + 1).equals("!")) {
								pos1 = j;
							}
							if (line.substring(j, j + 1).equals("@")) {
								pos2 = j;
							}
							if (line.substring(j, j + 1).equals("#")) {
								pos3 = j;
							}
							if (line.substring(j, j + 1).equals("$")) {
								pos4 = j;
							}
							if (line.substring(j, j + 1).equals("%")) {
								pos5 = j;
							}
							if (line.substring(j, j + 1).equals("^")) {
								pos6 = j;
							}
							if (line.substring(j, j + 1).equals("&")) {
								pos7 = j;
							}
							if (line.substring(j, j + 1).equals("*")) {
								pos8 = j;
							}
						}
						pa.add(new Block(Double.parseDouble(line.substring(pos1 + 1, pos2)), (int) Double.parseDouble(line.substring(pos2 + 1, pos3)), Integer.parseInt(line.substring(pos3 + 1, pos4)), Integer.parseInt(line.substring(pos4 + 1, pos5)), Integer.parseInt(line.substring(pos5 + 1, pos6))));
						((Block) pa.get(pa.size() - 1)).setColor(new Color(Integer.parseInt(line.substring(pos6 + 1, pos7)), Integer.parseInt(line.substring(pos7 + 1, pos8)), Integer.parseInt(line.substring(pos8 + 1))));
					} else {
						for (int j = 1; j < line.length(); j++) {
							if (line.substring(j, j + 1).equals("!")) {
								pos1 = j;
							}
							if (line.substring(j, j + 1).equals("@")) {
								pos2 = j;
							}
							if (line.substring(j, j + 1).equals("#")) {
								pos3 = j;
							}
						}
						if (line.substring(0, 1).equals("E")) {
							pa.add(new Extra(Double.parseDouble(line.substring(pos1 + 1, pos2)), (int) Double.parseDouble(line.substring(pos2 + 1, pos3)), Integer.parseInt(line.substring(pos3 + 1))));
						} else if (line.substring(0, 1).equals("2")) {
							pa.add(new Extra2(Double.parseDouble(line.substring(pos1 + 1, pos2)), (int) Double.parseDouble(line.substring(pos2 + 1, pos3)), Integer.parseInt(line.substring(pos3 + 1))));
						} else if (line.substring(0, 1).equals("3")) {
							pa.add(new Extra3(Double.parseDouble(line.substring(pos1 + 1, pos2)), (int) Double.parseDouble(line.substring(pos2 + 1, pos3)), Integer.parseInt(line.substring(pos3 + 1))));
						}
					}
				}
				reloadGame(Integer.parseInt(tempReader.get(0)), Integer.parseInt(tempReader.get(1)), Integer.parseInt(tempReader.get(2)), Integer.parseInt(tempReader.get(3)), Double.parseDouble(tempReader.get(4)), pa);
			}
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (!getInput) {
			canSave = false;
			if (particleIndex < particles.size()) {
				if (particleTick == 0) {
					particles.get(particleIndex).activate();
					particleIndex++;
				}
			}
			particleTick = (particleTick + 1) % PARTICLE_DELAY;
			getInput = (0 == updateParticles());
			updateTargets();
			if (getInput) prepareNextRound();
		} else {
			canSave = true;
		}
		repaint();
	}

	public boolean calcVel(double start, Point end) { //determine if the angle of the line is too steep
		double angle = Math.atan2(end.y - bottom, end.x - origin);
		if ((angle > ANGLE_TOL - Math.PI) && (angle < -ANGLE_TOL)) {
			startingAngle = angle;
			canShoot = true;
			return true;
		}
		canShoot = false;
		return false;
	}

	private class MAdapter extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			mouseCoords.x = e.getX();
			mouseCoords.y = e.getY();
			if (getInput) {
				if (calcVel(origin, mouseCoords)) {
					getInput = false;
					firstBack = true;
					for (Particle p : particles) {
						p.setAngle(startingAngle);
					}
				}
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			mouseCoords.x = e.getX();
			mouseCoords.y = e.getY();
			calcVel(origin, mouseCoords);
		}
	}
}
