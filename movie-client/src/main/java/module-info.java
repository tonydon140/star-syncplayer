module top.tonydon {
    requires java.desktop;

    requires javafx.controls;
    requires javafx.media;
    requires javafx.fxml;
    requires javafx.graphics;

    requires Java.WebSocket;
    requires org.slf4j;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires lombok;
    requires movie.framework;
    requires fastjson;

    opens top.tonydon to javafx.fxml, javafx.graphics;
    exports top.tonydon;
}