package top.tonydon.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.tonydon.websocket.WebSocket;

@RestController
public class HelloController {

    @RequestMapping("/send")
    public void sendMessage(){
        WebSocket.broadcast();
    }
}
