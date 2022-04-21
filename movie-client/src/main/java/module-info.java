module top.tonydon {
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.controls;
    requires javafx.graphics;

    requires org.slf4j;
    requires websocket.client;
    requires ch.qos.logback.classic;
    requires com.fasterxml.jackson.databind;

    opens top.tonydon to javafx.fxml, javafx.graphics;

    exports top.tonydon;
    exports top.tonydon.message;
    exports top.tonydon.message.client;
    exports top.tonydon.message.server;
}