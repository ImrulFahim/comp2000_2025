import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class Client {

    // Start listening to the weather feed and send each parsed event to onEvent
    public static void startWeatherStream(Consumer<WeatherEvent> onEvent) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://13.238.167.130/weather")) // IMPORTANT
                .header("Accept", "text/event-stream")
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(HttpResponse::body)
                .thenAccept(inputStream -> {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.isBlank()) continue;

                            WeatherEvent event = parseLine(line);
                            if (event != null) {
                                System.out.println("Weather: " + event); // debug
                                onEvent.accept(event); // send into Stage
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Error reading SSE stream: " + e.getMessage());
                    }
                });
    }

    // Parse: timestamp attribute x y value
    private static WeatherEvent parseLine(String line) {
        try {
            String[] parts = line.trim().split("\\s+");
            if (parts.length != 5) return null;

            long ts = Long.parseLong(parts[0]);
            String attr = parts[1].toLowerCase(); // rain, windx, windy, temp
            int x = Integer.parseInt(parts[2]);
            int y = Integer.parseInt(parts[3]);
            double value = Double.parseDouble(parts[4]);

            return new WeatherEvent(ts, attr, x, y, value);
        } catch (Exception e) {
            System.err.println("Bad weather line: " + line + " (" + e.getMessage() + ")");
            return null;
        }
    }

    // Standalone tester (optional)
    public static void main(String[] args) {
        startWeatherStream(System.out::println);
    }
}
    
