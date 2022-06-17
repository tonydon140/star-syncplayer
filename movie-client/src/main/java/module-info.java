module top.tonydon {
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.controls;
    requires javafx.graphics;

    requires org.slf4j;
    requires websocket.client;
    requires ch.qos.logback.classic;
    requires com.fasterxml.jackson.databind;
    requires movie.common;

    opens top.tonydon to javafx.fxml, javafx.graphics;

    exports top.tonydon;
    exports top.tonydon.client;
    exports top.tonydon.util;
    exports top.tonydon.util.observer;
}