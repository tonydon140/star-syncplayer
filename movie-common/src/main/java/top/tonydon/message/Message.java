package top.tonydon.message;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 所有消息类型的顶类
 */
public abstract class Message {

    public Message() {
    }

    /**
     * 获取消息的类型码
     *
     * @return 消息类型码
     */
    @JsonIgnore
    public abstract int getType();

    /**
     * 将 Message 转为 JsonMessage，并序列化为 json 字符串
     *
     * @return json 字符串
     */
    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();

        String json;
        try {
            JsonMessage jsonMessage = new JsonMessage(getType(), mapper.writeValueAsString(this));
            json = mapper.writeValueAsString(jsonMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    @Override
    public String toString() {
        return "Message{type=" + getType() + "}";
    }
}
