package top.tonydon.syncplayer.constant

object ClientConstants {
    const val ID_LENGTH = 6
    const val EXAMPLE_URL = "ws://www.example.com:8888/websocket"

//    const val DEFAULT_URL = "ws://localhost:6515/syncplayer"
    const val DEFAULT_URL = "ws://123.57.92.83:6515/syncplayer"

    // 使用 gitee 发行版作为检查更新的地址
    const val CHECK_UPDATE_URL = "https://gitee.com/api/v5/repos/shuilanjiao/star-syncplayer/releases/latest"

    const val LATEST_URL = "https://gitee.com/shuilanjiao/star-syncplayer/releases"
    const val VLC_DOWNLOAD_URL = "https://www.videolan.org/vlc/index.zh_CN.html"
    const val ABOUT_URL = "https://shuilanjiao.gitee.io/p/2023/10/star-syncplayer/"
    const val URL_REG = "^(wss|ws)://[\\S]+"
    const val VERSION = "v2.1.0"

    const val TITLE = "星星电影院 $VERSION"

    /**
     *  窗口尺寸
     */
    const val CLIENT_DEFAULT_WIDTH = 1056.0
    const val CLIENT_DEFAULT_HEIGHT = 710.0
    const val CLIENT_MIN_WIDTH = 496.0
    const val CLIENT_MIN_HEIGHT = 395.0

    /**
     * 鼠标移动时间间隔
     */
    const val MOUSE_MOVE_INTERVAL = 50
}
