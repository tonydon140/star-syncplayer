package top.tonydon.util;

import top.tonydon.message.*;
import top.tonydon.message.client.BindMessage;
import top.tonydon.message.client.MovieMessage;
import top.tonydon.message.server.ConnectMessage;
import top.tonydon.message.server.ResponseMessage;

import java.util.HashMap;
import java.util.Map;

public class MessageType {
    public static final int CONNECT_TYPE = 1;
    public static final int MOVIE_TYPE = 2;
    public static final int BIND_TYPE = 3;

    public static final int RESPONSE_TYPE = 101;


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
        typeMap.put(CONNECT_TYPE, ConnectMessage.class);
        typeMap.put(MOVIE_TYPE, MovieMessage.class);
        typeMap.put(RESPONSE_TYPE, ResponseMessage.class);
        typeMap.put(BIND_TYPE, BindMessage.class);
    }
}
