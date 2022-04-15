package top.tonydon;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // 1. 加载 FXML
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Parent parent = fxmlLoader.load();

        // 2. 加载窗口
        Scene scene = new Scene(parent, 320, 240);
        stage.setWidth(1200);
        stage.setHeight(650);
        stage.setTitle("星星电影院");
        stage.setScene(scene);

        // 3. 窗口加载完毕后初始化数据
        HelloController controller = fxmlLoader.getController();
        controller.initData();

        // 4. 显示窗口
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}