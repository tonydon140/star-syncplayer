module top.tonydon {
    requires javafx.media;
    requires javafx.controls;
    requires javafx.graphics;
    requires java.net.http;

    requires movie.common;
    requires org.slf4j;
    requires websocket.client;
    requires ch.qos.logback.classic;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens top.tonydon.syncplayer to javafx.graphics;

    exports top.tonydon.syncplayer.entity to com.fasterxml.jackson.databind;
    exports top.tonydon.syncplayer;
    exports top.tonydon.syncplayer.util;
    exports top.tonydon.syncplayer.util.observer;
}