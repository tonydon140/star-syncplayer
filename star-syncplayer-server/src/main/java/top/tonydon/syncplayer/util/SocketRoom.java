package top.tonydon.syncplayer.util;

import top.tonydon.syncplayer.websocket.ProjectionRoomItem;

import java.util.HashSet;
import java.util.Set;

public class SocketRoom {

    private final Set<ProjectionRoomItem> ROOM_ITEMS;

    public SocketRoom() {
        this.ROOM_ITEMS = new HashSet<>();
    }

    public Set<ProjectionRoomItem> items() {
        return this.ROOM_ITEMS;
    }

    public void add(ProjectionRoomItem item) {
        this.ROOM_ITEMS.add(item);
    }

    public void remove(ProjectionRoomItem item) {
        this.ROOM_ITEMS.remove(item);
    }

    public int size(){
        return this.ROOM_ITEMS.size();
    }

}
