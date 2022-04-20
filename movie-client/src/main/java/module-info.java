module top.tonydon {
    requires java.desktop;

    requires javafx.controls;
    requires javafx.media;
    requires javafx.fxml;
    requires javafx.graphics;

    requires com.fasterxml.jackson.databind;
    requires websocket.client;
    requires java.logging;

    opens top.tonydon to javafx.fxml, javafx.graphics;

    exports top.tonydon;
    exports top.tonydon.message;
    exports top.tonydon.message.client;
    exports top.tonydon.message.server;
}