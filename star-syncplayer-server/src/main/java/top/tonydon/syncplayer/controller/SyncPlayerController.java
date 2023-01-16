package top.tonydon.syncplayer.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.tonydon.syncplayer.util.ResponseResult;
import top.tonydon.syncplayer.websocket.SyncPlayerItem;

@RestController
@RequestMapping("/syncplayer")
public class SyncPlayerController {

    @RequestMapping("/count")
    public ResponseResult getCount() {
        return ResponseResult.success(SyncPlayerItem.getCount());
    }

    @RequestMapping("/map")
    public ResponseResult getMap() {
        return ResponseResult.success(SyncPlayerItem.getMap());
    }
}
