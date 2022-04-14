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
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Parent parent = fxmlLoader.load();

        Scene scene = new Scene(parent, 320, 240);
        stage.setWidth(1200);
        stage.setHeight(650);
        stage.setTitle("星星电影院");
        stage.setScene(scene);

        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        super.stop();

//        System.out.println("stop");
//        Platform.exit();
    }
}