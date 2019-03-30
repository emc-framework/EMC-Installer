package me.deftware.emc.installer.utils;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class WebUtils {

	public static void download(String uri, String fileName) throws Exception {
		URL url = new URL(uri);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
		connection.setRequestMethod("GET");
		FileOutputStream out = new FileOutputStream(fileName);
		InputStream in = connection.getInputStream();
		int read;
		byte[] buffer = new byte[4096];
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
		in.close();
		out.close();
	}

	public static String get(String url) throws Exception {
		URL url1 = new URL(url);
		Object connection = (url.startsWith("https://") ? (HttpsURLConnection) url1.openConnection()
				: (HttpURLConnection) url1.openConnection());
		((URLConnection) connection).setConnectTimeout(8 * 1000);
		((URLConnection) connection).setRequestProperty("User-Agent", "EMC Installer");
		((HttpURLConnection) connection).setRequestMethod("GET");
		BufferedReader in = new BufferedReader(new InputStreamReader(((URLConnection) connection).getInputStream()));
		String result = "", text;
		while ((text = in.readLine()) != null) {
			result += text;
		}
		in.close();
		return result;
	}

}
