package top.tonydon.message;


import top.tonydon.message.common.ActionMessage;
import top.tonydon.message.common.MovieMessage;
import top.tonydon.message.common.StringMessage;

import java.util.HashMap;
import java.util.Map;

public class MessageType {


    public static final int ACTION = 101;
    public static final int STRING = 102;
    public static final int MOVIE = 103;


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
        typeMap.put(ACTION, ActionMessage.class);
        typeMap.put(STRING, StringMessage.class);
        typeMap.put(MOVIE, MovieMessage.class);
    }
}
