package top.tonydon.message.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.tonydon.message.Message;
import top.tonydon.util.MessageType;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BindMessage extends ClientMessage {

    private String targetNumber;

    @Override
    public int getType() {
        return MessageType.BIND_TYPE;
    }
}
