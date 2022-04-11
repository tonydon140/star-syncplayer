package top.tonydon;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import top.tonydon.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;

public class HelloController {
    @FXML
    public VBox vBox;

    @FXML
    public Button button;

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
        try {
            WebClient client = new WebClient(new URI("ws://localhost:8080/websocket"));
            boolean flag = client.connectBlocking();
            if (flag) {
                welcomeText.setText("连接成功");
            }else{
                welcomeText.setText("fail...");
            }
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        System.out.println("加载完毕！");
    }

}