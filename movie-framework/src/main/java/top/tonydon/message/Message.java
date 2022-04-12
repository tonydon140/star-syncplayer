package top.tonydon.message;

import com.alibaba.fastjson.JSON;

/**
 * 所有消息类型的顶类
 */
public abstract class Message {
    /**
     * 获取消息的类型码
     *
     * @return 消息类型码
     */
    public abstract int getType();

    /**
     * 将 Message 转为 JsonMessage，并序列化为 json 字符串
     *
     * @return json 字符串
     */
    public String toJson() {
        JsonMessage jsonMessage = new JsonMessage(getType(), JSON.toJSONString(this));
        return JSON.toJSONString(jsonMessage);
    }
}
