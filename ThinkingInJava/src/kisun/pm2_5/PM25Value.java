package kisun.pm2_5;

public class PM25Value {
	private City city;
	private double level;
	private String dateTime;

	public PM25Value(City city, double level, String dateTime) {
		this.city = city;
		this.level = level;
		this.dateTime = dateTime;
	}

	public City getCity() {
		return city;
	}

	public double getLevel() {
		return level;
	}

	public String getDateTime() {
		return dateTime;
	}

	@Override
	public String toString() {
		return "[" + dateTime + "," + city.toString() + "]" + ":" + level;
	}

}
