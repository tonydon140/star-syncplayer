package top.tonydon.util.observer;


import top.tonydon.message.common.BindMessage;
import top.tonydon.message.common.BulletScreenMessage;
import top.tonydon.message.common.MovieMessage;
import top.tonydon.message.server.ServerConnectMessage;

public interface ClientObserver {
    void onConnected(ServerConnectMessage message);

    void onMovie(MovieMessage message);

    void onBind(BindMessage message);

    void onUnbind();

    void onOffline();

    void onBulletScreen(BulletScreenMessage message);
}
