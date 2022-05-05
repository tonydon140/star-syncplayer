package top.tonydon.util.observer;


import top.tonydon.message.server.*;

public interface ClientObserver {
    void onConnected(ServerConnectMessage message);

    void onMovie(ServerMovieMessage message);

    void onBind(ServerBindMessage message);

    void onUnBind(ServerUnbindMessage message);

    void onOffline(ServerOfflineMessage message);

    void onBulletScreen(ServerBulletScreenMessage message);
}
