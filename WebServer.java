import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.System;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Properties;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * static fileを処理するだけのシンプルなWebサーバ.
 * 
 * @author shin1ogawa
 * @author vvakame
 */
public class WebServer {

	/**
	 * @param args
	 * @throws IOException
	 * @author shin1ogawa
	 * @author vvakame
	 */
	public static void main(String[] args) throws IOException {

		File propertiesFile = new File("./contenttypes.properties");
		if (propertiesFile.exists() == false) {
			System.err
					.println(propertiesFile.getAbsolutePath() + " not found.");
			System.exit(255);
		}
		final Properties properties = new Properties();
		properties.load(new FileInputStream(propertiesFile));

		final String port = properties.containsKey("port") ? properties
				.getProperty("port") : "8000";
		final String address = properties.containsKey("address") ? properties
				.getProperty("address") : null;
		final String webfolder = properties.containsKey("webfolder") ? properties
				.getProperty("webfolder") : ".";

		InetSocketAddress socketAddress;
		if (address != null) {
			socketAddress = new InetSocketAddress(address,
					Integer.parseInt(port));
		} else {
			socketAddress = new InetSocketAddress(Integer.parseInt(port));
		}
		HttpServer server = HttpServer.create(socketAddress, 0);

		server.createContext("/", new HttpHandler() {

			@Override
			public void handle(HttpExchange exchange) throws IOException {
				URI uri = exchange.getRequestURI();
				String filePath;
				try {
					if (uri.toString().endsWith("/")) {
						filePath = webfolder + uri.toString() + "index.html";
					} else if (uri.toString().startsWith("/")) {
						filePath = webfolder + uri.toString();
					} else {
						filePath = webfolder + "/" + uri.toString();
					}
					if (filePath.indexOf('?') > 0) {
						filePath = filePath.substring(0, filePath.indexOf('?'));
						System.out.println(filePath);
					}
					File file = new File(filePath);
					if (file.exists() == false) {
						System.err.println(exchange.getRequestMethod() + " "
								+ uri + " - 404");
						exchange.sendResponseHeaders(404, 0);
						return;
					}

					System.out.println(exchange.getRequestMethod() + " " + uri
							+ " - 200");
					String extension = filePath.substring(filePath
							.lastIndexOf("."));
					if (properties.containsKey(extension)) {
						exchange.getResponseHeaders().add("Content-Type",
								properties.getProperty(extension));
					} else {
						System.out
								.println("could not field content type definitions for: "
										+ extension);
					}
					exchange.getResponseHeaders().add("Content-Length",
							String.valueOf(file.length()));

					if (".js".equals(extension)) {
						exchange.getResponseHeaders().add("X-SourceMap",
								file.getName() + ".map");
					}

					exchange.sendResponseHeaders(200, file.length());
					OutputStream out = exchange.getResponseBody();
					FileInputStream in = new FileInputStream(file);
					byte[] buffer = new byte[10240];
					int read = -1;
					while ((read = in.read(buffer)) != -1) {
						out.write(buffer, 0, read);
					}
					in.close();
					out.flush();
					out.close();
				} finally {
					exchange.close();
				}
			}
		});

		server.start();
	}
}
