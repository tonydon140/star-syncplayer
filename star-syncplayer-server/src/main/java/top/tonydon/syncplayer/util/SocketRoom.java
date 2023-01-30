package top.tonydon.syncplayer.util;

import top.tonydon.syncplayer.websocket.SyncPlayerItem;

import java.util.HashSet;
import java.util.Set;

public class SocketRoom {

    private final Set<SyncPlayerItem> ROOM_ITEMS;

    public SocketRoom() {
        this.ROOM_ITEMS = new HashSet<>();
    }

    public Set<SyncPlayerItem> items() {
        return this.ROOM_ITEMS;
    }

    public void add(SyncPlayerItem item) {
        this.ROOM_ITEMS.add(item);
    }

    public void remove(SyncPlayerItem item) {
        this.ROOM_ITEMS.remove(item);
    }

    public int size(){
        return this.ROOM_ITEMS.size();
    }

}
