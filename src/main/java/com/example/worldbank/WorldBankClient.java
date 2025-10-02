package com.example.worldbank;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/** Fetches GDP data from the World Bank API */
public class WorldBankClient {
    private static final String BASE =
            "https://api.worldbank.org/v2/countries/%s/indicators/%s?per_page=5000&format=json";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<GdpRecord> fetchGdp(String iso3Country, String indicator) throws IOException, InterruptedException {
        String url = String.format(BASE, iso3Country, indicator);
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .GET().build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) {
            throw new IOException("HTTP " + res.statusCode() + " from World Bank: " + res.body());
        }
        return parse(res.body());
    }

    /** The API returns a two-element JSON array: [metadata, data[]] */
    private List<GdpRecord> parse(String json) throws IOException {
        JsonNode root = mapper.readTree(json);
        if (!root.isArray() || root.size() < 2) {
            throw new IOException("Unexpected JSON shape");
        }
        JsonNode data = root.get(1);
        List<GdpRecord> out = new ArrayList<>();
        if (data != null && data.isArray()) {
            for (JsonNode row : data) {
                String year = row.path("date").asText(null);
                JsonNode valNode = row.get("value");
                Double value = (valNode == null || valNode.isNull()) ? null : valNode.asDouble();
                if (year != null) {
                    out.add(new GdpRecord(year, value));
                }
            }
        }
        return out;
    }
}
