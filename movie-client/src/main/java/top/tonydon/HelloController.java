package top.tonydon;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import top.tonydon.client.WebClient;
import top.tonydon.message.client.BindMessage;
import top.tonydon.message.server.ConnectMessage;
import top.tonydon.message.client.MovieMessage;
import top.tonydon.message.server.ServerBindMessage;
import top.tonydon.util.ClientObserver;

import java.net.URI;
import java.net.URISyntaxException;

public class HelloController {
    @FXML
    public VBox vBox;
    @FXML
    public Button button;
    @FXML
    public TextField targetCode;
    public Label targetNumber;
    public Label selfNumber;
    @FXML
    private Label welcomeText;

    private WebClient client;


    @FXML
    private void initialize() {
        System.out.println("加载完毕！");
        try {
            client = new WebClient(new URI("ws://localhost:8080/websocket"));
            boolean flag = client.connectBlocking();
            if (!flag) {
                welcomeText.setText("连接服务器失败...");
                return;
            }

            // 添加观察者
            client.addObserver(new ClientObserver() {
                @Override
                public void onConnected(ConnectMessage message) {
                    Platform.runLater(() -> selfNumber.setText(message.getNumber()));
                }

                @Override
                public void onMovie(MovieMessage message) {

                }

                @Override
                public void onBind(ServerBindMessage message) {
                    Platform.runLater(() -> {
                        targetNumber.setText(message.getTargetNumber());
                    });
                }
            });

        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void sendMessage(ActionEvent actionEvent) {
        BindMessage bindMessage = new BindMessage(client.getNumber(), targetCode.getText());

        client.send(bindMessage.toJson());
    }
}