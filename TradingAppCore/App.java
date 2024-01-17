package app.TradingAppCore;

public class App {

    public static void main(String[] args) {
        User user = new User();
        Controller controller = new Controller();
        PositionInfo info = new PositionInfo();
        controller.setUser(user);
        info.setUser(user);


        user.setKey("3Mwsofre7yG7aOc1NHB8iLHl");
        user.setSecret("Wy_ru4DAPSfxwVjIS3cPiT9Va9wPEDFv2-EtvxWNlA2MjRym");
        info.setSymbol("XBTUSD");
        controller.setSymbol("XBTUSD");
        controller.setSide("Sell");
        controller.setQuantity(100);


        //controller.createMarketOrder();
        //System.out.println("com = " + info.getPosCommByTicker());
        //System.out.println("pnl = " + info.getCurrentPnlByTicker());
        controller.closePositionByTicker();
    }
}
