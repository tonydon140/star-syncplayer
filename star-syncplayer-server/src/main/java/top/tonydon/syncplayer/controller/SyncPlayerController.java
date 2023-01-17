package top.tonydon.syncplayer.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.tonydon.syncplayer.util.ResponseResult;
import top.tonydon.syncplayer.websocket.SyncPlayerItem;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/syncplayer")
public class SyncPlayerController {

    @RequestMapping("/count")
    public ResponseResult getCount() {
        return ResponseResult.success(SyncPlayerItem. PLAYER_ITEM_MAP.size());
    }

    @RequestMapping("/map")
    public ResponseResult getMap() {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, SyncPlayerItem> entry : SyncPlayerItem.PLAYER_ITEM_MAP.entrySet()) {
            map.put(entry.getKey(), entry.getValue().friendId);
        }
        return ResponseResult.success(map);
    }
}
