module top.tonydon {
    requires java.desktop;

    requires Java.WebSocket;
    requires org.slf4j;
//    requires org.slf4j.simple;


    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires lombok;
    requires movie.framework;
    requires fastjson;

    opens top.tonydon to javafx.fxml;
    exports top.tonydon;
}