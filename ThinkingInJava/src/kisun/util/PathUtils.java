package kisun.util;

import java.net.URL;

/**
 * @author Kingsley Sun - 24 Nov 2015
 */
public class PathUtils {
	public static void main(String[] args) {
		System.out.println(getPath());
	}

	public static String getPath() {
		return System.getProperty("user.dir");
	}

	public URL getPath(String resourceName) {
		// return this.getClass().getClassLoader().getResource(resourceName);
		// return PathUtils.class.getResource(resourceName);
		return Thread.currentThread().getContextClassLoader().getResource(resourceName);
	}

}
