package kisun.maze;

import java.util.Stack;

public class Start {

	private static final String BLOCK = "#";
	private static final String EMPTY = "o";
	private static final String STEP = "=";

	private Node startNode;
	private Node endNode;

	private int[][] init() {
		int[][] map = radomMap(17, 17);
		startNode = new Node(0, 0, map);
		endNode = new Node(map.length - 1, map[0].length - 1, map);
		return map;
	}

	public static void main(String[] args) {
		Start start = new Start();
		start.find();
	}

	private void find() {
		Stack<Node> steps = null;
		int[][] map = null;
		boolean find = false;
		int i = 1;
		while (!find) {
			steps = new Stack<Node>();
			map = init();
			steps.push(startNode);
			find = lookingFor(steps, startNode);
			System.out.println("Try [" + i++ + "]");
		}
		printMap(map, steps);
	}

	private boolean lookingFor(Stack<Node> steps, Node node) {
		boolean find = false;
		if (node != null && node.equals(endNode))
			return true;
		Node next = null;
		int dir = Node.RIGHT;
		while (dir <= 3) {
			if (node.canGo(dir)) {
				next = node.nextNode(dir, steps);
				if (next != null) {
					steps.push(next);
					find = lookingFor(steps, next);
					if (find)
						break;
				}
			}
			dir++;
		}
		if (dir > 3)
			steps.pop();
		return find;
	}

	private int[][] radomMap(int x, int y) {
		// int[][] map = { { 0, 0, 0, 0 }, { 0, 1, 0, 0 }, { 1, 1, 1, 1 }, { 1,
		// 0, 0, 0 } };
		int[][] map = new int[x][y];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				map[i][j] = Math.random() < 0.5 ? 0 : 1;
			}
		}
		map[0][0] = 0;
		map[x - 1][y - 1] = 0;
		return map;
	}

	private void printMap(int[][] map, Stack<Node> steps) {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				Node node = new Node(i, j, map);
				if (map[i][j] == 1) {
					System.out.print(BLOCK);
				} else if (steps.contains(node)) {
					System.out.print(STEP);
				} else {
					System.out.print(EMPTY);
				}
			}
			System.out.println();
		}
	}

}
