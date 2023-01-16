package top.tonydon.syncplayer;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import top.tonydon.syncplayer.constant.ClientConstants;

import java.util.Objects;

public class RoomApplication extends Application {

    RoomWindow roomWindow;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 创建窗口
        roomWindow = new RoomWindow(primaryStage);

        // 加载窗口
        Parent parent = roomWindow.load();
        Scene scene = new Scene(parent);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("icon/star_128.png")).toString()));
        primaryStage.setTitle(ClientConstants.TITLE);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(ClientConstants.CLIENT_MIN_WIDTH);
        primaryStage.setMinHeight(ClientConstants.CLIENT_MIN_HEIGHT);
        primaryStage.setWidth(ClientConstants.CLIENT_DEFAULT_WIDTH);
        primaryStage.setHeight(ClientConstants.CLIENT_DEFAULT_HEIGHT);
        primaryStage.show();

//        mainWindow.init(this);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
