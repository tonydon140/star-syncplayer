package top.tonydon.message.server;

import cn.hutool.core.util.RandomUtil;
import lombok.*;
import top.tonydon.message.Message;
import top.tonydon.util.MessageType;

/**
 * 连接消息，连接成功返回星星号
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerConnectMessage extends Message {

    private String number;

    public static ServerConnectMessage success(){
        // 随机生成 8 位数字字符串
        String number = RandomUtil.randomNumbers(8).toUpperCase();
        return new ServerConnectMessage(number);
    }

    @Override
    public int getType() {
        return MessageType.SERVER_CONNECT;
    }
}
