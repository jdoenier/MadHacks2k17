
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Pacman extends JFrame {
	private int dx;
	private int dy;
	private int x;
	private int y;
	int speed;
	private int[] _dx;
	private int[] _dy;
	private Image image;
	private int viewDX;
	private int viewDY;

	public Pacman(Board board, int x, int y, int dx, int dy, int speed, int viewDX, int viewDY) {
		image = new ImageIcon("Images/pacman.png").getImage();
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.speed =speed;
		this.viewDX = viewDX;
		this.viewDY = viewDY;
		_dx = new int[4];
		_dy = new int[4];
	}

	public void move(Board board) {
		int blockSize = board.getBlockSize();
		int numBlocks = board.getNumBlocks();

		int pos;
		int count;
		short ch;
		
		

		if (x % blockSize == 0 && y % blockSize == 0) {
			pos = x / blockSize + numBlocks * (int) (y / blockSize);
			ch = board.screenData[pos];
			
			if ((ch & 16) != 0) {
			board.screenData[pos] = (short) (ch & 15);
			board.decrementScore();
			}
			count = 0;

			if ((board.screenData[pos] & 1) == 0 && dx != 1) {
				_dx[count] = -1;
				_dy[count] = 0;
				count++;
			}

			if ((board.screenData[pos] & 2) == 0 && dy != 1) {
				_dx[count] = 0;
				_dy[count] = -1;
				count++;
			}

			if ((board.screenData[pos] & 4) == 0 && dx != -1) {
				_dx[count] = 1;
				_dy[count] = 0;
				count++;
			}

			if ((board.screenData[pos] & 8) == 0 && dy != -1) {
				_dx[count] = 0;
				_dy[count] = 1;
				count++;
			}

			if (count == 0) {

				if ((board.screenData[pos] & 15) == 15) {
					dx = 0;
					dy = 0;
				} else {
					this.dx = -dx;
					dy = -dy;
				}

			} else {

				boolean noTurn = false;
				for(int i = 0; i < count; i ++){
					if (dx == _dx[i] && dy == _dy[i]){
						noTurn = true;
					}
				}
				if(Math.random() > 0.4){
					noTurn = false;
				}
				if(!noTurn){
					count = (int) (Math.random() * count);

					if (count > 3) {
						count = 3;
					}

					dx = _dx[count];
					dy = _dy[count];
					viewDX = dx;
					viewDY = dy;
				}
			}
		}
		x = x + (dx * speed);
		y = y + (dy * speed);


	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Image getImage() {
		return image;
	}
	
	public int getViewDX(){
		return viewDX;
	}
	
	public int getViewDY(){
		return viewDY;
	}

	// public void drawPacman() {
	// //fill this in
	// }

}
