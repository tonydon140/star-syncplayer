package top.tonydon.syncplayer

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import top.tonydon.syncplayer.constant.ClientConstants
import java.util.*

class MainApplication : Application() {

    private var mainWindow: MainWindow? = null

    override fun start(primaryStage: Stage) {
        // 创建窗口
        mainWindow = MainWindow(primaryStage)

        // 加载窗口
        val parent = mainWindow!!.load()
        val scene = Scene(parent)
        primaryStage.icons.add(Image(Objects.requireNonNull(javaClass.getResource("icon/star_128.png")).toString()))
        primaryStage.title = ClientConstants.TITLE
        primaryStage.scene = scene
        primaryStage.minWidth = ClientConstants.CLIENT_MIN_WIDTH
        primaryStage.minHeight = ClientConstants.CLIENT_MIN_HEIGHT
        primaryStage.width = ClientConstants.CLIENT_DEFAULT_WIDTH
        primaryStage.height = ClientConstants.CLIENT_DEFAULT_HEIGHT
        primaryStage.show()
        mainWindow!!.init(this)
    }

    override fun stop() {
        super.stop()
        // 关闭客户端
        mainWindow!!.close()
    }
}

fun main(){
    Application.launch(MainApplication::class.java)
}