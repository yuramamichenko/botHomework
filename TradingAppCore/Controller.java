package app.TradingAppCore;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Controller {
    private User user;
    private String symbol, side;
    private int quantity;

    public void createMarketOrder() {
        try {
            RequestData data = new RequestData();
            data.addValues("symbol", symbol);
            data.addValues("side", side);
            data.addValues("orderQty", String.valueOf(quantity));

            long expires = Signature.createExpires();
            String signPost = Signature.generateSignature(user.getSecret(), "POST", "/api/v1/order", expires, String.valueOf(data));
            HttpRequest requestToBuy = HttpRequest.newBuilder()
                    .uri(URI.create(user.getBaseUrl() + "/api/v1/order"))
                    .header("Content-Type", "application/json")
                    .header("api-key", user.getKey())
                    .header("api-expires", String.valueOf(expires))
                    .header("api-signature", signPost)
                    .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(data)))
                    .build();
            HttpResponse<String> response = user.getClient().send(requestToBuy, HttpResponse.BodyHandlers.ofString());
            System.out.println("Open price = " + KeyResponseParser.parseInfo(response, "price"));
            //System.out.println(response.body());
        } catch (Exception e) {
        }
    }

    public void closePositionByTicker() {
        try {
            RequestData data = new RequestData();
            data.addValues("symbol", symbol);
            data.addValues("execInst", "Close");

            long expires = Signature.createExpires();
            String signature = Signature.generateSignature(user.getSecret(), "POST", "/api/v1/order", expires, String.valueOf(data));
            HttpRequest requestToClose = HttpRequest.newBuilder()
                    .uri(URI.create(user.getBaseUrl() + "/api/v1/order"))
                    .header("Content-Type", "application/json")
                    .header("api-key", user.getKey())
                    .header("api-expires", String.valueOf(expires))
                    .header("api-signature", signature)
                    .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(data)))
                    .build();
            HttpResponse<String> response = user.getClient().send(requestToClose, HttpResponse.BodyHandlers.ofString());
            System.out.println("Close price = " + KeyResponseParser.parseInfo(response, "price"));
            //System.out.println(response.body());
        } catch (Exception e) {
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSide() {
        return side;
    }
}