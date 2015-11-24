package kisun.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.output.NullOutputStream;

/**
 * @author Kingsley Sun - 24 Nov 2015
 */
public class FileUtils {

	/*
	 * 获取系统的临时目录路径
	 */
	public static String getTempDirectoryPath() {
		return System.getProperty("java.io.tmpdir");
	}

	/*
	 * 获取代表系统临时目录的文件
	 */
	public static File getTempDirectory() {
		return new File(getTempDirectoryPath());
	}

	/*
	 * 获取用户的主目录路径
	 */
	public static String getUserDirectoryPath() {
		return System.getProperty("user.home");
	}

	/*
	 * 获取代表用户主目录的文件
	 */
	public static File getUserDirectory() {
		return new File(getUserDirectoryPath());
	}

	/*
	 * 根据指定的文件获取一个新的文件输入流
	 */
	public static FileInputStream openInputStream(File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file + "' exists but is adirectory");
			}

			if (file.canRead() == false) {
				throw new IOException("File '" + file + "' cannot be read");
			}
		} else {
			throw new FileNotFoundException("File '" + file + "' does notexist");
		}
		return new FileInputStream(file);
	}

	/*
	 * 根据指定的文件获取一个新的文件输出流
	 */
	public static FileOutputStream openOutputStream(File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File'" + file + "' exists but is a directory");
			}
			if (file.canWrite() == false) {
				throw new IOException("File '" + file + "' cannot be written to");
			}
		} else {
			File parent = file.getParentFile();
			if (parent != null && parent.exists() == false) {
				if (parent.mkdirs() == false) {
					throw new IOException("File '" + file + "' could not be created");
				}
			}
		}
		return new FileOutputStream(file);
	}

	/*
	 * 字节转换成直观带单位的值（包括单位GB，MB，KB或字节）
	 */
	public static String byteCountToDisplaySize(long size) {
		long ONE_KB = 1024;
		long ONE_MB = 1024 * ONE_KB;
		long ONE_GB = 1024 * ONE_MB;
		String displaySize;
		if (size / ONE_GB > 0) {
			displaySize = String.valueOf(size / ONE_GB) + " GB";
		} else if (size / ONE_MB > 0) {
			displaySize = String.valueOf(size / ONE_MB) + " MB";
		} else if (size / ONE_KB > 0) {
			displaySize = String.valueOf(size / ONE_KB) + " KB";
		} else {
			displaySize = String.valueOf(size) + " bytes";
		}
		return displaySize;
	}

	/*
	 * 创建一个空文件，若文件应经存在则只更改文件的最近修改时间
	 */
	public static void touch(File file) throws IOException {
		if (!file.exists()) {
			OutputStream out = openOutputStream(file);
			IOUtils.closeQuietly(out);
		}
		boolean success = file.setLastModified(System.currentTimeMillis());
		if (!success) {
			throw new IOException("Unableto set the last modification time for " + file);
		}
	}

	/*
	 * 把相应的文件集合转换成文件数组
	 */
	public static File[] convertFileCollectionToFileArray(Collection<File> files) {

		return files.toArray(new File[files.size()]);

	}

	/*
	 * 根据一个过滤规则获取一个目录下的文件
	 */
	private static void innerListFiles(Collection<File> files, File directory, FileFilter filter) {
		File[] found = directory.listFiles((FileFilter) filter);
		if (found != null) {
			for (File file : found) {
				if (file.isDirectory()) {
					innerListFiles(files, file, filter);
				} else {
					files.add(file);
				}
			}
		}
	}

	/*
	 * 根据一个FileFilter过滤规则获取一个目录下的文件集合listFiles
	 */
	public static Collection<File> listFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException("Parameter'directory' is not a directory");
		}
		if (fileFilter == null) {
			throw new NullPointerException("Parameter 'fileFilter' is null");
		}
		// Setup effective file filter

		IOFileFilter effFileFilter = FileFilterUtils.and(fileFilter,
				FileFilterUtils.notFileFilter(DirectoryFileFilter.INSTANCE));
		// Setup effective directory filter

		IOFileFilter effDirFilter;
		if (dirFilter == null) {
			effDirFilter = FalseFileFilter.INSTANCE;
		} else {
			effDirFilter = FileFilterUtils.and(dirFilter, DirectoryFileFilter.INSTANCE);
		}

		// Find files
		Collection<File> files = new LinkedList<File>();
		innerListFiles(files, directory, FileFilterUtils.or(effFileFilter, effDirFilter));
		return files;
	}

	/*
	 * 根据一个FileFilter过滤规则获取一个目录下的文件集合的Iterator迭代器
	 */
	public static Iterator<File> iterateFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
		return listFiles(directory, fileFilter, dirFilter).iterator();
	}

	/*
	 * 把指定的字符串数组变成后缀名格式字符串数组
	 */
	private static String[] toSuffixes(String[] extensions) {
		String[] suffixes = new String[extensions.length];
		for (int i = 0; i < extensions.length; i++) {
			suffixes[i] = "." + extensions[i];
		}
		return suffixes;
	}

	/*
	 * 查找一个目录下面符合对应扩展名的文件的集合
	 */
	public static Collection<File> listFiles(File directory, String[] extensions, boolean recursive) {
		IOFileFilter filter;
		if (extensions == null) {
			filter = TrueFileFilter.INSTANCE;
		} else {
			String[] suffixes = toSuffixes(extensions);
			filter = new SuffixFileFilter(suffixes);
		}
		return listFiles(directory, filter, (recursive ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE));
	}

	/*
	 * 查找一个目录下面符合对应扩展名的文件的集合的迭代器
	 */
	public static Iterator<File> iterateFiles(File directory, String[] extensions, boolean recursive) {

		return listFiles(directory, extensions, recursive).iterator();

	}

	/*
	 * 判断两个文件是否相等
	 */
	public static boolean contentEquals(File file1, File file2) throws IOException {
		boolean file1Exists = file1.exists();
		if (file1Exists != file2.exists()) {
			return false;
		}
		if (!file1Exists) {
			// two not existing files areequal
			return true;
		}

		if (file1.isDirectory() || file2.isDirectory()) {
			// don't want to comparedirectory contents
			throw new IOException("Can't compare directories, only files");
		}

		if (file1.length() != file2.length()) {
			// lengths differ, cannot beequal
			return false;
		}

		if (file1.getCanonicalFile().equals(file2.getCanonicalFile())) {
			// same file
			return true;
		}
		InputStream input1 = null;
		InputStream input2 = null;
		try {
			input1 = new FileInputStream(file1);
			input2 = new FileInputStream(file2);
			return IOUtils.contentEquals(input1, input2);
		} finally {
			IOUtils.closeQuietly(input1);
			IOUtils.closeQuietly(input2);
		}
	}

	/*
	 * 根据一个Url来创建一个文件toFile(URL url)
	 */
	public static File toFile(URL url) throws IOException {
		if (url == null || !"file".equalsIgnoreCase(url.getProtocol())) {
			return null;
		} else {
			String filename = url.getFile().replace('/', File.separatorChar);
			filename = decodeUrl(filename);
			return new File(filename);
		}
	}

	/*
	 * 对一个Url字符串进行将指定的URL按照RFC 3986 进行转换decodeUrl(Stringurl)
	 */
	public static String decodeUrl(String url) throws IOException {
		String decoded = url;
		if (url != null && url.indexOf('%') >= 0) {
			int n = url.length();
			StringBuffer buffer = new StringBuffer();
			ByteBuffer bytes = ByteBuffer.allocate(n);
			for (int i = 0; i < n;) {
				if (url.charAt(i) == '%') {
					try {

						do {

							byte octet = (byte) Integer.parseInt(url.substring(i + 1, i + 3), 16);

							bytes.put(octet);

							i += 3;

						} while (i < n && url.charAt(i) == '%');

						continue;

					} catch (RuntimeException e) {

						// malformedpercent-encoded octet, fall through and

						// append charactersliterally

					} finally {

						if (bytes.position() > 0) {

							bytes.flip();

							buffer.append(IOUtils.toString(bytes.array(), "UTF-8"));

							bytes.clear();

						}

					}

				}

				buffer.append(url.charAt(i++));

			}

			decoded = buffer.toString();

		}

		return decoded;

	}

	/*
	 * 将一个URL数组转化成一个文件数组toFiles(URL[] urls)
	 */

	public static File[] toFiles(URL[] urls) throws IOException {
		File[] EMPTY_FILE_ARRAY = {};
		if (urls == null || urls.length == 0) {
			return EMPTY_FILE_ARRAY;
		}
		File[] files = new File[urls.length];
		for (int i = 0; i < urls.length; i++) {
			URL url = urls[i];
			if (url != null) {
				if (url.getProtocol().equals("file") == false) {
					throw new IllegalArgumentException("URL couldnot be converted to a File: " + url);
				}
				files[i] = toFile(url);
			}
		}
		return files;
	}

	/*
	 * 将一个文件数组转化成一个URL数组toURLs(File [] files)
	 */

	public static URL[] toURLs(File[] files) throws IOException {

		URL[] urls = new URL[files.length];

		for (int i = 0; i < urls.length; i++) {

			urls[i] = files[i].toURI().toURL();

		}

		return urls;

	}

	/*
	 * 拷贝一个文件到指定的目录文件
	 */
	public static void copyFileToDirectory(File srcFile, File destDir) throws IOException {

		copyFileToDirectory(srcFile, destDir, true);

	}

	/*
	 * 拷贝一个文件到指定的目录文件并且设置是否更新文件的最近修改时间
	 */
	public static void copyFileToDirectory(File srcFile, File destDir, boolean preserveFileDate) throws IOException {

		if (destDir == null) {

			throw new NullPointerException("Destination must not be null");

		}

		if (destDir.exists() && destDir.isDirectory() == false) {

			throw new IllegalArgumentException("Destination '" + destDir + "' is not adirectory");

		}

		File destFile = new File(destDir, srcFile.getName());

		copyFile(srcFile, destFile, preserveFileDate);

	}

	/*
	 * 拷贝文件到新的文件中并且保存最近修改时间copyFile(File srcFile, File destFile)
	 */
	public static void copyFile(File srcFile, File destFile) throws IOException {

		copyFile(srcFile, destFile, true);

	}

	/*
	 * 拷贝文件到新的文件中并且设置是否保存最近修改时间
	 */
	public static void copyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {

		if (srcFile == null) {

			throw new NullPointerException("Source must not be null");

		}

		if (destFile == null) {

			throw new NullPointerException("Destination must not be null");

		}

		if (srcFile.exists() == false) {

			throw new FileNotFoundException("Source '" + srcFile + "' does notexist");

		}

		if (srcFile.isDirectory()) {

			throw new IOException("Source '" + srcFile + "' exists but is adirectory");

		}

		if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {

			throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");

		}

		if (destFile.getParentFile() != null && destFile.getParentFile().exists() == false) {

			if (destFile.getParentFile().mkdirs() == false) {

				throw new IOException("Destination '" + destFile + "' directory cannot becreated");

			}

		}

		if (destFile.exists() && destFile.canWrite() == false) {

			throw new IOException("Destination '" + destFile + "' exists but isread-only");

		}

		doCopyFile(srcFile, destFile, preserveFileDate);

	}

	/*
	 * 拷贝文件到新的文件中并且设置是否保存最近修改时间
	 */
	private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
		long FIFTY_MB = 50 * 1024 * 1024;

		if (destFile.exists() && destFile.isDirectory()) {

			throw new IOException("Destination '" + destFile + "' exists but is adirectory");

		}

		FileInputStream fis = null;

		FileOutputStream fos = null;

		FileChannel input = null;

		FileChannel output = null;

		try {

			fis = new FileInputStream(srcFile);

			fos = new FileOutputStream(destFile);

			input = fis.getChannel();

			output = fos.getChannel();

			long size = input.size();

			long pos = 0;

			long count = 0;

			while (pos < size) {

				count = (size - pos) > FIFTY_MB ? FIFTY_MB : (size - pos);

				pos += output.transferFrom(input, pos, count);

			}

		} finally {

			IOUtils.closeQuietly(output);

			IOUtils.closeQuietly(fos);

			IOUtils.closeQuietly(input);

			IOUtils.closeQuietly(fis);

		}

		if (srcFile.length() != destFile.length()) {

			throw new IOException("Failed to copy full contents from '" +

			srcFile + "' to '" + destFile + "'");

		}

		if (preserveFileDate) {

			destFile.setLastModified(srcFile.lastModified());

		}

	}

	/*
	 * 将一个目录拷贝到另一目录中，并且保存最近更新时间
	 */
	public static void copyDirectoryToDirectory(File srcDir, File destDir) throws IOException {

		if (srcDir == null) {

			throw new NullPointerException("Source must not be null");

		}

		if (srcDir.exists() && srcDir.isDirectory() == false) {

			throw new IllegalArgumentException("Source'" + destDir + "' is not a directory");

		}

		if (destDir == null) {

			throw new NullPointerException("Destination must not be null");

		}

		if (destDir.exists() && destDir.isDirectory() == false) {

			throw new IllegalArgumentException("Destination '" + destDir + "' is not adirectory");

		}

		copyDirectory(srcDir, new File(destDir, srcDir.getName()), true);

	}

	/*
	 * 拷贝整个目录到新的位置，并且保存最近修改时间copyDirectory(File srcDir, File destDir)
	 */
	public static void copyDirectory(File srcDir, File destDir) throws IOException {

		copyDirectory(srcDir, destDir, true);

	}

	/*
	 * 拷贝整个目录到新的位置，并且设置是否保存最近修改时间copyDirectory(File srcDir, File destDir,
	 * boolean preserveFileDate)
	 */
	public static void copyDirectory(File srcDir, File destDir, boolean preserveFileDate) throws IOException {

		copyDirectory(srcDir, destDir, null, preserveFileDate);

	}

	/*
	 * 拷贝过滤后的目录到指定的位置，并且保存最近修改时间copyDirectory(File srcDir, File destDir,
	 * FileFilter filter)
	 */
	public static void copyDirectory(File srcDir, File destDir, FileFilter filter) throws IOException {
		copyDirectory(srcDir, destDir, filter, true);
	}

	/*
	 * 拷贝过滤后的目录到指定的位置，并且设置是否保存最近修改时间
	 */
	public static void copyDirectory(File srcDir, File destDir, FileFilter filter, boolean preserveFileDate)
			throws IOException {

		if (srcDir == null) {

			throw new NullPointerException("Source must not be null");

		}

		if (destDir == null) {

			throw new NullPointerException("Destination must not be null");

		}

		if (srcDir.exists() == false) {

			throw new FileNotFoundException("Source '" + srcDir + "' does notexist");

		}

		if (srcDir.isDirectory() == false) {

			throw new IOException("Source '" + srcDir + "' exists but is not adirectory");

		}

		if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {

			throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");

		}

		// Cater for destination being directorywithin the source directory (see
		// IO-141)

		List<String> exclusionList = null;

		if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {

			File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);

			if (srcFiles != null && srcFiles.length > 0) {

				exclusionList = new ArrayList<String>(srcFiles.length);

				for (File srcFile : srcFiles) {

					File copiedFile = new File(destDir, srcFile.getName());

					exclusionList.add(copiedFile.getCanonicalPath());

				}

			}

		}

		doCopyDirectory(srcDir, destDir, filter, preserveFileDate, exclusionList);

	}

	/*
	 * 内部拷贝目录的方法
	 */
	private static void doCopyDirectory(File srcDir, File destDir, FileFilter filter, boolean preserveFileDate,
			List<String> exclusionList) throws IOException {

		// recurse

		File[] files = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);

		if (files == null) { // null if security restricted

			throw new IOException("Failed to list contents of " + srcDir);

		}

		if (destDir.exists()) {

			if (destDir.isDirectory() == false) {

				throw new IOException("Destination '" + destDir + "' exists but is not adirectory");

			}

		} else {

			if (destDir.mkdirs() == false) {

				throw new IOException("Destination '" + destDir + "' directory cannot becreated");

			}

		}

		if (destDir.canWrite() == false) {

			throw new IOException("Destination '" + destDir + "' cannot be writtento");

		}

		for (File file : files) {

			File copiedFile = new File(destDir, file.getName());

			if (exclusionList == null || !exclusionList.contains(file.getCanonicalPath())) {

				if (file.isDirectory()) {

					doCopyDirectory(file, copiedFile, filter, preserveFileDate, exclusionList);

				} else {

					doCopyFile(file, copiedFile, preserveFileDate);

				}

			}

		}

		// Do this last, as the above hasprobably affected directory metadata

		if (preserveFileDate) {

			destDir.setLastModified(srcDir.lastModified());

		}

	}

	/*
	 * 根据一个Url拷贝字节到一个文件中copyURLToFile(URL source, File destination)
	 */

	public static void copyURLToFile(URL source, File destination) throws IOException {

		InputStream input = source.openStream();

		copyInputStreamToFile(input, destination);

	}

	/*
	 * 根据一个Url拷贝字节到一个文件中，并且可以设置连接的超时时间和读取的超时时间
	 */
	public static void copyURLToFile(URL source, File destination, int connectionTimeout, int readTimeout)
			throws IOException {

		URLConnection connection = source.openConnection();

		connection.setConnectTimeout(connectionTimeout);

		connection.setReadTimeout(readTimeout);

		InputStream input = connection.getInputStream();

		copyInputStreamToFile(input, destination);

	}

	/*
	 * 拷贝一个字节流到一个文件中，如果这个文件不存在则新创建一个，存在的话将被重写进内容
	 */
	public static void copyInputStreamToFile(InputStream source, File destination) throws IOException {

		try {

			FileOutputStream output = openOutputStream(destination);

			try {

				IOUtils.copy(source, output);

			} finally {

				IOUtils.closeQuietly(output);

			}

		} finally {

			IOUtils.closeQuietly(source);

		}

	}

	/*
	 * 递归的删除一个目录deleteDirectory(File directory)
	 */
	public static void deleteDirectory(File directory) throws IOException {

		if (!directory.exists()) {

			return;

		}

		if (!isSymlink(directory)) {

			cleanDirectory(directory);

		}

		if (!directory.delete()) {

			String message =

			"Unable to deletedirectory " + directory + ".";

			throw new IOException(message);

		}

	}

	/*
	 * 安静模式删除目录，操作过程中会抛出异常deleteQuietly(File file)
	 */
	public static boolean deleteQuietly(File file) {

		if (file == null) {

			return false;

		}

		try {

			if (file.isDirectory()) {

				cleanDirectory(file);

			}

		} catch (Exception ignored) {

		}

		try {

			return file.delete();

		} catch (Exception ignored) {

			return false;

		}

	}

	/*
	 * 清除一个目录而不删除它cleanDirectory(File directory)
	 */
	public static void cleanDirectory(File directory) throws IOException {

		if (!directory.exists()) {

			String message = directory + " does not exist";

			throw new IllegalArgumentException(message);

		}

		if (!directory.isDirectory()) {

			String message = directory + " is not a directory";

			throw new IllegalArgumentException(message);

		}

		File[] files = directory.listFiles();

		if (files == null) { // null if security restricted

			throw new IOException("Failed to list contents of " + directory);

		}

		IOException exception = null;

		for (File file : files) {

			try {

				forceDelete(file);

			} catch (IOException ioe) {

				exception = ioe;

			}

		}

		if (null != exception) {

			throw exception;

		}

	}

	/*
	 * 等待NFS来传播一个文件的创建，实施超时waitFor(File file, int seconds)
	 */
	public static boolean waitFor(File file, int seconds) {

		int timeout = 0;

		int tick = 0;

		while (!file.exists()) {

			if (tick++ >= 10) {

				tick = 0;

				if (timeout++ > seconds) {

					return false;

				}

			}

			try {

				Thread.sleep(100);

			} catch (InterruptedException ignore) {

				// ignore exception

			} catch (Exception ex) {

				break;

			}

		}

		return true;

	}

	/*
	 * 把一个文件的内容读取到一个对应编码的字符串中去readFileToString(File file, String encoding)
	 */
	public static String readFileToString(File file, String encoding) throws IOException {

		InputStream in = null;

		try {

			in = openInputStream(file);

			return IOUtils.toString(in, encoding);

		} finally {

			IOUtils.closeQuietly(in);

		}

	}

	/*
	 * 读取文件的内容到虚拟机的默认编码字符串readFileToString(File file)
	 */
	public static String readFileToString(File file) throws IOException {

		return readFileToString(file, null);

	}

	/*
	 * 把一个文件转换成字节数组返回readFileToByteArray(File file)
	 */
	public static byte[] readFileToByteArray(File file) throws IOException {

		InputStream in = null;

		try {

			in = openInputStream(file);

			return IOUtils.toByteArray(in);

		} finally {

			IOUtils.closeQuietly(in);

		}

	}

	/*
	 * 把文件中的内容逐行的拷贝到一个对应编码的
	 */
	public static List<String> readLines(File file, String encoding) throws IOException {

		InputStream in = null;

		try {

			in = openInputStream(file);

			return IOUtils.readLines(in, encoding);

		} finally {

			IOUtils.closeQuietly(in);

		}

	}

	/*
	 * 把文件中的内容逐行的拷贝到一个虚拟机默认编码的
	 */
	public static List<String> readLines(File file) throws IOException {
		return readLines(file, null);
	}

	/*
	 * 根据对应编码返回对应文件内容的行迭代器
	 */
	public static LineIterator lineIterator(File file, String encoding) throws IOException {

		InputStream in = null;

		try {

			in = openInputStream(file);

			return IOUtils.lineIterator(in, encoding);

		} catch (IOException ex) {

			IOUtils.closeQuietly(in);

			throw ex;

		} catch (RuntimeException ex) {

			IOUtils.closeQuietly(in);

			throw ex;

		}

	}

	/*
	 * 根据虚拟机默认编码返回对应文件内容的行迭代器lineIterator(File file)
	 */

	public static LineIterator lineIterator(File file) throws IOException {

		return lineIterator(file, null);

	}

	/*
	 * 根据对应编码把字符串写进对应的文件中writeStringToFile(File file, String data, String
	 * encoding)
	 */

	public static void writeStringToFile(File file, String data, String encoding) throws IOException {

		OutputStream out = null;

		try {

			out = openOutputStream(file);

			IOUtils.write(data, out, encoding);

		} finally {

			IOUtils.closeQuietly(out);

		}

	}

	/*
	 * 根据虚拟机默认编码把字符串写进对应的文件中writeStringToFile(File file, String data)
	 */

	public static void writeStringToFile(File file, String data) throws IOException {

		writeStringToFile(file, data, null);

	}

	/*
	 * 根据虚拟机默认的编码把CharSequence写入到文件中(File file, CharSequence data)
	 */

	public static void write(File file, CharSequence data) throws IOException {

		String str = data == null ? null : data.toString();

		writeStringToFile(file, str);

	}

	/*
	 * 根据对应的编码把CharSequence写入到文件中write(File file, CharSequence data, String
	 * encoding)
	 */

	public static void write(File file, CharSequence data, String encoding) throws IOException {

		String str = data == null ? null : data.toString();

		writeStringToFile(file, str, encoding);

	}

	/*
	 * 把一个字节数组写入到指定的文件中writeByteArrayToFile(File file, byte[] data)
	 */

	public static void writeByteArrayToFile(File file, byte[] data) throws IOException {

		OutputStream out = null;

		try {

			out = openOutputStream(file);

			out.write(data);

		} finally {

			IOUtils.closeQuietly(out);

		}

	}

	/*
	 * 把集合中的内容根据对应编码逐项插入到文件中writeLines(File file, String encoding, Collection<?>
	 * lines)
	 */

	public static void writeLines(File file, String encoding, Collection<?> lines) throws IOException {

		writeLines(file, encoding, lines, null);

	}

	/*
	 * 把集合中的内容根据虚拟机默认编码逐项插入到文件中writeLines(File file, Collection<?> lines)
	 */
	public static void writeLines(File file, Collection<?> lines) throws IOException {

		writeLines(file, null, lines, null);

	}

	/*
	 * 把集合中的内容根据对应字符编码和行编码逐项插入到文件中
	 */

	public static void writeLines(File file, String encoding, Collection<?> lines, String lineEnding)

			throws IOException {

		OutputStream out = null;

		try {

			out = openOutputStream(file);

			IOUtils.writeLines(lines, lineEnding, out, encoding);

		} finally {

			IOUtils.closeQuietly(out);

		}

	}

	/*
	 * 把集合中的内容根据对应行编码逐项插入到文件中[java]
	 */

	public static void writeLines(File file, Collection<?> lines, String lineEnding) throws IOException {

		writeLines(file, null, lines, lineEnding);

	}

	/*
	 * 删除一个文件,如果是目录则递归删除forceDelete(File file)
	 */

	public static void forceDelete(File file) throws IOException {

		if (file.isDirectory()) {

			deleteDirectory(file);

		} else {

			boolean filePresent = file.exists();

			if (!file.delete()) {

				if (!filePresent) {

					throw new FileNotFoundException("File does not exist: " + file);

				}

				String message =

				"Unable to deletefile: " + file;

				throw new IOException(message);

			}

		}

	}

	/*
	 * 当虚拟机退出关闭时删除文件forceDeleteOnExit(File file)
	 */

	public static void forceDeleteOnExit(File file) throws IOException {

		if (file.isDirectory()) {

			deleteDirectoryOnExit(file);

		} else {

			file.deleteOnExit();

		}

	}

	/*
	 * 当虚拟机退出关闭时递归删除一个目录deleteDirectoryOnExit(File directory)
	 */

	private static void deleteDirectoryOnExit(File directory) throws IOException {

		if (!directory.exists()) {

			return;

		}

		if (!isSymlink(directory)) {

			cleanDirectoryOnExit(directory);

		}

		directory.deleteOnExit();

	}

	/*
	 * 在虚拟机退出或者关闭时清除一个目录而不删除它[java]
	 */
	private static void cleanDirectoryOnExit(File directory) throws IOException {

		if (!directory.exists()) {

			String message = directory + " does not exist";

			throw new IllegalArgumentException(message);

		}

		if (!directory.isDirectory()) {

			String message = directory + " is not a directory";

			throw new IllegalArgumentException(message);

		}

		File[] files = directory.listFiles();

		if (files == null) { // null if security restricted

			throw new IOException("Failed to list contents of " + directory);

		}

		IOException exception = null;

		for (File file : files) {

			try {

				forceDeleteOnExit(file);

			} catch (IOException ioe) {

				exception = ioe;

			}

		}

		if (null != exception) {

			throw exception;

		}

	}

	/*
	 * 创建一个目录除了不存在的父目录其他所必须的都可以创建forceMkdir(File directory)
	 */

	public static void forceMkdir(File directory) throws IOException {

		if (directory.exists()) {

			if (!directory.isDirectory()) {

				String message =

				"File "

						+ directory

						+ " exists andis "

						+ "not adirectory. Unable to create directory.";

				throw new IOException(message);

			}

		} else {

			if (!directory.mkdirs()) {

				// Double-check that someother thread or process hasn't made

				// the directory in thebackground

				if (!directory.isDirectory())

				{

					String message =

					"Unable tocreate directory " + directory;

					throw new IOException(message);

				}

			}

		}

	}

	/*
	 * 获取文件或者目录的大小sizeOf(File file)
	 */

	public static long sizeOf(File file) {

		if (!file.exists()) {

			String message = file + "does not exist";

			throw new IllegalArgumentException(message);

		}

		if (file.isDirectory()) {

			return sizeOfDirectory(file);

		} else {

			return file.length();

		}

	}

	/*
	 * 获取目录的大小sizeOfDirectory(File directory)
	 */

	public static long sizeOfDirectory(File directory) {

		if (!directory.exists()) {

			String message = directory + " does not exist";

			throw new IllegalArgumentException(message);

		}

		if (!directory.isDirectory()) {

			String message = directory + " is not a directory";

			throw new IllegalArgumentException(message);

		}

		long size = 0;

		File[] files = directory.listFiles();

		if (files == null) { // null if security restricted

			return 0L;

		}

		for (File file : files) {

			size += sizeOf(file);

		}

		return size;

	}

	/*
	 * 测试指定文件的最后修改日期是否比reference的文件新isFilenew er(File
	 * file,Filereference)[java]view plaincopyprint?
	 */

	public static boolean isFilenewer(File file, File reference) {

		if (reference == null) {

			throw new IllegalArgumentException("No specified reference file");

		}

		if (!reference.exists()) {

			throw new IllegalArgumentException("The reference file '"

					+ reference + "'doesn't exist");

		}
		return isFilenewer(file, reference.lastModified());
	}

	/*
	 * 检测指定文件的最后修改时间是否在指定日期之前isFilenew er(File file, Date date)
	 */

	public static boolean isFilenewer(File file, Date date) {

		if (date == null) {

			throw new IllegalArgumentException("No specified date");

		}

		return isFilenewer(file, date.getTime());

	}

	/*
	 * 检测指定文件的最后修改时间（毫秒）
	 */
	// 是否在指定日期之前isFilenew er(File file, long timeMillis)

	public static boolean isFilenewer(File file, long timeMillis) {

		if (file == null) {

			throw new IllegalArgumentException("No specified file");

		}

		if (!file.exists()) {

			return false;

		}

		return file.lastModified() > timeMillis;

	}

	/*
	 * 检测指定文件的最后修改日期是否比reference文件的晚isFileOlder(File
	 * file,Filereference)[java]view plaincopyprint?
	 */

	public static boolean isFileOlder(File file, File reference) {

		if (reference == null) {

			throw new IllegalArgumentException("No specified reference file");

		}

		if (!reference.exists()) {

			throw new IllegalArgumentException("The reference file '"

					+ reference + "'doesn't exist");

		}

		return isFileOlder(file, reference.lastModified());

	}

	/*
	 * 检测指定文件的最后修改时间是否在指定日期之后isFileOlder(File file, Date date)
	 */

	public static boolean isFileOlder(File file, Date date) {

		if (date == null) {

			throw new IllegalArgumentException("No specified date");

		}

		return isFileOlder(file, date.getTime());

	}

	/*
	 * 检测指定文件的最后修改时间（毫秒）是否在指定日期之后isFileOlder(File file, long timeMillis)
	 */
	public static boolean isFileOlder(File file, long timeMillis) {

		if (file == null) {

			throw new IllegalArgumentException("Nospecified file");

		}

		if (!file.exists()) {

			return false;

		}

		return file.lastModified() < timeMillis;

	}

	/*
	 * 计算使用CRC32校验程序文件的校验和checksumCRC32(File file)
	 */

	public static long checksumCRC32(File file) throws IOException {

		CRC32 crc = new CRC32();

		checksum(file, crc);

		return crc.getValue();

	}

	/*
	 * 计算一个文件使用指定的校验对象的校验checksum(File file,Checksum checksum)[java]view
	 * plaincopyprint?
	 */

	public static Checksum checksum(File file, Checksum checksum) throws IOException {

		if (file.isDirectory()) {

			throw new IllegalArgumentException("Checksums can't be computed ondirectories");

		}

		InputStream in = null;

		try {

			in = new CheckedInputStream(new FileInputStream(file), checksum);

			IOUtils.copy(in, new NullOutputStream());

		} finally {

			IOUtils.closeQuietly(in);

		}

		return checksum;

	}

	/*
	 * 移动目录到新的目录并且删除老的目录moveDirectory(File srcDir, File destDir)
	 */

	public static void moveDirectory(File srcDir, File destDir) throws IOException {

		if (srcDir == null) {

			throw new NullPointerException("Source must not be null");

		}

		if (destDir == null) {

			throw new NullPointerException("Destination must not be null");

		}

		if (!srcDir.exists()) {

			throw new FileNotFoundException("Source '" + srcDir + "' does notexist");

		}

		if (!srcDir.isDirectory()) {

			throw new IOException("Source '" + srcDir + "' is not a directory");

		}

		if (destDir.exists()) {

			throw new FileExistsException("Destination '" + destDir + "' alreadyexists");

		}

		boolean rename = srcDir.renameTo(destDir);

		if (!rename) {

			copyDirectory(srcDir, destDir);

			deleteDirectory(srcDir);

			if (srcDir.exists()) {

				throw new IOException("Failed to delete original directory '" + srcDir +

				"' after copyto '" + destDir + "'");

			}

		}

	}

	/*
	 * 把一个目录移动到另一个目录中去moveDirectoryToDirectory(File src, File
	 * destDir,booleancreateDestDir)[java]view plaincopyprint?
	 */

	public static void moveDirectoryToDirectory(File src, File destDir, boolean createDestDir) throws IOException {

		if (src == null) {

			throw new NullPointerException("Source must not be null");

		}

		if (destDir == null) {

			throw new NullPointerException("Destination directory must not be null");

		}

		if (!destDir.exists() && createDestDir) {

			destDir.mkdirs();

		}

		if (!destDir.exists()) {

			throw new FileNotFoundException("Destination directory '" + destDir +

			"' does not exist[createDestDir=" + createDestDir + "]");

		}

		if (!destDir.isDirectory()) {

			throw new IOException("Destination'" + destDir + "' is not a directory");

		}

		moveDirectory(src, new File(destDir, src.getName()));

	}

	/*
	 * 复制文件到对应的文件中去moveFile(File srcFile,File destFile)[java]view
	 * plaincopyprint?
	 */

	public static void moveFile(File srcFile, File destFile) throws IOException {

		if (srcFile == null) {

			throw new NullPointerException("Source must not be null");

		}

		if (destFile == null) {

			throw new NullPointerException("Destination must not be null");

		}

		if (!srcFile.exists()) {

			throw new FileNotFoundException("Source '" + srcFile + "' does notexist");

		}

		if (srcFile.isDirectory()) {

			throw new IOException("Source '" + srcFile + "' is a directory");

		}

		if (destFile.exists()) {

			throw new FileExistsException("Destination '" + destFile + "' alreadyexists");

		}

		if (destFile.isDirectory()) {

			throw new IOException("Destination '" + destFile + "' is adirectory");

		}

		boolean rename = srcFile.renameTo(destFile);

		if (!rename) {

			copyFile(srcFile, destFile);

			if (!srcFile.delete()) {

				FileUtils.deleteQuietly(destFile);

				throw new IOException("Failed to delete original file '" + srcFile +

				"' after copyto '" + destFile + "'");

			}

		}

	}

	/*
	 * 复制文件到对应的文件中去，可设置当目标文件不存在时是否创建新的文件moveFile(File srcFile, File destFile)
	 */

	public static void moveFileToDirectory(File srcFile, File destDir, boolean createDestDir) throws IOException {

		if (srcFile == null) {

			throw new NullPointerException("Source must not be null");

		}

		if (destDir == null) {

			throw new NullPointerException("Destinationdirectory must not be null");

		}

		if (!destDir.exists() && createDestDir) {

			destDir.mkdirs();

		}

		if (!destDir.exists()) {

			throw new FileNotFoundException("Destination directory '" + destDir +

			"' does not exist[createDestDir=" + createDestDir + "]");

		}

		if (!destDir.isDirectory()) {

			throw new IOException("Destination'" + destDir + "' is not a directory");

		}

		moveFile(srcFile, new File(destDir, srcFile.getName()));

	}

	/*
	 * 移动文件或者目录到新的路径下，并且设置在目标路径不存在的情况下是否创建moveToDirectory(File src, File
	 * destDir, boolean createDestDir)
	 */

	public static void moveToDirectory(File src, File destDir, boolean createDestDir) throws IOException {

		if (src == null) {

			throw new NullPointerException("Source must not be null");

		}

		if (destDir == null) {

			throw new NullPointerException("Destination must not be null");

		}

		if (!src.exists()) {

			throw new FileNotFoundException("Source '" + src + "' does notexist");

		}

		if (src.isDirectory()) {

			moveDirectoryToDirectory(src, destDir, createDestDir);

		} else {

			moveFileToDirectory(src, destDir, createDestDir);

		}

	}

	/*
	 * 确定指定的文件是否是一个符号链接，而不是实际的文件
	 */
	public static boolean isSymlink(File file) throws IOException {
		if (file == null) {

			throw new NullPointerException("File must not be null");

		}

		// if (FilenameUtils.isSystemWindows()) {
		// return false;
		// }
		// TODO

		File fileInCanonicalDir = null;

		if (file.getParent() == null) {

			fileInCanonicalDir = file;

		} else {

			File canonicalDir = file.getParentFile().getCanonicalFile();

			fileInCanonicalDir = new File(canonicalDir, file.getName());

		}

		if (fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile())) {

			return false;

		} else {

			return true;

		}

	}

}
