module syncplayer.client {
    requires javafx.controls;
    requires javafx.graphics;
    requires java.net.http;

    requires syncplayer.common;
    requires org.slf4j;
    requires org.java_websocket;
    requires ch.qos.logback.classic;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires kotlin.stdlib;
    requires uk.co.caprica.vlcj;
    requires com.sun.jna.platform;

    opens top.tonydon.syncplayer to javafx.graphics;

    exports top.tonydon.syncplayer.entity to com.fasterxml.jackson.databind;
    exports top.tonydon.syncplayer;
    exports top.tonydon.syncplayer.util;
    exports top.tonydon.syncplayer.util.observer;
}