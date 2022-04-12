module movie.framework {
    requires lombok;
    requires hutool.all;
    requires fastjson;
    requires java.desktop;

    opens top.tonydon.message to fastjson;
    exports top.tonydon.message;
    exports top.tonydon.util;
    exports top.tonydon.message.client;
    opens top.tonydon.message.client to fastjson;
    exports top.tonydon.message.server;
    opens top.tonydon.message.server to fastjson;
}