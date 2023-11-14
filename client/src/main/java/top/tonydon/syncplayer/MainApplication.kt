package top.tonydon.syncplayer

import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinReg
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.image.Image
import javafx.scene.layout.Pane
import javafx.stage.Modality
import javafx.stage.Stage
import top.tonydon.syncplayer.constant.ClientConstants
import java.util.*

class MainApplication : Application() {

    private var mainWindow: MainWindow? = null

    private fun setBaseParameter(stage: Stage) {
        stage.icons.add(Image(Objects.requireNonNull(javaClass.getResource("icon/star_128.png")).toString()))
        stage.title = ClientConstants.TITLE
        stage.minWidth = ClientConstants.CLIENT_MIN_WIDTH
        stage.minHeight = ClientConstants.CLIENT_MIN_HEIGHT
        stage.width = ClientConstants.CLIENT_DEFAULT_WIDTH
        stage.height = ClientConstants.CLIENT_DEFAULT_HEIGHT
    }

    private fun loadConfig() {
        val inputStream = javaClass.getResourceAsStream("config.properties")
        val config = Properties()
        config.load(inputStream)

        ClientConstants.DEFAULT_URL = config["url.server"] as String
        ClientConstants.EXAMPLE_URL = config["url.example"] as String
        ClientConstants.ABOUT_URL = config["url.about"] as String
        ClientConstants.LATEST_URL = config["url.latest"] as String
        ClientConstants.CHECK_UPDATE_URL = config["url.update"] as String
        ClientConstants.VLC_DOWNLOAD_URL = config["url.vlc"] as String
        ClientConstants.URL_REG = config["url.reg"] as String

        ClientConstants.ID_LENGTH = config["id.length"].toString().toInt()
        ClientConstants.VERSION = config["version"] as String

        ClientConstants.CLIENT_MIN_WIDTH = config["width.min"].toString().toDouble()
        ClientConstants.CLIENT_DEFAULT_WIDTH = config["width.default"].toString().toDouble()
        ClientConstants.CLIENT_MIN_HEIGHT = config["height.min"].toString().toDouble()
        ClientConstants.CLIENT_DEFAULT_HEIGHT = config["height.default"].toString().toDouble()
        ClientConstants.MOUSE_MOVE_INTERVAL = config["mouse.interval"].toString().toInt()
        inputStream?.close()
    }

    override fun start(primaryStage: Stage) {
        // 加载配置文件
        loadConfig();
        // 监测是否存在VLC播放器，如果没有则提示下载
        if (!checkVlc()) {
            hintDownloadVLC()
            primaryStage.close()
            return
        }
        // 设置窗口基本属性
        setBaseParameter(primaryStage)
        // 加载窗口
        mainWindow = MainWindow(primaryStage)
        val parent = mainWindow!!.load()
        val scene = Scene(parent)
        primaryStage.scene = scene
        // 显示窗口
        primaryStage.show()
        // 初始化MainWindow
        mainWindow!!.init(this)
    }

    private fun checkVlc(): Boolean {
        val keys = Advapi32Util.registryGetKeys(WinReg.HKEY_CLASSES_ROOT, "Applications")
        return keys.contains("vlc.exe")
    }

    private fun hintDownloadVLC() {
        // 显示一个空白窗口
        val stage = Stage()
        stage.scene = Scene(Pane())
        setBaseParameter(stage)
        stage.show()
        // 弹窗提示下载VLC
        val alert = Alert(Alert.AlertType.WARNING)
        alert.headerText = "未检测到VLC media player！"
        alert.contentText =
            "本软件基于VLC播放器技术，可以支持播放绝大部分视频格式。请点击确定前往下载安装VLC media player，否则无法使用软件！"
        alert.initModality(Modality.WINDOW_MODAL)
        alert.initOwner(stage)
        alert.showAndWait().ifPresent {
            hostServices.showDocument(ClientConstants.VLC_DOWNLOAD_URL)
        }
        stage.close()
    }

    override fun stop() {
        // 关闭客户端
        mainWindow?.close()
    }
}

fun main() {
    Application.launch(MainApplication::class.java)
}