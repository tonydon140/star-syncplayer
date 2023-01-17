package top.tonydon.syncplayer;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import top.tonydon.syncplayer.constant.ClientConstants;
import top.tonydon.syncplayer.constant.RoomConstants;

import java.util.Objects;

public class RoomApplication extends Application {

    RoomWindow roomWindow;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 创建窗口
        roomWindow = new RoomWindow(primaryStage);

        // 加载窗口
        Scene scene = new Scene(roomWindow.getRoot());
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("icon/star_128.png")).toString()));
        primaryStage.setTitle(RoomConstants.TITLE);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(ClientConstants.CLIENT_MIN_WIDTH);
        primaryStage.setMinHeight(ClientConstants.CLIENT_MIN_HEIGHT);
        primaryStage.setWidth(ClientConstants.CLIENT_DEFAULT_WIDTH);
        primaryStage.setHeight(ClientConstants.CLIENT_DEFAULT_HEIGHT);
        primaryStage.show();

        roomWindow.init(this);
    }

    @Override
    public void stop() throws Exception {
        roomWindow.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}