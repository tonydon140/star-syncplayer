package top.tonydon.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.tonydon.ws.ServerWebSocket;

/**
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketGroup {
    private ServerWebSocket self;
    private ServerWebSocket target;
}
