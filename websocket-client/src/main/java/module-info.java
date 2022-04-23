module websocket.client {
    requires org.slf4j;
    requires ch.qos.logback.classic;

    exports org.java_websocket.client;
    exports org.java_websocket.handshake;
}