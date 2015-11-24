import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import com.google.api.client.util.Strings;
import com.opencsv.CSVWriter;

public class GoEuroTest {
	

	public static class GeoPosition {
		@Key
		public Double latitude;
		@Key
		public Double longitude;
	}

	/** Represents a suggestion */
	public static class Suggestion {
		@Key
		private Integer _id;
		@Key
		private String key;
		@Key
		private String name;
		@Key
		private String fullName;
		@Key
		private String iata_airport_code;
		@Key
		private String location;
		@Key
		private String country;
		@Key
		private GeoPosition geo_position;
		@Key
		private String type;
		@Key
		private Integer locationId;
		@Key
		private boolean inEurope;
		@Key
		private String countryCode;
		@Key
		private boolean coreCountry;
		@Key
		private Double distance;
		@Key
		private String title;

		// @Key
		// public String url;
	}

	/** URL for API. */
	public static class Url extends GenericUrl {

		public Url(String encodedUrl) {
			super(encodedUrl);
		}

		@Key
		public String fields;
	}

	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final JsonFactory JSON_FACTORY = new JacksonFactory();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		if (args == null || args.length ==0 || Strings.isNullOrEmpty(args[0])) {
			System.out.println("Please enter a city name.");
			System.exit(0);
		}
		HttpRequestFactory requestFactory = HTTP_TRANSPORT
				.createRequestFactory(new HttpRequestInitializer() {
					@Override
					public void initialize(HttpRequest request) {
						request.setParser(new JsonObjectParser(JSON_FACTORY));
					}
				});
		Url url = new Url("http://api.goeuro.com/api/v2/position/suggest/en/"
				+ args[0]);
		HttpRequest request = requestFactory.buildGetRequest(url);
		Suggestion[] suggestions = request.execute().parseAs(
				(Suggestion[].class));
		writeCSVData(suggestions);
	}

	private static void writeCSVData(Suggestion[] suggestions)
			throws IOException {
		FileWriter writer = new FileWriter("GoEurope.csv");
		
		CSVWriter csvWriter = new CSVWriter(writer, ',');
		List<String[]> data = toStringArray(suggestions);
		csvWriter.writeAll(data);
		csvWriter.close();

	}

	private static List<String[]> toStringArray(Suggestion[] suggestions) {
		List<String[]> records = new ArrayList<String[]>();
		// add header record
		records.add(new String[] { "_id", "name", "type", "latitude",
				"longitude" });
		for (Suggestion sugg : suggestions) {
			records.add(new String[] {
					sugg._id != null ? sugg._id.toString() : "null",
					sugg.name != null ? sugg.name.toString() : "null",
					sugg.type != null ? sugg.type.toString() : "null",
					sugg.geo_position.latitude != null ? sugg.geo_position.latitude
							.toString() : "null",
					sugg.geo_position.longitude != null ? sugg.geo_position.longitude
							.toString() : "null" });
		}

		return records;
	}

}
