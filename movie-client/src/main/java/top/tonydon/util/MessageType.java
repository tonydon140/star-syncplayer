package top.tonydon.util;

import top.tonydon.message.Message;
import top.tonydon.message.common.*;
import top.tonydon.message.server.*;

import java.util.HashMap;
import java.util.Map;

public class MessageType {

    /**************************************************************************
     *
     * 公共消息
     *
     **************************************************************************/

    public static final int BIND = 101;
    public static final int MOVIE = 102;
    public static final int BULLET_SCREEN = 103;
    public static final int NOTIFICATION = 104;

    /**************************************************************************
     *
     * 服务端消息
     *
     **************************************************************************/
    public static final int SERVER_CONNECT = 201;
    public static final int SERVER_RESPONSE = 202;

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

        typeMap.put(BIND, BindMessage.class);
        typeMap.put(NOTIFICATION, Notification.class);
        typeMap.put(MOVIE, MovieMessage.class);
        typeMap.put(BULLET_SCREEN, BulletScreenMessage.class);
    }
}
