package app.TradingAppCore;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class IndicatorsService {
    private String interval, symbol, period;
    private String secret = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbHVlIjoiNjU2ZWRkMmUxNDBjZmQ3MjNkYTM1MjcwIiwiaWF0IjoxNzAxNzY0NTIwLCJleHAiOjMzMjA2MjI4NTIwfQ.V7EKcplwelDHKb3kYG3WCEFlTOpco8qVuJUag5k4INM";

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int reqRSI() {
        int rsi = 0;
        URI taApi = URI.create("https://api.taapi.io/rsi?secret=" + secret + "&exchange=binance&symbol=" + symbol + "&interval=" + interval + "&period=" + period);

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(taApi).GET().build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String value = KeyResponseParser.parseInfo(response, "value");
            //System.out.println(response.body());
            rsi = (int) Double.parseDouble(value);
        } catch (Exception e) {
            rsi = 50;
        }
        return rsi;
    }
}