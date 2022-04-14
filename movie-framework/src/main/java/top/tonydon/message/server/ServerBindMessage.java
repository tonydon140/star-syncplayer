package top.tonydon.message.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.tonydon.message.Message;
import top.tonydon.util.MessageType;

/**
 * 服务端绑定客户端消息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerBindMessage extends Message {
    private String targetNumber;

    public String getTargetNumber() {
        return targetNumber;
    }

    public void setTargetNumber(String targetNumber) {
        this.targetNumber = targetNumber;
    }

    @Override
    public int getType() {
        return MessageType.SERVER_BIND;
    }
}
