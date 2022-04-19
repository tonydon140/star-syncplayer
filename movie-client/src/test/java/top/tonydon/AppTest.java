package top.tonydon;

import javafx.application.Application;
import javafx.scene.control.ButtonBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class AppTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        ButtonBar buttonBar = new ButtonBar();
        ImageView imageView = new ImageView();
        Image image = new Image("@icon/喇叭.svg");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
