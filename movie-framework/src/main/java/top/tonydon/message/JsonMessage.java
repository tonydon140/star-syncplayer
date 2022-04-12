package top.tonydon.message;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.tonydon.util.MessageType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonMessage {
    private int type;
    private String json;

    /**
     * 将 JsonMessage 的 json 字符串解析为 Message 实例
     *
     * @param json json 字符串
     * @return Message 实例
     */
    public static Message parse(String json) {
        JsonMessage jsonMessage = JSON.parseObject(json, JsonMessage.class);
        return JSON.parseObject(jsonMessage.getJson(), MessageType.getClazz(jsonMessage.getType()));
    }
}
