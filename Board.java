import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import sun.applet.Main;

public class Board extends JPanel implements ActionListener {

	private Dimension d;
	private final Font smallFont = new Font("Helvetica", Font.BOLD, 14);

	private Image ii;
	private final Color dotColor = new Color(192, 192, 0);
	private Color mazeColor;

	private boolean inGame = false;
	private boolean dying = false;

	private final int BLOCK_SIZE = 24;
	private final int N_BLOCKS = 15;
	private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
	private final int PAC_ANIM_DELAY = 4;
	private final int PACMAN_ANIM_COUNT = 4;
	private final int MAX_PACS = 12;
	private final int GHOST_SPEED = 4;
	private final String WINNING_MESSAGE = "You WON! Press s to PARTY again.";
	private final String BEGINNING_MESSAGE = "Press s to PARTY!";

	private int pacAnimCount = PAC_ANIM_DELAY;
	private int pacAnimDir = 1;
	private int pacmanAnimPos = 0;
	private int N_PACS;
	private int ghostsLeft, score;
	private ArrayList<Pacman> pacmans;
	private Ghost ghost;

	private Image ghostLeft, ghostRight, ghostUp, ghostDown;
	private Image pacman1, pacman2up, pacman2left, pacman2right, pacman2down;
	private Image pacman3up, pacman3down, pacman3left, pacman3right;
	private Image pacman4up, pacman4down, pacman4left, pacman4right;

	private int req_dx, req_dy;

	private final short[][] q1options = {
			{ 19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22, 21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16,
				16, 16, 20, 21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 21, 0, 0, 0, 17, 16, 16, 24,
				16, 16, 16, 16, 16, 16, 20, 17, 18, 18, 18, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 20 } };
	private final short[][] q2options = { { 17, 16, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 16, 24, 20, 25, 16, 16, 16,
		24, 24, 28, 0, 25, 24, 24, 16, 20, 0, 21, 1, 17, 16, 20, 0, 0, 0, 0, 0, 0, 0, 17, 20, 0, 21, 1, 17, 16, 16,
		18, 18, 22, 0, 19, 18, 18, 16, 20, 0, 21, 1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21 } };

	private final short[][] q3options = { { 1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21, 1, 17, 16, 16, 16,
		16, 16, 18, 16, 16, 16, 16, 20, 0, 21, 1, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 21, 1, 25, 24,
		24, 24, 24, 24, 24, 24, 24, 16, 16, 16, 18, 20, 9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 25, 24, 24, 24, 28 } };

	private final int validSpeeds[] = { 1, 2, 3, 4, 6, 8 };
	private final int maxSpeed = 8;

	private int currentSpeed = 3;
	protected short[] screenData;
	private Timer timer;

	protected Graphics2D g2d;

	public Board() {

		initVariables();
		loadImages();
		initBoard();

	}

	public int getBlockSize() {
		return this.BLOCK_SIZE;
	}

	public int getNumBlocks() {
		return this.N_BLOCKS;

	}

	public void decrementScore() {
		score = score - 1;
		if (score == 0) {
			dying = true;
		}
	}

	private void initBoard() {

		addKeyListener(new TAdapter()); // Keep this

		setFocusable(true);

		setBackground(Color.black);
		setDoubleBuffered(true);
	}

	private void initVariables() {

		screenData = new short[N_BLOCKS * N_BLOCKS];
		mazeColor = new Color(5, 100, 5);
		d = new Dimension(400, 400);
		timer = new Timer(40, this);
		timer.start();
	}

	@Override
	public void addNotify() {
		super.addNotify();

		initGame();
	}

	private void doAnim() {

		pacAnimCount--;

		if (pacAnimCount <= 0) {
			pacAnimCount = PAC_ANIM_DELAY;
			pacmanAnimPos = pacmanAnimPos + pacAnimDir;

			if (pacmanAnimPos == (PACMAN_ANIM_COUNT - 1) || pacmanAnimPos == 0) {
				pacAnimDir = -pacAnimDir;
			}
		}
	}

