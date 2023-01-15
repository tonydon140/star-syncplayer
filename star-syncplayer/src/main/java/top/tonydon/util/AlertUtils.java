package top.tonydon.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Window;

/**
 * 提示框工具类
 */
public class AlertUtils {

    public static void confirmation(String head, String context, Window window) {
        alter(head, context, window, Alert.AlertType.CONFIRMATION);
    }

    public static void none(String head, String context, Window window) {
        alter(head, context, window, Alert.AlertType.NONE);
    }

    public static void error(String head, String context, Window window) {
        alter(head, context, window, Alert.AlertType.ERROR);
    }

    public static void information(String head, String context, Window window) {
        alter(head, context, window, Alert.AlertType.INFORMATION);
    }

    public static void warning(String head, String context, Window window) {
        alter(head, context, window, Alert.AlertType.WARNING);
    }

    private static void alter(String head, String context, Window window, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setHeaderText(head);
            alert.setContentText(context);
            alert.initModality(Modality.WINDOW_MODAL);
            alert.initOwner(window);
            alert.show();
        });
    }
}
