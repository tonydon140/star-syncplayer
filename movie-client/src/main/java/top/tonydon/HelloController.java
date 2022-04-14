package top.tonydon;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import top.tonydon.client.WebClient;
import top.tonydon.message.client.ClientBindMessage;
import top.tonydon.message.client.ClientUnbindMessage;
import top.tonydon.message.server.ServerConnectMessage;
import top.tonydon.message.client.ClientMovieMessage;
import top.tonydon.message.server.ServerBindMessage;
import top.tonydon.message.server.ServerOfflineMessage;
import top.tonydon.message.server.ServerUnbindMessage;
import top.tonydon.util.ClientObserver;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class HelloController {

    @FXML
    public Label selfNumberLabel;
    @FXML
    public Label targetNumberLabel;
    public AnchorPane root;
    public Button bindButton;
    public ButtonBar togetherButtonBar;
    public MediaView mediaView;


    private WebClient client;
    private Stage primaryStage;


    @FXML
    private void initialize() {
        // 2. 创建 websocket 客户端
        try {
            client = new WebClient(new URI("ws://localhost:8080/websocket"));
            boolean flag = client.connectBlocking();
            if (!flag) {
                selfNumberLabel.setText("连接服务器失败...");
                return;
            }

            // 添加观察者
            client.addObserver(new ClientObserver() {
                @Override
                public void onConnected(ServerConnectMessage message) {
                    Platform.runLater(() -> selfNumberLabel.setText(message.getNumber()));
                }

                @Override
                public void onMovie(ClientMovieMessage message) {

                }

                @Override
                public void onBind(ServerBindMessage message) {
                    // 将客户端设置为已绑定，并更新 ui
                    client.isBind = true;
                    Platform.runLater(() -> {
                        togetherButtonBar.setDisable(false);
                        targetNumberLabel.setText(message.getTargetNumber());
                        bindButton.setText("解除绑定");
                    });
                }

                @Override
                public void onUnBind(ServerUnbindMessage message) {
                    // 解除绑定
                    client.isBind = false;
                    Platform.runLater(() -> {
                        togetherButtonBar.setDisable(true); // 禁用一起播放按钮组
                        targetNumberLabel.setText("");      // 清空另一半的星星号
                        bindButton.setText("绑定他/她");     // 重置绑定按钮名称
                    });
                }

                @Override
                public void onOffline(ServerOfflineMessage message) {
                    onUnBind(new ServerUnbindMessage());
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("信息");
                        alert.setHeaderText("另一半断开连接");
                        alert.initModality(Modality.WINDOW_MODAL);
                        alert.initOwner(primaryStage);

                        // 4. 显示窗口
                        Optional<ButtonType> result = alert.showAndWait();
                    });
                }
            });

        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void bindButtonAction(ActionEvent actionEvent) {
        if (client.isBind) unbindNumber(actionEvent);
        else bindNumber(actionEvent);
    }

    /**
     * 解除绑定
     *
     * @param actionEvent 点击事件
     */
    public void unbindNumber(ActionEvent actionEvent) {
        // 1. 创建确认信息
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认消息");
        alert.setHeaderText("确定要解绑吗");

        // 2. 设置主窗口
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner(primaryStage);

        // 4. 显示窗口
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ClientUnbindMessage message = new ClientUnbindMessage(client.getSelfNumber());
            client.send(message.toJson());
        }
    }


    /**
     * 绑定另一个人
     *
     * @param actionEvent 点击事件
     */
    public void bindNumber(ActionEvent actionEvent) {
        // 1. 设置主窗口
        if (primaryStage == null) primaryStage = (Stage) root.getScene().getWindow();

        // 2. 创建对话框
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("绑定他/她");
        inputDialog.setHeaderText("绑定他/她");
        inputDialog.setContentText("请输入他/她的星星号：");

        // 3. 设置主窗口
        inputDialog.initModality(Modality.WINDOW_MODAL);
        inputDialog.initOwner(primaryStage);

        // 4. 显示窗口
        Optional<String> result = inputDialog.showAndWait();

        // 5. 点击确定后发送消息
        if (result.isPresent()) {
            ClientBindMessage clientBindMessage = new ClientBindMessage(client.getSelfNumber(), result.get());
            client.send(clientBindMessage.toJson());
        }
    }


    @FXML
    public void selectVideo(ActionEvent actionEvent) {
        Media media = new Media("file:/C:/Users/tangjian/Desktop/华为变奏曲.mp4");
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
    }
}