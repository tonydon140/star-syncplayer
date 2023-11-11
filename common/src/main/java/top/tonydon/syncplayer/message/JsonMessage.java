package top.tonydon.syncplayer.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JsonMessage {
    private int type;
    private String json;

    private static final ObjectMapper mapper = new ObjectMapper();

    public JsonMessage() {
    }

    public JsonMessage(int type, String json) {
        this.type = type;
        this.json = json;
    }

    /**
     * 将 JsonMessage 的 json 字符串解析为 Message 实例
     *
     * @param json json 字符串
     * @return Message 实例
     */
    public static Message parse(String json) {
        Message message;
        try {
            JsonMessage jsonMessage = mapper.readValue(json, JsonMessage.class);
            message = mapper.readValue(jsonMessage.getJson(), MessageType.getClazz(jsonMessage.getType()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    @Override
    public String toString() {
        return "JsonMessage{" +
                "type=" + type +
                ", json='" + json + '\'' +
                '}';
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
