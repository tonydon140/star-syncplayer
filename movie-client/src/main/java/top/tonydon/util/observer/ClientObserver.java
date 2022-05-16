package top.tonydon.util.observer;


import top.tonydon.message.common.*;
import top.tonydon.message.server.*;

public interface ClientObserver {
    void onConnected(ServerConnectMessage message);

    void onMovie(MovieMessage message);

    void onBind(BindMessage message);

    void onUnbind();

    void onOffline();

    void onBulletScreen(BulletScreenMessage message);
}
