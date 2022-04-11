module top.tonydon {
    requires java.desktop;

    requires Java.WebSocket;
    requires slf4j.api;
    requires org.slf4j.simple;

    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires lombok;
    requires fastjson;


    opens top.tonydon to javafx.fxml;
    exports top.tonydon;
}