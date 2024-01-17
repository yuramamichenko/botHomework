package app.FXparts;

import app.TradingAppCore.Controller;
import app.TradingAppCore.IndicatorsService;
import app.TradingAppCore.PositionInfo;
import app.TradingAppCore.User;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class JavaFXController implements Initializable {
    private IndicatorsService indicator;
    private User user;
    private Controller controller;
    private PositionInfo info;

    private Timeline timeline;
    private Timeline progressTime;
    @FXML
    private PasswordField apiKey;

    @FXML
    private PasswordField apiSecret;

    @FXML
    private TextField openLevel;

    @FXML
    private TextField closeLevel;

    @FXML
    private TextField quantity;
    @FXML
    private ProgressBar timeoutBar;
    private double timeoutProgress;
    private double timeoutChange;
    private Timeline timeoutTimer;

    @FXML
    private ProgressBar progressBar;
    private double progress;
    private int positionOpen;
    private int positionClose;

    private int minutesTimeout;
    private boolean notPaused = true;
    private Timeline pauseOpenings;


    @FXML
    private Label counter;

    @FXML
    private ChoiceBox<String> indTF = new ChoiceBox<>();
    @FXML
    private ChoiceBox<String> indTickers = new ChoiceBox<>();
    @FXML
    private ChoiceBox<String> indPeriodBox = new ChoiceBox<>();

    @FXML
    private ChoiceBox<String> tradingTickers = new ChoiceBox<>();
    @FXML
    private ChoiceBox<String> tradingSide = new ChoiceBox<>();
    @FXML
    private ChoiceBox<String> explanation = new ChoiceBox<>();

    @FXML
    private ChoiceBox<String> orderTimeout = new ChoiceBox<>();


    private String minOrdSize[] = {"XBT = div by 100"};

    private String[] bitmexTickers = {"XBTUSD"};
    private String[] orderSide = {"Buy", "Sell"};
    private String[] indicatorTimeframe = {"1m", "5m", "15m", "30m", "1h", "4h"};
    private String[] indicatorTickers = {"BTC/USDT", "ETH/USDT", "XRP/USDT", "LTC/USDT", "XMR/USDT"};
    private String[] indicatorPeriods = {"3", "4", "5", "6", "7", "8",
            "9", "10", "11", "12", "13", "14", "15"};
    private String[] orderTimeoutArray = {"1", "5", "10", "15", "30", "60"};

    private String indTFParam;
    private String indTickParam;
    private String period;

    public void onStartButton(ActionEvent e) {
        timeoutChange = 100.0 / (minutesTimeout * 60 / 20.0) / 100;
        timeoutTimer = new Timeline(new KeyFrame(Duration.seconds(20), actionEvent -> {
            timeoutBar.setProgress(timeoutProgress += timeoutChange);
        }));
        timeoutTimer.setCycleCount(Animation.INDEFINITE);

        final int[] indValue = new int[1];
        indicator.setPeriod(period);
        indicator.setSymbol(indTickParam);
        indicator.setInterval(indTFParam);
        counter.setText("started");

        progressBar.setProgress(progress += 0.05);
        progressTime = new Timeline(new KeyFrame(Duration.seconds(1), actionEvent -> {
            progressBar.setProgress(progress += 0.05);
        }));

        progressTime.setCycleCount(Animation.INDEFINITE);
        progressTime.play();
        String side = controller.getSide();

        pauseOpenings = new Timeline(new KeyFrame(Duration.minutes(minutesTimeout), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                notPaused = true;
                System.out.println("Timer stopped");
                pauseOpenings.stop();
            }
        }));
        pauseOpenings.setCycleCount(Animation.INDEFINITE);


        timeline = new Timeline(new KeyFrame(Duration.seconds(20), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                indValue[0] = indicator.reqRSI();
                counter.setText("rsi = " + indValue[0]);
                progressBar.setProgress(progress = 0.05);

                if (notPaused) {
                    timeoutBar.setProgress(timeoutProgress = 0);
                    timeoutTimer.stop();
                }

                if (side.equals("Buy")) {
                    if (indValue[0] <= positionOpen) {
                        if (notPaused) {
                            manualOpen();
                            notPaused = false;
                            pauseOpenings.play();
                            timeoutTimer.play();
                            System.out.println("Timer started");

                        }
                    } else if (indValue[0] >= positionClose) {
                        if (!info.getPosCommByTicker().equals("0")) {
                            manualClose();
                        }
                    }
                } else if (side.equals("Sell")) {
                    if (indValue[0] >= positionOpen) {
                        if (notPaused) {
                            manualOpen();
                            notPaused = false;
                            pauseOpenings.play();
                            timeoutTimer.play();
                            System.out.println("Timer started");

                        }
                    } else if (indValue[0] <= positionClose) {
                        if (!info.getPosCommByTicker().equals("0")) {
                            manualClose();
                        }
                    }
                }


            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void onStopButton(ActionEvent e) {
        timeline.stop();
        progressTime.stop();
        progressTime.stop();
        timeoutTimer.stop();
        counter.setText("stopped");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        indicator = new IndicatorsService();
        user = new User();
        controller = new Controller();
        info = new PositionInfo();
        controller.setUser(user);
        info.setUser(user);

        counter.setText("go?");
        indTickers.getItems().addAll(indicatorTickers);
        indTickers.setOnAction(this::selectIndTicker);

        indTF.getItems().addAll(indicatorTimeframe);
        indTF.setOnAction(this::selectIndTF);

        indPeriodBox.getItems().addAll(indicatorPeriods);
        indPeriodBox.setOnAction(this::savePeriod);

        tradingTickers.getItems().addAll(bitmexTickers);
        tradingTickers.setOnAction(this::saveOrderTicker);

        tradingSide.getItems().addAll(orderSide);
        tradingSide.setOnAction(this::saveOrderSide);

        orderTimeout.getItems().addAll(orderTimeoutArray);
        orderTimeout.setOnAction(this::saveTimeout);


        explanation.getItems().addAll(minOrdSize);
    }

    public void saveTimeout(ActionEvent e) {
        minutesTimeout = Integer.parseInt(orderTimeout.getValue());
    }

    public void saveOrderSide(ActionEvent e) {
        String value = tradingSide.getValue();
        controller.setSide(value);
    }

    public void saveOrderTicker(ActionEvent e) {
        String value = tradingTickers.getValue();
        info.setSymbol(value);
        controller.setSymbol(value);
    }

    public void selectIndTF(ActionEvent e) {
        indTFParam = indTF.getValue();
    }

    public void selectIndTicker(ActionEvent e) {
        indTickParam = indTickers.getValue();
    }

    public void savePeriod(ActionEvent e) {
        period = indPeriodBox.getValue();
    }

    public void initializeValues() {
        user.setKey(apiKey.getText());
        user.setSecret(apiSecret.getText());
        positionOpen = Integer.parseInt(openLevel.getText());
        positionClose = Integer.parseInt(closeLevel.getText());
        controller.setQuantity(Integer.parseInt(quantity.getText()));

    }

    public void manualOpen() {
        controller.createMarketOrder();
    }

    public void manualClose() {
        controller.closePositionByTicker();
    }
}
