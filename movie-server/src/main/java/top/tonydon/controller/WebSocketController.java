package top.tonydon.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.tonydon.util.ResponseResult;
import top.tonydon.ws.ServerWebSocket;

@RestController
@RequestMapping("/ws")
public class WebSocketController {

    @RequestMapping("/count")
    public ResponseResult getCount(){
        return ResponseResult.success(ServerWebSocket.getCount());
    }
}
