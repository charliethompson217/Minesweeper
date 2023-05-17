import java.util.Random;
import processing.core.PApplet;
import processing.core.PImage;

public class Minsweeper extends PApplet {

	// user pref
	int height = 28;
	int width = 28;
	int cellSize = 28;
	double dificulty = 18;
	int topMargin = cellSize;
	int leftMargin = 0;

	// globals
	ScoreBoard scoreBoard;
	Cell cells[][];
	int mines;
	int openCells;
	boolean lost;
	boolean won;
	PImage mine;
	PImage flag;
	PImage smiley;
	long seed;
	int flags;

	public static void main(String[] args) {
		PApplet.main("Minsweeper");
	}

	public void settings() {
		this.size(cellSize * width + leftMargin, cellSize * height + topMargin);
	}

	public void setup() {
		mine = loadImage("mine.png");
		flag = loadImage("flag.png");
		smiley = loadImage("smiley.png");
		seed = System.currentTimeMillis();
		startGame(seed);
	}

	public void draw() {
		background(255);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				cells[i][j].update(leftMargin, topMargin);
			}
		}
		scoreBoard.update();
		if (openCells + mines == height * width)
			playerWon();
		if (lost) {
			textSize(cellSize);
			fill(250, 0, 0);
			text("Game Over", (float) (((width * cellSize) / 2) - cellSize * 2.5) + leftMargin,
					(height * cellSize) / 2 + topMargin);
		}
		if (won) {
			textSize(cellSize);
			fill(250, 0, 0);
			text("Player Won!", (float) (((width * cellSize) / 2) - cellSize * 2.5) + leftMargin,
					(height * cellSize) / 2 + topMargin);
		}
	}

	public void mouseClicked() {
		if (mouseY < topMargin) {
			if (mouseX > (width * cellSize) / 2 - 3 * topMargin / 2
					&& mouseX < (width * cellSize) / 2 - topMargin / 2) {
				seed = System.currentTimeMillis();
				startGame(seed);
			}
			if (mouseX > (width * cellSize) / 2 - topMargin / 2 && mouseX < (width * cellSize) / 2 + topMargin / 2) {
				if (mouseButton == LEFT)
					startGame(seed);
			}
			if (lost)
				return;
			if (mouseX > (width * cellSize) / 2 + topMargin / 2
					&& mouseX < (width * cellSize) / 2 + 3 * topMargin / 2) {
				hint();
			}

			return;
		}
		if (lost)
			return;
		int X = mouseX - leftMargin;
		int Y = mouseY - topMargin;
		Cell cell = cells[X / cellSize][Y / cellSize];
		if (cell.isOpen)
			return;

		if (mouseButton == RIGHT)
			cell.flag();
		else
			cell.open();
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
		boolean hinted = false;

		Cell(int x, int y) {
			this.x = x;
			this.y = y;
		}

		void update(int offX, int offY) {
			fill(r, g, b);
			stroke(0, 0, 0);
			strokeWeight(0);
			square(x * cellSize + offX, y * cellSize + offY, cellSize);
			fill(0, 0, 0);
			if (isOpen && count > 0) {
				textSize((float) (cellSize));
				switch (count) {
				case (1):
					fill(3, 65, 252);
					break;
				case (2):
					fill(78, 201, 20);
					break;
				case (3):
					fill(250, 0, 0);
					break;
				case (4):
					fill(13, 28, 122);
					break;
				case (5):
					fill(122, 13, 13);
					break;
				case (6):
					fill(11, 141, 153);
					break;
				case (7):
					fill(0, 0, 0);
					break;
				case (8):
					fill(70, 70, 70);
					break;

				}
				text(Integer.toString(count), x * cellSize + (3 * cellSize / 12) + offX,
						y * cellSize + (4 * cellSize / 5) + offY);
				if (isBomb) {
					image(mine, x * cellSize + offX, y * cellSize + offY, cellSize, cellSize);
				}
			}
			if (isFlaged) {
				image(flag, x * cellSize + 2 + offX, y * cellSize + 2 + offY, cellSize - 4, cellSize - 4);
			}
			if (lost) {
				if (isBomb && !isFlaged) {
					image(mine, x * cellSize + offX, y * cellSize + offY, cellSize, cellSize);
				}
				if (isBomb && isOpen) {
					stroke(250, 0, 0);
					strokeWeight(2);
					line(x * cellSize + offX, y * cellSize + offY, x * cellSize + cellSize + offX,
							y * cellSize + cellSize + offY);
					line(x * cellSize + cellSize + offX, y * cellSize + offY, x * cellSize + offX,
							y * cellSize + cellSize + offY);
				}
				if (!isBomb && isFlaged) {
					stroke(0, 0, 0);
					strokeWeight(2);
					line(x * cellSize + offX, y * cellSize + offY, x * cellSize + cellSize + offX,
							y * cellSize + cellSize + offY);
					line(x * cellSize + cellSize + offX, y * cellSize + offY, x * cellSize + offX,
							y * cellSize + cellSize + offY);
				}

			}

		}

		void open() {
			if (isFlaged) {
				flag();
				return;
			}
			openCells++;
			if (isBomb) {
				playerLost();
			}
			r = 200;
			g = 200;
			b = 200;
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
				flags++;

			} else {
				r = 250;
				g = 0;
				b = 0;
				isFlaged = true;
				flags--;
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

	void playerLost() {
		lost = true;
	}

	void playerWon() {
		won = true;
	}

	class ScoreBoard {
		int size;
		long startTime;
		long time;
		long bucket;

		ScoreBoard(int size, long startTime) {
			this.size = size;
			this.startTime = startTime;
			this.time = System.currentTimeMillis() - startTime;
			this.bucket = time;
		}

		void update() {
			long temp = bucket;
			time = System.currentTimeMillis() - startTime;
			if (!won && !lost)
				bucket = time;
			int HH = (int) (bucket / (60 * 60 * 1000));
			bucket = bucket % (60 * 60 * 1000);
			int MM = (int) (bucket / (60 * 1000));
			bucket = bucket % (60 * 1000);
			int SS = (int) (bucket / 1000);
			bucket = temp;
			String t = "";
			if (HH < 10)
				t += "0";
			t += HH + ":";
			if (MM < 10)
				t += "0";
			t += MM + ":";
			if (SS < 10)
				t += "0";
			t += SS;

			fill(250, 0, 0);
			image(smiley, ((width * cellSize) / 2) - size / 2 + leftMargin - size, 1, size - 2, size - 2);
			image(smiley, ((width * cellSize) / 2) - size / 2 + leftMargin, 1, size - 2, size - 2);
			image(smiley, ((width * cellSize) / 2) - size / 2 + leftMargin + size, 1, size - 2, size - 2);
			textSize((float) (cellSize));
			text(t, (width * cellSize) / 4 - 30, 7 * size / 8);
			text(flags, (3 * width * cellSize) / 4 - 30, 7 * size / 8);
		}
	}

	void startGame(long seed) {
		background(255);
		lost = false;
		won = false;
		mines = (int) ((width * height * dificulty) / 100);
		flags = mines;
		openCells = 0;
		cells = new Cell[width][height];
		scoreBoard = new ScoreBoard(topMargin, seed);
		// initialize cells
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				cells[i][j] = new Cell(i, j);
			}
		}
		// place bombs
		Random random = new Random(seed);
		System.out.println(seed);
		for (int i = 0; i < mines; i++) {
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
		hint();
	}

	void hint() {
		int i = 0;
		while (i < width * height) {
			i++;
			int x = (int) (Math.random() * width);
			int y = (int) (Math.random() * height);
			Cell cell = cells[x][y];
			if (cell.count == 0 && !cell.isOpen && !cell.hinted) {
				cell.g = 250;
				cell.r = 0;
				cell.b = 0;
				cell.hinted = true;
				return;
			}
		}
	}
}
