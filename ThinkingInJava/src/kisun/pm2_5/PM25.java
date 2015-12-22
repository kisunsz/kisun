package kisun.pm2_5;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;

public class PM25 {
	private static int IMAGE_COUNT = 64;
	private static double SCALING = 0.5;

	public static void main(String[] args) throws IOException {
		System.getProperties().put("http.proxyHost", "cn-proxy.jp.oracle.com");
		System.getProperties().put("http.proxyPort", "80");

		System.out.println("start..");
		PM25 pm = new PM25();
		// City city = new City("SuZhou", +8, 303, 219);
		// List<PM25Value> list = pm.pm25ByCity(city);
		int count = 0;
		for (City city : pm.readCities()) {
			List<PM25Value> list = pm.pm25ByCity(city);
			count += list.size();
		}
		System.out.println("POST " + count + " records to http://kitest.sinaapp.com");
	}

	public List<City> readCities() throws FileNotFoundException, IOException {
		// city.properties
		File jarPath = new File(PM25.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String propertiesPath = jarPath.getParentFile().getAbsolutePath();
		Properties prop = new Properties();
		prop.load(new FileInputStream(propertiesPath + "/city.properties"));
		List<City> cities = new ArrayList<City>();
		String cityName = prop.getProperty("cityName");
		City city;
		String name;
		if (cityName != null && !cityName.isEmpty()) {
			String[] cityNames = cityName.split(",");
			for (int i = 0; i < cityNames.length; i++) {
				name = cityNames[i];
				city = new City(name, pInt(prop, name + "_zone"), pInt(prop, name + "_x"), pInt(prop, name + "_y"));
				cities.add(city);
			}

		}
		return cities;
	}

	private Integer pInt(Properties prop, String name) {
		return Integer.valueOf(prop.getProperty(name));
	}

	public void simpleCURL(PM25Value value) throws UnsupportedEncodingException, IOException {
		String urlStr = "http://kitest.sinaapp.com/pm2_5/pm2_5.php?xcode=pm25&dt=" + value.getDateTime() + "&city="
				+ value.getCity() + "&level=" + value.getLevel();
		URL url = new URL(urlStr);

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
			for (String line; (line = reader.readLine()) != null;) {
				System.out.println(line);
			}
		}
	}

	public List<PM25Value> pm25ByCity(City city) throws IOException {
		// every 3 h
		URL url;
		double pm25L;
		double pm25L_avg = 0.00;
		PM25Value pm25Value;
		List<PM25Value> pm25Ls = new ArrayList<PM25Value>();

		for (int i = 1; i <= IMAGE_COUNT; i++) {
			url = genUrl(i);
			pm25L = processOneImage(url, city);
			pm25L_avg += pm25L;

			if (i % (1 / SCALING) == 0) {
				pm25L_avg = pm25L_avg * SCALING;
				pm25Value = new PM25Value(city, pm25L_avg, imageDateTime(i));
				pm25Ls.add(pm25Value);
				simpleCURL(pm25Value);
				pm25L_avg = 0.00;
			}
		}
		return pm25Ls;
	}

	public String imageDateTime(int i) {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.add(Calendar.HOUR_OF_DAY, (i - 9) * 3);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
		return sdf.format(cal.getTime());
	}

	private URL genUrl(int num) throws MalformedURLException {
		String numStr = num < 10 ? "0" + num : String.valueOf(num);
		return new URL("http://sprintars.riam.kyushu-u.ac.jp/images/casu_asia_jp_" + numStr + ".png");
	}

	private double processOneImage(URL url, City city) throws IOException {
		Color[][] image = getImageGRB(url);
		List<Color> level = initLevel(image);
		return level.indexOf(image[city.getX()][city.getY()]) + 1;
	}

	private Color[][] getImageGRB(URL url) throws IOException {
		Color[][] result = null;
		BufferedImage bufImg = ImageIO.read(url);
		int height = bufImg.getHeight();
		int width = bufImg.getWidth();
		int rgb;
		result = new Color[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				rgb = bufImg.getRGB(i, j) & 0xFFFFFF;
				result[i][j] = new Color(rgb);
			}
		}

		return result;
	}

	private static List<Color> initLevel(Color[][] image) {
		List<Color> level = new ArrayList<Color>();
		level.add(image[162][413]);
		level.add(image[190][413]);
		level.add(image[235][413]);
		level.add(image[276][413]);
		level.add(image[322][413]);
		level.add(image[365][413]);
		level.add(image[408][413]);
		level.add(image[438][413]);
		return level;
	}

}
