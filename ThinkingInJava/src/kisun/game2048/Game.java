package kisun.game2048;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class Game extends JPanel {
	private static final long serialVersionUID = -8038827443106016533L;
	private static final String WINDOWS_TITLE = "2048 Game";

	public static void main(String[] args) {
		JFrame game = new JFrame();
		game.setTitle(WINDOWS_TITLE);
		game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		game.setSize(340, 400);
		game.setResizable(false);

		// game.add(new Game2048());

		game.setLocationRelativeTo(null);
		game.setVisible(true);
	}

	static class Block {
		int value;

		public Block() {
			this(0);
		}

		public Block(int num) {
			value = num;
		}

		public boolean isEmpty() {
			return value == 0;
		}

		public Color getForeground() {
			return value < 16 ? new Color(0x776e65) : new Color(0xf9f6f2);
		}

		public Color getBackground() {
			switch (value) {
			case 2:
				return new Color(0xeee4da);
			case 4:
				return new Color(0xede0c8);
			case 8:
				return new Color(0xf2b179);
			case 16:
				return new Color(0xf59563);
			case 32:
				return new Color(0xf67c5f);
			case 64:
				return new Color(0xf65e3b);
			case 128:
				return new Color(0xedcf72);
			case 256:
				return new Color(0xedcc61);
			case 512:
				return new Color(0xedc850);
			case 1024:
				return new Color(0xedc53f);
			case 2048:
				return new Color(0xedc22e);
			}
			return new Color(0xcdc1b4);
		}
	}
}
