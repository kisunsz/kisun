package kisun.pm2_5;

public class City {
	private int x;
	private int y;
	private String name;
	private int zone;

	public City(String name, int zone, int x, int y) {
		this.x = x;
		this.y = y;
		this.zone = zone;
		this.name = name;
	}

	public int getZone() {
		return zone;
	}

	public String getName() {
		return name;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public String toString() {
		return name;
	}

}
