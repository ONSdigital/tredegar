package com.github.onsdigital.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.github.davidcarboni.restolino.framework.Endpoint;
import com.github.davidcarboni.restolino.json.Serialiser;
import com.github.onsdigital.configuration.Configuration;
import com.github.onsdigital.json.ContentType;
import com.github.onsdigital.json.Data;
import com.github.onsdigital.json.TaxonomyNode;

/**
 * Sitemap endpoint that reflects the taxonomy structure:
 * 
 * @author David Carboni
 *
 */
@Endpoint
public class Sitemap {

	static String encoding = "UTF8";
	static Document document;

	@GET
	public void get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {

		if (document == null) {

			// Get the request URI:
			URL requestUrl = new URL(request.getRequestURL().toString());

			// Create a sitemap structure:
			Document document = createDocument();
			Element rootElement = createRootElement(document);

			// Iterate the taxonomy structure:
			Path taxonomyPath = getHomePath();
			// System.out.println("Searching " + taxonomyPath);
			int total = 0;
			total += addPath(taxonomyPath, document, rootElement, 1, requestUrl);
			total += iterate(taxonomyPath, 0.8, document, rootElement, requestUrl);

			// Output the result:
			System.out.println("Found " + total + " URLs for the sitemap.");
			Sitemap.document = document;
		}

		writeResponse(document, response);
	}

	private Path getHomePath() throws IOException {

		return FileSystems.getDefault().getPath(Configuration.getTaxonomyPath());
	}

	private Document createDocument() throws IOException {

		try {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			document.setXmlStandalone(true);
			return document;
		} catch (ParserConfigurationException e) {
			throw new IOException("Error setting up XML DocumentBuilder", e);
		}
	}

	/**
	 * Creates the correct root element in the Document.
	 * 
	 * @param document
	 * @return
	 */
	private Element createRootElement(Document document) {
		Element rootElement = document.createElementNS("http://www.sitemaps.org/schemas/sitemap/0.9", "urlset");
		document.appendChild(rootElement);
		return rootElement;
	}

	private int iterate(Path taxonomyPath, double priority, Document document, Element rootElement, URL requestUrl) throws IOException {
		int result = 0;

		List<Path> subdirectories = new ArrayList<Path>();

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(taxonomyPath)) {

			for (Path path : stream) {

				// Iterate over the paths:
				if (Files.isDirectory(path)) {
					result += addPath(path, document, rootElement, priority, requestUrl);
					subdirectories.add(path);
				}
			}

		} catch (DOMException | MalformedURLException e) {
			throw new IOException("Error iterating taxonomy", e);
		}

		// Step into subfolders:
		for (Path subdirectory : subdirectories) {
			result += iterate(subdirectory, priority * .8, document, rootElement, requestUrl);
		}

		return result;
	}

	private int addPath(Path path, Document document, Element rootElement, double priority, URL requestUrl) throws IOException {
		int result = 0;
		Data data = getDataJson(path);
		if (data != null && !StringUtils.equals(data.type, ContentType.TIMESERIES.name())) {
			try {
				URI uri = toUri(data, requestUrl);
				addUrl(uri, document, rootElement, priority);
				result++;
				// System.out.println(path + " : " + uri + " (" + priority +
				// ")");
			} catch (URISyntaxException | DOMException | MalformedURLException e) {
				throw new IOException("Error iterating taxonomy", e);
			}
		} else if (data == null) {
			System.out.println("Skipping non-data folder " + path);
		} 
		// else if (StringUtils.equals(data.type,
		// ContentType.TIMESERIES.name())) {
		// System.out.println("Skipping timeseries " + path);
		// } else {
		// System.out.println("Skipping for some reason: " + path);
		// }
		return result;
	}

	private Data getDataJson(Path path) throws IOException {
		Data result = null;

		Path dataJson = path.resolve("data.json");
		if (Files.exists(dataJson)) {
			try (InputStream input = Files.newInputStream(dataJson)) {
				result = Serialiser.deserialise(input, Data.class);
			}
		}

		return result;
	}

	private URI toUri(Data data, URL requestUrl) throws URISyntaxException {
		StringBuilder fragment = new StringBuilder("!/home");
		if (data != null && data.breadcrumb != null) {
			for (TaxonomyNode segment : data.breadcrumb) {
				fragment.append("/" + segment.fileName);
			}
		}
		if (!StringUtils.equals("/", data.fileName)) {
			fragment.append("/" + data.fileName);
		}
		new URI(requestUrl.getProtocol(), requestUrl.getHost(), "/", fragment.toString());
		int port = -1;
		if (requestUrl.getPort() == 8080) {
			port = 8080;
		}
		String userInfo = null;
		String query = null;
		URI uri = new URI(requestUrl.getProtocol(), userInfo, requestUrl.getHost(), port, "/", query, fragment.toString());
		return uri;
	}

	private void addUrl(URI uri, Document document, Element rootElement, double priorityValue) throws DOMException, MalformedURLException {

		// Container
		Element url = document.createElement("url");
		rootElement.appendChild(url);

		// Location
		Element loc = document.createElement("loc");
		url.appendChild(loc);
		loc.setTextContent(uri.toURL().toExternalForm());

		// Priority
		Element priority = document.createElement("priority");
		url.appendChild(priority);
		priority.setTextContent(String.format("%.2f", priorityValue));

		System.out.println(uri.toURL().toExternalForm());
	}

	private void writeResponse(Document document, HttpServletResponse response) throws IOException {

		// Setting the character encoding here ensures the PrintWriter
		// uses the correct encoding:
		response.setCharacterEncoding(encoding);
		PrintWriter writer = response.getWriter();

		try {

			DOMSource domSource = new DOMSource(document);
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();

			// Just to be sure, set the value in <?xml ... encoding="..."?>
			// to match the response character encoding:
			transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
			transformer.transform(domSource, result);
			writer.flush();

		} catch (TransformerException e) {
			throw new IOException("Error transforming XML", e);
		}
	}
}
