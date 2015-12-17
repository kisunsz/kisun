package kisun.game2048;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Game {
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
}
