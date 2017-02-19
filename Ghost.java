
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Ghost {

	private int dx;
	private int dy;
	private int x;
	private int y;
	int speed;
	private Image image;
	private Board board;
	private int viewDX;
	private int viewDY;

	public Ghost(Board board, int x, int y, int dx, int dy, int viewDX, int viewDY) {
		this.board = board;
		image = new ImageIcon("Images/ghost.png").getImage();
		speed = 2;
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.viewDX = viewDX;
		this.viewDY = viewDY;
	}

	public void move(int req_dx, int req_dy) {
		int pos;
		short ch;
		
		int blockSize = board.getBlockSize();
		int numBlocks = board.getNumBlocks();

		if (req_dx == -dx && req_dy == -dy) {
			dx = req_dx;
			dy = req_dy;
			viewDX = dx;
			viewDY = dy;
		}

		if (x % blockSize == 0 && y % blockSize == 0) {
			pos = x / blockSize + numBlocks * (int) (y / blockSize);
			ch = board.screenData[pos];

			if (req_dx != 0 || req_dy != 0) {
				if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0) || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
						|| (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
						|| (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
					dx = req_dx;
					dy = req_dy;
					viewDX = dx;
					viewDY = dy;
				}
			}

			// Check for standstill
			if ((dx == -1 && dy == 0 && (ch & 1) != 0) || (dx == 1 && dy == 0 && (ch & 4) != 0)
					|| (dx == 0 && dy == -1 && (ch & 2) != 0) || (dx == 0 && dy == 1 && (ch & 8) != 0)) {
				dx = 0;
				dy = 0;
			}
		}
		x = x + speed * dx;
		y = y + speed * dy;
	}

	public void loadImages() {
		System.out.println("supposed load images");
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int getViewDX(){
		return viewDX;
	}
	
	public int getViewDY(){
		return viewDY;
	}

	public Image getImage() {
		return image;
	}

	// public void drawGhost(Graphics2D g2d) {
	// g2d.drawImage(image, x, y, board);
	// System.out.println("drawing at " + x + " " + y);
	// }

}
