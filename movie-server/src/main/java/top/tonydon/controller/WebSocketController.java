package top.tonydon.controller;


import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.tonydon.constant.RedisConstants;
import top.tonydon.util.ResponseResult;
import top.tonydon.ws.ServerWebSocket;

import java.util.Map;

@RestController
@RequestMapping("/ws")
public class WebSocketController {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/count")
    public ResponseResult getCount() {
        return ResponseResult.success(ServerWebSocket.getCount());
    }

    @RequestMapping("/map")
    public ResponseResult getMap() {
        return ResponseResult.success(ServerWebSocket.getMap());
    }

    @RequestMapping("/connection-count")
    public ResponseResult getConnectionCount() {
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(RedisConstants.KEY_CONNECTION_COUNT);
        return ResponseResult.success(map);
    }
}
