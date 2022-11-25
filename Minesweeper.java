import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class Minesweeper extends JFrame implements MouseListener, ActionListener {
	// ตัวแปร
	int ROW;
	int COL;
	JButton[][] button;
	boolean[][] bombs;
	int flaged;
	int remainFlag;
	int mines;
	int beRemain;
	int remains;
	int score;
	JLabel cBomb = new JLabel();

	// ติดระเบิด
	private void init(int num) {
		Random r = new Random();
		int count = 0;
		while (count < num) {
			int i = r.nextInt(ROW - 1);
			int j = r.nextInt(COL - 1);
			if (!bombs[i][j]) {
				bombs[i][j] = true;
				count++;
			}
		}
	}

	// ตัวเกม เร่มโดยใส่จำนวนระเบิด
	public Minesweeper(int mines, int size) {
		this.mines = mines;
		ROW = size;
		COL = size;
		flaged = mines;
		remainFlag = mines;
		remains = (ROW) * (COL);
		setSize(50 * size, 50 * size);
		JPanel frame = new JPanel();
		cBomb.setText("SCORE : 0");
		cBomb.setPreferredSize(new Dimension(100, 30));
		add(cBomb, BorderLayout.NORTH);
		frame.setLayout(new GridLayout(size, size));
		button = new JButton[ROW][COL];
		bombs = new boolean[ROW][COL];
		for (int i = 0; i < ROW; i++) {
			Arrays.fill(bombs[i], false);
		}
		init(mines);
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				button[i][j] = new JButton();
				if (bombs[i][j]) {
					button[i][j].setText("*");// <------------
				}
				button[i][j].addActionListener(this);
				button[i][j].addMouseListener(this);
				frame.add(button[i][j]);
			}
		}
		setName("Minesweeper");
		add(frame);
	}

	// ใว้เปิดช่อง + เช็คต่างๆ
	public void actionPerformed(ActionEvent e) {
		JButton a = (JButton) e.getSource();
		beRemain = remains;
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				if ((a == button[i][j]) && !(button[i][j].getText() == "O")) {

					winCheck();

					openMap(i, j);
					System.out.println(remains);
					score += (beRemain - remains) * 10;
					cBomb.setText("SCORE : " + score);

					// System.out.println(score);
					winCheck();
					if (clickOnMine(i, j)) {
						reset();
					}
				}
			}
		}
	}

	// เช็กคลิกขวาปักธง
	public void mouseClicked(MouseEvent e) {
		JButton a = (JButton) e.getSource();
		if (e.getButton() == MouseEvent.BUTTON3) {
			for (int i = 0; i < ROW; i++) {
				for (int j = 0; j < COL; j++) {
					if ((a == button[i][j]) && button[i][j].isEnabled() && (button[i][j].getText() != "O")) {
						if (remainFlag > 0) {
							remainFlag--;
							button[i][j].setText("O");
							if (bombs[i][j]) {
								flaged--;
								--remains;
								System.out.println(flaged + " mines");
								score += 50;
								cBomb.setText("SCORE : " + score);
							}
						} else {
							JOptionPane.showMessageDialog(null, "Not enough Flag");
						}
					} else if ((a == button[i][j]) && (button[i][j].getText() == "O")) {
						remainFlag++;
						button[i][j].setText("");
						if (bombs[i][j]) {
							flaged++;
							++remains;
							System.out.println(flaged + " plus");
						}
					}

				}
			}
			winCheck();
		}
	}

	// นับระเบิดช่องรอบๆ
	public int minedCount(int i, int j) {
		int numMined = 0;
		for (int row = i - 1; row <= i + 1; row++) {
			for (int col = j - 1; col <= j + 1; col++) {
				if (row >= 0 && row < ROW && col >= 0 && col < COL) {
					if (bombs[row][col]) {
						numMined++;
					}
				}
			}
		}
		return numMined;
	}

	// เปิด map + ใบ้ระเบิด
	public void openMap(int i, int j) {
		if (!button[i][j].isEnabled() || bombs[i][j]) {
			return;
		}
		--remains;
		button[i][j].setEnabled(false);
		if (minedCount(i, j) != 0)
			button[i][j].setText(minedCount(i, j) + "");
		if (minedCount(i, j) == 0) {
			if (i > 0) {
				openMap(i - 1, j);
			}
			if (j > 0) {
				openMap(i, j - 1);
			}
			if (i < ROW - 1) {
				openMap(i + 1, j);
			}
			if (j < COL - 1) {
				openMap(i, j + 1);
			}
			if ((i < ROW - 1) && (j < COL - 1)) {
				openMap(i + 1, j + 1);
			}
			if ((i > 0) && (j < COL - 1)) {
				openMap(i - 1, j + 1);
			}
			if ((i < ROW - 1) && (j > 0)) {
				openMap(i + 1, j - 1);
			}
			if ((i > 0) && (j > 0)) {
				openMap(i - 1, j - 1);
			}
		}
	}

	// เมื่อกดโดนระเบิด
	public boolean clickOnMine(int i, int j) {
		if (bombs[i][j]) {
			for (int k = 0; k < ROW; k++) {
				for (int l = 0; l < COL; l++) {
					button[k][l].setEnabled(false);
					if (bombs[k][l]) {
						if (flagCheck(k, l)) {
							button[k][l].setBackground(Color.blue);
						} else {
							button[k][l].setText("*");
						}
					} else {
					}
				}
			}
			JOptionPane.showMessageDialog(null, "Game Over");
			return true;
		}
		return false;
	}

	// เช็กธง
	public boolean flagCheck(int i, int j) {
		if (button[i][j].getText() == "O") {
			return true;
		}
		return false;
	}

	// เช็กชนะ
	public void winCheck() {
		if (flaged == 0) {
			for (int k = 0; k < ROW; k++) {
				for (int l = 0; l < COL; l++) {
					button[k][l].setEnabled(false);
					if (bombs[k][l]) {
						if (flagCheck(k, l)) {
							button[k][l].setBackground(Color.blue);
						}
					}
				}
			}
			JOptionPane.showMessageDialog(null, "You won");
			reset();
		} else if (flaged == remains) {
			for (int k = 0; k < ROW; k++) {
				for (int l = 0; l < COL; l++) {
					button[k][l].setEnabled(false);
					if (bombs[k][l]) {
						button[k][l].setBackground(Color.blue);
					}
				}
			}
			JOptionPane.showMessageDialog(null, "You won");
			reset();
		}
	}

	public void reset() {
		for (int k = 0; k < ROW; k++) {
			for (int l = 0; l < COL; l++) {
				button[k][l].setText("");
				button[k][l].setBackground(null);
				button[k][l].setEnabled(true);
				bombs[k][l] = false;
			}
		}
		init(mines);
		flaged = mines;
		remainFlag = flaged;
		remains = (ROW) * (COL);
		beRemain = remains;
	}

	// เริ่มเกม
	public static void main(String[] args) {
		Minesweeper a = new Minesweeper(15, 10);
		a.setDefaultCloseOperation(EXIT_ON_CLOSE);
		a.setVisible(true);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}