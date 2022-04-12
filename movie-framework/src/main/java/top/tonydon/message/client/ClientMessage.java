package top.tonydon.message.client;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.tonydon.message.Message;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class ClientMessage extends Message {
    private String selfNumber;
}
