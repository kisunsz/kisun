package kisun.maze;

import java.util.Stack;

public class Node {
	private int row;
	private int col;
	private int[][] map;

	public static final int RIGHT = 0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int UP = 3;

	private Node preNode;

	public Node(int row, int col, int[][] map) {
		this(row, col, map, null);
	}

	public Node(int row, int col, int[][] map, Node preNode) {
		this.row = row;
		this.col = col;
		this.map = map;
		this.preNode = preNode;
	}

	public Node nextNode(int direction,Stack<Node> steps) {
		Node nextNode = null;
		if (direction == RIGHT)
			nextNode = new Node(row, col + 1, map, this);
		if (direction == DOWN)
			nextNode = new Node(row + 1, col, map, this);
		if (direction == LEFT)
			nextNode = new Node(row, col - 1, map, this);
		if (direction == UP)
			nextNode = new Node(row - 1, col, map, this);
		if (nextNode == null || nextNode.equals(preNode) || steps.contains(nextNode))
			return null;
		return nextNode;
	}

	public boolean canGo(int dir) {
		switch (dir) {
		case RIGHT:
			return canRight();
		case DOWN:
			return canDown();
		case LEFT:
			return canLeft();
		case UP:
			return canUp();
		default:
			return false;
		}
	}

	public boolean canRight() {
		return !isOutOfBound(row, col + 1) && map[row][col + 1] == 0;
	}

	public boolean canDown() {
		return !isOutOfBound(row + 1, col) && map[row + 1][col] == 0;
	}

	public boolean canLeft() {
		return !isOutOfBound(row, col - 1) && map[row][col - 1] == 0;
	}

	public boolean canUp() {
		return !isOutOfBound(row - 1, col) && map[row - 1][col] == 0;
	}

	private boolean isOutOfBound(int tryRow, int tryCol) {
		if (tryRow >= map.length || tryCol >= map[0].length || tryRow < 0 || tryCol < 0)
			return true;
		return false;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 == null)
			return false;
		Node other = (Node) arg0;
		return this.row == other.row && this.col == other.col;
	}

	@Override
	public int hashCode() {
		int num = this.map.length * this.map[0].length;
		return this.row * num + this.col;
	}
}
