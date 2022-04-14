package top.tonydon.util;

import top.tonydon.message.*;
import top.tonydon.message.client.*;
import top.tonydon.message.server.*;

import java.util.HashMap;
import java.util.Map;

public class MessageType {


    /**************************************************************************
     *
     * 服务端消息
     *
     **************************************************************************/
    public static final int SERVER_CONNECT = 101;
    public static final int SERVER_RESPONSE = 102;
    public static final int SERVER_BIND = 103;
    public static final int SERVER_UNBIND = 104;
    public static final int SERVER_OFFLINE = 105;
    public static final int SERVER_MOVIE = 106;


    /**************************************************************************
     *
     * 客户端消息
     *
     **************************************************************************/
    public static final int CLIENT_BIND = 201;
    public static final int CLIENT_UNBIND = 202;
    public static final int CLIENT_MOVIE = 203;


    /**
     * 根据类型获取 class 类型
     *
     * @param type 类型
     * @return class
     */
    public static Class<? extends Message> getClazz(int type) {
        return typeMap.get(type);
    }


    /**
     * type 和 Message 的 Map 集合
     */
    private static final Map<Integer, Class<? extends Message>> typeMap = new HashMap<>();

    static {
        typeMap.put(SERVER_CONNECT, ServerConnectMessage.class);
        typeMap.put(SERVER_RESPONSE, ServerResponseMessage.class);
        typeMap.put(SERVER_BIND, ServerBindMessage.class);
        typeMap.put(SERVER_UNBIND, ServerUnbindMessage.class);
        typeMap.put(SERVER_OFFLINE, ServerOfflineMessage.class);
        typeMap.put(SERVER_MOVIE, ServerMovieMessage.class);

        typeMap.put(CLIENT_BIND, ClientBindMessage.class);
        typeMap.put(CLIENT_UNBIND, ClientUnbindMessage.class);
        typeMap.put(CLIENT_MOVIE, ClientMovieMessage.class);
    }
}