	private void playGame(Graphics2D g2d) {

		if (dying) {
			death();
		} else {
			for (int i = 0; i < pacmans.size(); i++) {
				pacmans.get(i).move(this);
				drawPacman(g2d, pacmans.get(i));
			}

			ghost.move(req_dx, req_dy);
			drawGhost(g2d, ghost);
			checkCollision();
			checkMaze();
		}
	}

	/**
	 * 
	 */
	private void checkCollision() {
		Pacman removePac = null;
		for (Pacman pacman : pacmans) {
			if (pacman.getX() > (ghost.getX() - 12) && pacman.getX() < (ghost.getX() + 12)
					&& pacman.getY() > (ghost.getY() - 12) && pacman.getY() < (ghost.getY() + 12) && inGame) {
				System.out.println("COLLISION");
				// dying = true;
				removePac = pacman;
			}
		}
		if (removePac != null)
			pacmans.remove(removePac);
	}

	private void showPopupScreen(Graphics2D g2d, String message) {

		g2d.setColor(new Color(0, 32, 48));
		g2d.fillRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);
		g2d.setColor(Color.white);
		g2d.drawRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);

		String s = message;
		Font small = new Font("Comic Sans", Font.BOLD, 16);
		FontMetrics metr = this.getFontMetrics(small);

		g2d.setColor(Color.white);
		g2d.setFont(small);
		g2d.drawString(s, (SCREEN_SIZE - metr.stringWidth(s)) / 2, SCREEN_SIZE / 2);
	}

	private void drawScore(Graphics2D g) {

		int i;
		String s;

		g.setFont(smallFont);
		g.setColor(new Color(96, 128, 255));
		s = "Score: " + score;
		g.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);

		for (i = 0; i < ghostsLeft; i++) {
			g.drawImage(ghostLeft, i * 28 + 8, SCREEN_SIZE + 1, this);
		}
	}

	private void checkMaze() {

		if (pacmans.isEmpty()) {

			if (N_PACS < MAX_PACS) {
				N_PACS++;
			}
			if (N_PACS == MAX_PACS) {
				inGame = false;

			}

			if (currentSpeed < maxSpeed) {
				currentSpeed++;
			}

			initLevel();
		}
	}

	private void death() {

		ghostsLeft--;

		if (ghostsLeft == 0) {
			inGame = false;
		}

		initLevel();
		continueLevel();
	}

	private void loadImages() {

		ghostLeft = new ImageIcon(Main.class.getResource("/images/ghostLeft.png")).getImage();
		ghostRight = new ImageIcon(Main.class.getResource("/images/ghostRight.png")).getImage();
		ghostUp = new ImageIcon(Main.class.getResource("/images/ghostUp.png")).getImage();
		ghostDown = new ImageIcon(Main.class.getResource("/images/ghostDown.png")).getImage();
		pacman1 = new ImageIcon(Main.class.getResource("/images/pacman.png")).getImage();
		pacman2up = new ImageIcon(Main.class.getResource("/images/up1.png")).getImage();
		pacman3up = new ImageIcon(Main.class.getResource("/images/up2.png")).getImage();
		pacman4up = new ImageIcon(Main.class.getResource("/images/up3.png")).getImage();
		pacman2down = new ImageIcon(Main.class.getResource("/images/down1.png")).getImage();
		pacman3down = new ImageIcon(Main.class.getResource("/images/down2.png")).getImage();
		pacman4down = new ImageIcon(Main.class.getResource("/images/down3.png")).getImage();
		pacman2left = new ImageIcon(Main.class.getResource("/images/left1.png")).getImage();
		pacman3left = new ImageIcon(Main.class.getResource("/images/left2.png")).getImage();
		pacman4left = new ImageIcon(Main.class.getResource("/images/left3.png")).getImage();
		pacman2right = new ImageIcon(Main.class.getResource("/images/right1.png")).getImage();
		pacman3right = new ImageIcon(Main.class.getResource("/images/right2.png")).getImage();
		pacman4right = new ImageIcon(Main.class.getResource("/images/right3.png")).getImage();

	}

	private void drawGhost(Graphics2D g2d, Ghost g) {
		if (g.getViewDX() == -1) {
			g2d.drawImage(ghostLeft, g.getX() + 1, g.getY() + 1, this);
		} else if (g.getViewDX() == 1) {
			g2d.drawImage(ghostRight, g.getX() + 1, g.getY() + 1, this);
		} else if (g.getViewDY() == -1) {
			g2d.drawImage(ghostUp, g.getX() + 1, g.getY() + 1, this);
		} else {
			g2d.drawImage(ghostDown, g.getX() + 1, g.getY() + 1, this);
		}
	}

	private void drawPacman(Graphics2D g2d, Pacman p) {

		if (p.getViewDX() == -1) {
			switch (pacmanAnimPos) {
			case 1:
				g2d.drawImage(pacman2left, p.getX() + 1, p.getY() + 1, this);
				break;
			case 2:
				g2d.drawImage(pacman3left, p.getX() + 1, p.getY() + 1, this);
				break;
			case 3:
				g2d.drawImage(pacman4left, p.getX() + 1, p.getY() + 1, this);
				break;
			default:
				g2d.drawImage(pacman1, p.getX() + 1, p.getY() + 1, this);
				break;
			}
		} else if (p.getViewDX() == 1) {
			switch (pacmanAnimPos) {
			case 1:
				g2d.drawImage(pacman2right, p.getX() + 1, p.getY() + 1, this);
				break;
			case 2:
				g2d.drawImage(pacman3right, p.getX() + 1, p.getY() + 1, this);
				break;
			case 3:
				g2d.drawImage(pacman4right, p.getX() + 1, p.getY() + 1, this);
				break;
			default:
				g2d.drawImage(pacman1, p.getX() + 1, p.getY() + 1, this);
				break;
			}
		} else if (p.getViewDY() == -1) {
			switch (pacmanAnimPos) {
			case 1:
				g2d.drawImage(pacman2up, p.getX() + 1, p.getY() + 1, this);
				break;
			case 2:
				g2d.drawImage(pacman3up, p.getX() + 1, p.getY() + 1, this);
				break;
			case 3:
				g2d.drawImage(pacman4up, p.getX() + 1, p.getY() + 1, this);
				break;
			default:
				g2d.drawImage(pacman1, p.getX() + 1, p.getY() + 1, this);
				break;
			}
		} else {
			switch (pacmanAnimPos) {
			case 1:
				g2d.drawImage(pacman2down, p.getX() + 1, p.getY() + 1, this);
				break;
			case 2:
				g2d.drawImage(pacman3down, p.getX() + 1, p.getY() + 1, this);
				break;
			case 3:
				g2d.drawImage(pacman4down, p.getX() + 1, p.getY() + 1, this);
				break;
			default:
				g2d.drawImage(pacman1, p.getX() + 1, p.getY() + 1, this);
				break;
			}
		}
	}

	private void drawMaze(Graphics2D g2d) {

		short i = 0;
		int x, y;

		for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
			for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

				g2d.setColor(mazeColor);
				g2d.setStroke(new BasicStroke(2));

				if ((screenData[i] & 1) != 0) {
					g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
				}

				if ((screenData[i] & 2) != 0) {
					g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
				}

				if ((screenData[i] & 4) != 0) {
					g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1, y + BLOCK_SIZE - 1);
				}

				if ((screenData[i] & 8) != 0) {
					g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1, y + BLOCK_SIZE - 1);
				}

				if ((screenData[i] & 16) != 0) {
					g2d.setColor(dotColor);
					g2d.fillRect(x + 11, y + 11, 2, 2);
				}

				i++;
			}
		}
	}

	private void initGame() {

		ghostsLeft = 3;
		score = 179;
		initLevel();
		N_PACS = 1;
		currentSpeed = 3;

	}

	private void initLevel() {

		int i;
		short[] levelData = generateLevel();
		for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
			screenData[i] = levelData[i];
		}

		continueLevel();
	}

	private short[] generateLevel() {

		short[] levelData = new short[N_BLOCKS * N_BLOCKS];

		for (int i = 0; i < 4; i++) {
			// Pick a random box
			int option = (int) Math.floor(Math.random() * 1);
			short[] quad;
			int lvlidx;
			switch (i) {
			// Upper left quadrant
			case 0:
				lvlidx = 0;
				quad = q1options[option];
				break;
				// Upper right quadrant
			case 1:
				lvlidx = (N_BLOCKS / 3) * N_BLOCKS;
				quad = q2options[option];
				break;
				// Lower right quadrant
			default:
				lvlidx = 2 * (N_BLOCKS / 3) * N_BLOCKS;
				quad = q3options[option];
				break;
			}

			for (int quadidx = 0; quadidx < quad.length; quadidx++) {
				levelData[lvlidx] = quad[quadidx];
				lvlidx++;

			}

		}

		return levelData;
	}

	private void continueLevel() {

		short i;
		int dx = 1;
		int random;
		score = 179;

		pacmans = new ArrayList<Pacman>();

		for (i = 0; i < N_PACS; i++) {

			random = (int) (Math.random() * (currentSpeed + 1));
			if (random < 5) {
				if (random > currentSpeed) {
					random = currentSpeed;
				}
			} else
				random = 3;

			int speed = validSpeeds[random];

			pacmans.add(new Pacman(this, 4 * BLOCK_SIZE, 4 * BLOCK_SIZE, dx, 0, speed, -1, 0));

			dx = -dx;
		}

		ghost = new Ghost(this, 7 * BLOCK_SIZE, 11 * BLOCK_SIZE, -1, 0, GHOST_SPEED, -1, 0);

		req_dx = 0;
		req_dy = 0;
		dying = false;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		doDrawing(g);
	}

	private void doDrawing(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, d.width, d.height);

		drawMaze(g2d);
		drawScore(g2d);
		doAnim();

		if (inGame) {
			playGame(g2d);
		} else if (N_PACS < MAX_PACS) {
			showPopupScreen(g2d, BEGINNING_MESSAGE);
		} else {
			showPopupScreen(g2d, WINNING_MESSAGE);
		}

		g2d.drawImage(ii, 5, 5, this);
		Toolkit.getDefaultToolkit().sync();
		g2d.dispose();
	}

	class TAdapter extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {

			int key = e.getKeyCode();

			if (inGame) {
				if (key == KeyEvent.VK_LEFT) {
					req_dx = -1;
					req_dy = 0;
				} else if (key == KeyEvent.VK_RIGHT) {
					req_dx = 1;
					req_dy = 0;
				} else if (key == KeyEvent.VK_UP) {
					req_dx = 0;
					req_dy = -1;
				} else if (key == KeyEvent.VK_DOWN) {
					req_dx = 0;
					req_dy = 1;
				} else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
					inGame = false;
				} else if (key == KeyEvent.VK_PAUSE) {
					if (timer.isRunning()) {
						timer.stop();
					} else {
						timer.start();
					}
				}
			} else {
				if (key == 's' || key == 'S') {
					inGame = true;
					initGame();
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {

			int key = e.getKeyCode();

			if (key == Event.LEFT || key == Event.RIGHT || key == Event.UP || key == Event.DOWN) {
				req_dx = 0;
				req_dy = 0;
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		repaint();
	}

	public Graphics2D getG2D() {
		return g2d;
	}

}