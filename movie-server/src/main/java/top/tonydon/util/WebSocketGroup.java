package top.tonydon.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.tonydon.websocket.WebSocket;

/**
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketGroup {
    private WebSocket self;
    private WebSocket target;
}
