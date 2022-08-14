package top.tonydon;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import top.tonydon.constant.ClientConstants;

import java.io.IOException;
import java.util.Objects;


public class ClientApplication extends Application {

    private ClientController controller;

    @Override
    public void start(Stage stage) throws IOException {
        // 1. 加载 FXML
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("movie-client.fxml"));
        Parent parent = fxmlLoader.load();

        // 2. 加载窗口
        Scene scene = new Scene(parent);
        stage.setWidth(1160);
        stage.setHeight(670);
        stage.setResizable(false);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("icon/星星128.png")).toString()));
        stage.setTitle(ClientConstants.TITLE);
        stage.setScene(scene);

        // 3. 窗口加载完毕后初始化数据
        controller = fxmlLoader.getController();
        controller.init(this);

        // 4. 显示窗口
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        // 关闭客户端
        controller.close();
    }
}