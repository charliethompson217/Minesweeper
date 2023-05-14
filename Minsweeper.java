import java.util.Random;

import processing.core.PApplet;
import processing.core.PImage;

public class Minsweeper extends PApplet {
	//user pref
	int height = 20;
	int width = 20;
	int cellSize = 25;
	double dificulty = 1.5;
	
	//seed for random number generator
 	long seed = System.currentTimeMillis();
	
	//globals
	boolean gameOver = false;
	Cell cells[][] = new Cell[width][height];
	PImage mine;
	PImage flag;

	public static void main(String[] args) {
		PApplet.main("Minsweeper");

	}

	public void settings() {
		this.size(cellSize * width, cellSize * height);
	}

	public void setup() {
		background(255);
		mine = loadImage("mine.png");
		flag = loadImage("hammer-sickle.png");
		// initialize cells
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				cells[i][j] = new Cell(i, j);
			}
		}
		// place bombs
		Random random = new Random(seed);
		for (int i = 0; i < (width * height * dificulty) / 10; i++) {
			int x = (int) (random.nextDouble() * width);
			int y = (int) (random.nextDouble() * height);
			Cell cell = cells[x][y];
			if (cell.isBomb) {
				i--;
				continue;
			}
			cell.placeBomb();
		}
		// count totals
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				cells[i][j].count();
			}
		}
	}

	public void draw() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				cells[i][j].update();
			}
		}
		if (gameOver) {
			textSize(cellSize);
			fill(250, 0, 0);
			text("Game Over", (float) (((width * cellSize) / 2) - cellSize * 2.5), (height * cellSize) / 2);
		}
	}

	public void mouseClicked() {
		if (gameOver) {
			return;
		}
		int X = mouseX;
		int Y = mouseY;
		if (mouseButton == RIGHT) {
			cells[X / cellSize][Y / cellSize].flag();
		} else
			cells[X / cellSize][Y / cellSize].open();
	}

	class Cell {
		int x;
		int y;
		int r = 250;
		int g = 250;
		int b = 250;
		boolean isBomb = false;
		int count = 0;
		boolean isOpen = false;
		boolean isFlaged = false;

		Cell(int x, int y) {
			this.x = x;
			this.y = y;
		}

		void update() {
			fill(r, g, b);
			stroke(0, 0, 0);
			strokeWeight(0);
			square(x * cellSize, y * cellSize, cellSize);
			if (isOpen && count > 0) {
				textSize((float) (cellSize));
				fill(0, 0, 100);
				text(Integer.toString(count), x * cellSize + (3 * cellSize / 12), y * cellSize + (4 * cellSize / 5));
				if (isBomb) {
					image(mine, x * cellSize, y * cellSize, cellSize, cellSize);
				}
			}
			if (isFlaged) {
				image(flag, x * cellSize + 2, y * cellSize + 2, cellSize - 4, cellSize - 4);
			}
			if (gameOver) {
				if (isBomb && !isFlaged) {
					image(mine, x * cellSize, y * cellSize, cellSize, cellSize);
				}
				if (isBomb && isOpen) {
					stroke(250, 0, 0);
					strokeWeight(2);
					line(x * cellSize, y * cellSize, x * cellSize + cellSize, y * cellSize + cellSize);
					line(x * cellSize + cellSize, y * cellSize, x * cellSize, y * cellSize + cellSize);
				}
				if (!isBomb && isFlaged) {
					stroke(0, 0, 0);
					strokeWeight(2);
					line(x * cellSize, y * cellSize, x * cellSize + cellSize, y * cellSize + cellSize);
					line(x * cellSize + cellSize, y * cellSize, x * cellSize, y * cellSize + cellSize);
				}

			}

		}

		void open() {
			if (isFlaged) {
				flag();
				return;
			}
			if (isBomb) {
				gameOver();
			}
			r = 0;
			g = 0;
			b = 250;
			isOpen = true;
			if (count == 0) {
				// open all adjacent cells
				for (int i = x - 1; i <= x + 1 && i < width; i++) {
					if (i < 0) {
						continue;
					}
					for (int j = y - 1; j <= y + 1 && j < height; j++) {
						if (j < 0) {
							continue;
						}
						if (i == x && j == y)
							continue;
						if (!cells[i][j].isOpen)
							cells[i][j].open();
					}
				}
			}
		}

		void flag() {
			if (isFlaged) {
				r = 250;
				g = 250;
				b = 250;
				isFlaged = false;
			} else {
				r = 250;
				g = 0;
				b = 0;
				isFlaged = true;
			}
		}

		void placeBomb() {
			isBomb = true;
		}

		void count() {
			if (isBomb) {
				count = 9;
				return;
			}
			for (int i = x - 1; i <= x + 1 && i < width; i++) {
				if (i < 0) {
					continue;
				}
				for (int j = y - 1; j <= y + 1 && j < height; j++) {
					if (j < 0) {
						continue;
					}
					if (i == x && j == y)
						continue;
					if (cells[i][j].isBomb)
						count++;
				}
			}
		}
	}

	void gameOver() {
		gameOver = true;
	}
}
