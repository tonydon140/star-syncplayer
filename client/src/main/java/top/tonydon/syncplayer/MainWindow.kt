package top.tonydon.syncplayer

import com.fasterxml.jackson.databind.ObjectMapper
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.application.Application
import javafx.application.HostServices
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.robot.Robot
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.util.Duration
import org.slf4j.LoggerFactory
import top.tonydon.syncplayer.client.WebClient
import top.tonydon.syncplayer.constant.ClientConstants
import top.tonydon.syncplayer.constant.UIState
import top.tonydon.syncplayer.constant.VideoConstants
import top.tonydon.syncplayer.entity.VersionInfo
import top.tonydon.syncplayer.exception.HttpException
import top.tonydon.syncplayer.exception.ResultException
import top.tonydon.syncplayer.message.ActionCode
import top.tonydon.syncplayer.message.common.ActionMessage
import top.tonydon.syncplayer.message.common.MovieMessage
import top.tonydon.syncplayer.message.common.StringMessage
import top.tonydon.syncplayer.task.CountTask
import top.tonydon.syncplayer.util.AlertUtils
import top.tonydon.syncplayer.util.TimeFormat.getText
import top.tonydon.syncplayer.util.TimeFormat.setTotal
import top.tonydon.syncplayer.util.URIUtils
import top.tonydon.syncplayer.util.observer.ClientObserver
import top.tonydon.syncplayer.util.observer.CountObserver
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface
import uk.co.caprica.vlcj.media.Media
import uk.co.caprica.vlcj.media.MediaEventAdapter
import uk.co.caprica.vlcj.media.Meta
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import java.net.URI
import java.net.URISyntaxException
import java.net.http.*
import java.util.*
import java.util.concurrent.TimeUnit

class MainWindow(private val primaryStage: Stage) {
    private val log = LoggerFactory.getLogger(MainWindow::class.java)
    private val robot: Robot = Robot()
    private val countTask: CountTask = CountTask(TimeUnit.SECONDS, 1)
    private val factory: MediaPlayerFactory = MediaPlayerFactory()
    private val player: EmbeddedMediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer()

    // UI属性
    private val playBlackIcon: Image = Image(javaClass.getResource("icon/play_black.png")!!.toString())
    private val playBlueIcon: Image = Image(javaClass.getResource("icon/play_blue.png")!!.toString())
    private val pauseBlackIcon: Image = Image(javaClass.getResource("icon/pause_black.png")!!.toString())
    private val pauseBlueIcon: Image = Image(javaClass.getResource("icon/pause_blue.png")!!.toString())
    private val loveIcon: Image = Image(javaClass.getResource("icon/love.png")!!.toString())
    private val linkIcon: Image = Image(javaClass.getResource("icon/link_black.png")!!.toString())
    private val breakIcon: Image = Image(javaClass.getResource("icon/break_black.png")!!.toString())
    private val syncBlackIcon: Image = Image(javaClass.getResource("icon/sync_black.png")!!.toString())
    private val syncBlueIcon: Image = Image(javaClass.getResource("icon/sync_blue.png")!!.toString())
    private val fullScreenIcon: Image = Image(javaClass.getResource("icon/full_screen.png")!!.toString())
    private val soundOpenIcon: Image = Image(javaClass.getResource("icon/sound_open.png")!!.toString())
    private val soundCloseIcon: Image = Image(javaClass.getResource("icon/sound_close.png")!!.toString())
    private val selfColor = Color.WHITESMOKE
    private val friendColor = Color.web("#FFFF00")
    private val menuBar: MenuBar = MenuBar()
    private val root: AnchorPane = AnchorPane()
    private val playPane: AnchorPane = AnchorPane()
    private val syncPane: AnchorPane = AnchorPane()
    private val controlBox: VBox = VBox()
    private val videoPane: FlowPane = FlowPane()
    private val bindPane: AnchorPane = AnchorPane()
    private val playImage: ImageView = ImageView(playBlackIcon)
    private val bindImage: ImageView = ImageView(linkIcon)
    private val syncImage: ImageView = ImageView(syncBlackIcon)
    private val soundImage: ImageView = ImageView(soundOpenIcon)
    private val volumeLabel: Label = Label("80%")
    private val selfNumberLabel: Label = Label()
    private val timeLabel: Label = Label("0:00/0:00")
    private val videoImageView: ImageView = ImageView()
    private val videoSlider: Slider = Slider()
    private val volumeSlider: Slider = Slider(0.0, 100.0, 80.0)
    private val rateSpinner: Spinner<Number> = Spinner(0.5, 2.5, 1.0, 0.05)
    private val bulletScreenInput: TextField = TextField()
    private val friendInput: TextField = TextField()
    private val closeServerItem: MenuItem = MenuItem("断开连接")
    private val connectDefaultServerItem: MenuItem = MenuItem("连接默认服务器")
    private val connectCustomServerItem: MenuItem = MenuItem("连接自定义服务器")

    // 应用属性
    private var primaryScene: Scene? = null
    private var hostServices: HostServices? = null
    private var client: WebClient? = null

    // 状态属性
    private var mouse = false
    private var isMouseBottom = false
    private var isMute = false
    private var isBind = false
    private var isConnection = false
    private var isCustom = false
    private var id: String? = null

    private fun setListener() {
        root.widthProperty().addListener { _, _, newValue -> updateWidth(newValue.toDouble()) }

        root.heightProperty().addListener { _, _, newValue -> updateHeight(newValue.toDouble()) }

        // 时长进度条监听
        videoSlider.setOnMousePressed { mouse = true }
        videoSlider.setOnMouseReleased {
            mouse = false
            val millis = videoSlider.value.toLong()
            // 转换进度
            player.controls().setTime(millis)
            timeLabel.text = getText(millis)
        }

        // 音量进度条监听
        volumeSlider.valueProperty().addListener { _, _, newVolume ->
            val volume = newVolume.toInt()
            player.audio().setVolume(volume)
            volumeLabel.text = "$volume%"
        }

        // 倍速输入框监听
        rateSpinner.valueProperty().addListener { _, _, newRate ->
            val rate = newRate.toFloat()
            player.controls().setRate(rate)
            log.debug("set rate = {}", rate)
        }
    }

    private fun updateWidth(width: Double) {
        // 如果不是全屏，做变换
        if (!primaryStage.isFullScreen) {
            val needHeight = width / 16 * 9
            val currentHeight = root.height - 86
            if (currentHeight >= needHeight) {
                videoImageView.fitHeight = needHeight
                videoImageView.fitWidth = width
            } else {
                val currentWidth = currentHeight / 9 * 16
                videoImageView.fitHeight = currentHeight
                videoImageView.fitWidth = currentWidth
            }
        }
        // 菜单栏同步变化
        menuBar.prefWidth = width
        // 控制栏同步变化
        controlBox.prefWidth = width
        videoPane.prefWidth = width
    }

    private fun updateHeight(pHeight: Double) {
        val height = pHeight - 86
        if (!primaryStage.isFullScreen) {
            val needWidth = height / 9 * 16
            val currentWidth = root.width
            if (currentWidth >= needWidth) {
                videoImageView.fitHeight = height
                videoImageView.fitWidth = needWidth
            } else {
                val currentHeight = currentWidth / 16 * 9
                videoImageView.fitHeight = currentHeight
                videoImageView.fitWidth = currentWidth
            }
        }
        videoPane.prefHeight = height
    }

    fun init(application: Application) {
        // 获得主场景
        primaryScene = root.scene
        // 获取 host
        hostServices = application.hostServices
        // 检查更新
        checkUpdate(true)
        // 连接服务器
        connectServer(ClientConstants.DEFAULT_URL)
        // 请求焦点
        volumeSlider.requestFocus()

        // 获取主屏幕尺寸
        val bounds = Screen.getPrimary().bounds
        val screenWidth = bounds.width
        val screenHeight = bounds.height

        // 添加全屏效果
        primaryStage.fullScreenProperty().addListener { _, _, isFull ->
            if (isFull) {
                videoPane.layoutY = 0.0
                controlBox.opacity = 0.8
                videoImageView.fitHeight = screenHeight
                videoImageView.fitWidth = screenWidth
            } else {
                // 恢复视频窗口尺寸
                videoPane.layoutY = 25.0
                controlBox.opacity = 1.0
                controlBox.isVisible = true
                // 恢复鼠标
                primaryScene!!.cursor = Cursor.DEFAULT
                // 恢复尺寸
                updateWidth(root.width)
                updateHeight(root.height)
            }
        }


        // 启动任务
        countTask.start()
        countTask.addObserver(object : CountObserver {
            override fun countChange(old: Int, cur: Int) {
                // 当视频正在播放时，每搁一段时间移动一下鼠标
                val isPlay = (cur % ClientConstants.MOUSE_MOVE_INTERVAL == 0
                        && player.status().isPlaying)
                if (isPlay) {
                    Platform.runLater {
                        val mouseX = robot.mouseX
                        val mouseY = robot.mouseY
                        robot.mouseMove(mouseX + 1, mouseY + 1)
                        robot.mouseMove(mouseX, mouseY)
                        log.debug("mouse move")
                    }
                }

                // 全屏状态下、鼠标不在控制栏附近、鼠标指针没有隐藏，则每隔大约两秒隐藏鼠标
                if (primaryStage.isFullScreen && !isMouseBottom && primaryScene!!.cursor !== Cursor.NONE && cur % 2 == 0)
                    primaryScene!!.cursor = Cursor.NONE
            }
        })

        // 监听全屏鼠标移动
        videoImageView.onMouseMoved = EventHandler { event: MouseEvent ->
            // 不是全屏状态下直接返回
            if (!primaryStage.isFullScreen)
                return@EventHandler

            // 显示鼠标
            primaryScene!!.cursor = Cursor.DEFAULT

            // 鼠标是否在控制栏附近
            isMouseBottom = event.screenY > screenHeight - 80
            controlBox.isVisible = isMouseBottom
        }
        player.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun timeChanged(mediaPlayer: MediaPlayer, newTime: Long) {
                if (!mouse) {
                    Platform.runLater {
                        videoSlider.value = newTime.toDouble()
                        timeLabel.text = getText(newTime)
                    }

                    // 当播放结束时暂停
                    val temp = (mediaPlayer.media().info().duration() - newTime).toDouble()
                    if (temp <= 120) {
                        pause()
                    }
                }
            }
        })
        player.events().addMediaEventListener(object : MediaEventAdapter() {
            override fun mediaMetaChanged(media: Media, metaType: Meta) {
                // 1. 按钮解禁
                flushUI(UIState.OPEN_VIDEO)

                // 设置时长进度条
                val total = media.info().duration()
                videoSlider.max = total.toDouble()
                videoSlider.isVisible = true
                setTotal(total)
                Platform.runLater { timeLabel.text = getText(0) }

                // 设置初始音量
                player.audio().setVolume(VideoConstants.DEFAULT_VOLUME)

                // 设置初始倍速
                player.controls().setRate(VideoConstants.DEFAULT_RATE)

                // 3. 设置视频点击播放/暂停
                videoImageView.setOnMouseClicked { playOrPause(null) }
            }
        })
    }

    fun load(): Parent {
        setMenuBar()
        setCenter()
        setBottom()
        setListener()
        return root
    }

    private fun setMenuBar() {
        menuBar.prefHeight = 25.0

        // 打开视频
        val openVideoMenu = Menu()
        val openVideoLabel = Label("打开视频")
        openVideoMenu.graphic = openVideoLabel
        openVideoLabel.setOnMouseClicked { openVideo() }

        // 服务器菜单
        val serverMenu = Menu("服务器")
        serverMenu.items.add(closeServerItem)
        serverMenu.items.add(connectDefaultServerItem)
        serverMenu.items.add(connectCustomServerItem)
        closeServerItem.setOnAction { closeServer() }
        connectDefaultServerItem.setOnAction {
            isCustom = true
            connectServer(ClientConstants.DEFAULT_URL)
        }
        connectCustomServerItem.setOnAction {
            connectCustomServer()
        }

        // 帮助菜单
        val helpMenu = Menu("帮助")
        val updateItem = MenuItem("检查更新")
        val aboutItem = MenuItem("关于")
        helpMenu.items.add(updateItem)
        helpMenu.items.add(aboutItem)
        updateItem.setOnAction { checkUpdate(false) }
        aboutItem.setOnAction { about() }
        menuBar.menus.add(openVideoMenu)
        menuBar.menus.add(serverMenu)
        menuBar.menus.add(helpMenu)
        root.children.add(menuBar)
    }

    private fun setCenter() {
        videoPane.layoutY = 25.0
        videoPane.background = Background.fill(Color.rgb(16, 16, 16))
        videoPane.alignment = Pos.CENTER
        videoImageView.isPreserveRatio = true
        player.videoSurface().set(ImageViewVideoSurface(videoImageView))
        videoPane.children.add(videoImageView)
        root.children.add(videoPane)
    }

    private fun setBottom() {
        AnchorPane.setBottomAnchor(controlBox, 0.0)
        controlBox.padding = Insets(5.0, 0.0, 10.0, 0.0)
        controlBox.background = Background.fill(Color.valueOf("f4f4f4"))

        // 设置进度条
        setVideoSlider(controlBox)
        // 设置底部控制栏
        setBottomControlBox(controlBox)
        root.children.add(controlBox)
    }

    // 视频进度条
    private fun setVideoSlider(videoControlBox: VBox) {
        videoSlider.isDisable = true
        videoControlBox.children.add(videoSlider)
    }

    // 底部控制栏
    private fun setBottomControlBox(videoControlBox: VBox) {
        val gridPane = GridPane()
        gridPane.alignment = Pos.CENTER
        gridPane.hgap = 10.0
        setConnectBox(gridPane)
        setPlayOrPauseItem(gridPane)
        setSyncItem(gridPane)
        setTimeItem(gridPane)
        setBulletScreenItem(gridPane)
        setSoundItem(gridPane)
        setRateItem(gridPane)
        setFullScreen(gridPane)
        videoControlBox.children.add(gridPane)
    }

    // 时间组件
    private fun setTimeItem(gridPane: GridPane) {
        timeLabel.font = Font(15.0)
        GridPane.setMargin(timeLabel, Insets(0.0, 0.0, 0.0, 10.0))
        gridPane.add(timeLabel, 0, 0)
    }

    // 绑定组件
    private fun setConnectBox(gridPane: GridPane) {
        val hBox = HBox(10.0)
        hBox.alignment = Pos.CENTER

        // 爱心图标
        val loveImage = ImageView(loveIcon)

        // 自己的星星号
        val tooltip = Tooltip("星星号，点击复制")
        tooltip.showDelay = Duration.ZERO
        selfNumberLabel.tooltip = tooltip
        selfNumberLabel.cursor = Cursor.HAND
        selfNumberLabel.setOnMouseClicked {
            val clipboard = Clipboard.getSystemClipboard()
            val content = ClipboardContent()
            content.putString(id)
            clipboard.setContent(content)
        }

        // 朋友的星星号
        friendInput.prefWidth = 95.0
        friendInput.promptText = "她/他的星星号"
        friendInput.textProperty().addListener { _, oldValue, newValue ->
            // 限制长度
            if (newValue.length > ClientConstants.ID_LENGTH) friendInput.text = oldValue
            // 限制输入只为数字
            var isNumber = true
            for (ch in newValue.toCharArray()) {
                if (ch < '0' || ch > '9') {
                    isNumber = false
                    break
                }
            }
            if (!isNumber) friendInput.text = oldValue
        }

        // 绑定按钮
        bindPane.cursor = Cursor.HAND
        bindPane.children.add(bindImage)
        // 点击事件，点击进行绑定
        bindPane.setOnMouseClicked {
            if (isBind()) {
                // 如果已经绑定了就解除绑定
                // 1. 创建确认信息
                val alert = Alert(Alert.AlertType.CONFIRMATION)
                alert.title = "确认消息"
                alert.headerText = "确定要解绑吗"

                // 2. 设置主窗口
                alert.initModality(Modality.WINDOW_MODAL)
                alert.initOwner(primaryStage)

                // 4. 显示窗口
                val result = alert.showAndWait()

                // 确定要解除绑定
                if (result.isPresent && result.get() == ButtonType.OK) {
                    // 发送解除绑定消息
                    client!!.send(ActionMessage.UNBIND.toJson())
                    flushUI(UIState.UN_BIND)
                    isBind = false
                }
            } else {
                val number = friendInput.text
                // 校验星星号
                if (number.length != ClientConstants.ID_LENGTH) {
                    AlertUtils.error("星星号必须是6位数字", primaryStage)
                    return@setOnMouseClicked
                }
                client!!.send(StringMessage.BIND.setContent(number).toJson())
            }
        }
        hBox.children.add(loveImage)
        hBox.children.add(selfNumberLabel)
        hBox.children.add(friendInput)
        hBox.children.add(bindPane)
        gridPane.add(hBox, 1, 0)
    }

    // 播放/暂停组件
    private fun setPlayOrPauseItem(gridPane: GridPane) {
        playPane.isDisable = true
        playPane.cursor = Cursor.HAND
        playPane.opacity = 0.6
        playPane.setOnMouseClicked { playOrPause(it) }
        playPane.children.add(playImage)
        gridPane.add(playPane, 2, 0)
    }

    // 同步组件
    private fun setSyncItem(gridPane: GridPane) {
        syncPane.cursor = Cursor.HAND
        syncPane.isDisable = true
        syncPane.opacity = 0.6
        syncPane.setOnMouseClicked {
            val milliseconds = player.status().time()
            val rate = player.status().rate()
            client!!.send(MovieMessage(ActionCode.MOVIE_SYNC, milliseconds, rate).toJson())
        }
        syncPane.children.add(syncImage)
        gridPane.add(syncPane, 3, 0)
    }

    // 弹幕组件
    private fun setBulletScreenItem(gridPane: GridPane) {
        bulletScreenInput.promptText = "发送弹幕~"
        gridPane.add(bulletScreenInput, 4, 0)

        // 发送弹幕
        bulletScreenInput.setOnAction {
            // 获取内容
            val content = bulletScreenInput.text
            if (content.isEmpty())
                return@setOnAction
            bulletScreenInput.text = ""
            // 显示弹幕
            showBulletScreen(content, selfColor)
            // 如果已经建立了绑定，则发送弹幕消息
            if (isBind()) {
                client!!.send(StringMessage.BULLET_SCREEN.setContent(content).toJson())
            }
        }
    }

    // 声音组件
    private fun setSoundItem(gridPane: GridPane) {
        val hBox = HBox(5.0)
        hBox.alignment = Pos.CENTER

        // 声音图标，点击静音
        val pane = AnchorPane()
        pane.children.add(soundImage)
        pane.cursor = Cursor.HAND
        pane.setOnMouseClicked {
            if (isMute) {
                soundImage.image = soundOpenIcon
                if (hasMedia()) {
                    player.audio().isMute = false
                }
            } else {
                soundImage.image = soundCloseIcon
                if (hasMedia()) {
                    player.audio().isMute = true
                }
            }
            isMute = !isMute
        }
        hBox.children.add(pane)
        hBox.children.add(volumeSlider)
        volumeLabel.alignment = Pos.CENTER_RIGHT
        volumeLabel.prefWidth = 40.0
        volumeLabel.font = Font(15.0)
        hBox.children.add(volumeLabel)
        gridPane.add(hBox, 5, 0)
    }

    // 倍速组件
    private fun setRateItem(gridPane: GridPane) {
        rateSpinner.isDisable = true
        rateSpinner.isEditable = true
        rateSpinner.prefWidth = 80.0
        rateSpinner.styleClass.add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL)
        gridPane.add(rateSpinner, 6, 0)
    }

    // 全屏组件
    private fun setFullScreen(gridPane: GridPane) {
        val pane = AnchorPane()
        pane.cursor = Cursor.HAND
        pane.children.add(ImageView(fullScreenIcon))
        pane.setOnMouseClicked { event: MouseEvent ->
            // 鼠标左键点击有效
            if (event.button != MouseButton.PRIMARY) return@setOnMouseClicked

            // 设置全屏
            primaryStage.isFullScreen = !primaryStage.isFullScreen
        }
        gridPane.add(pane, 7, 0)
    }

    // 关闭服务器连接
    private fun closeServer() {
        try {
            client!!.closeBlocking()
            client = null
            log.info("server connection closed")
            isConnection = false
            isBind = false
            flushUI(UIState.CLOSED_SERVER)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    // 连接服务器
    private fun connectServer(url: String) {
        // 开启新的线程连接服务器
        Thread(Runnable {
            try {
                client = WebClient(URI(url))
                val flag = client!!.connectBlocking(3, TimeUnit.SECONDS)

                // 连接失败
                if (!flag) {
                    client = null
                    log.error("server connection fail!")

                    if (isCustom) AlertUtils.error(
                        "自定义服务器连接失败！请检查服务器地址是否正确，或联系服务器作者！",
                        primaryStage
                    ) else AlertUtils.error("默认服务器连接失败！请检查更新或联系作者！", primaryStage)
                    return@Runnable
                }

                // 连接成功
                isConnection = true
                Platform.runLater { flushUI(UIState.CONNECTED_SERVER) }

                // 添加观察者
                client!!.addObserver(object : ClientObserver {
                    override fun onAction(code: Int) {
                        doAction(code)
                    }

                    override fun onString(code: Int, content: String) {
                        doString(code, content)
                    }

                    override fun onMovie(message: MovieMessage) {
                        doMovie(message)
                    }

                    override fun onError(exception: Exception) {
                        doError(exception)
                    }
                })
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, "ConnectServerThread").start()
    }

    // 连接自定义服务器
    private fun connectCustomServer() {
        // 1. 创建对话框
        val inputDialog = TextInputDialog()
        inputDialog.editor.text = ClientConstants.EXAMPLE_URL
        inputDialog.title = "连接服务器"
        inputDialog.headerText = "输入服务器地址，例如：" + ClientConstants.EXAMPLE_URL
        inputDialog.contentText = "输入服务器地址："

        // 2. 设置主窗口
        inputDialog.initModality(Modality.WINDOW_MODAL)
        inputDialog.initOwner(primaryStage)


        // 3. 显示窗口
        val result = inputDialog.showAndWait()

        // 4. 点击确定后发送消息
        if (result.isEmpty) return

        // 判断地址是否正确
        val url = result.get()
        log.info("custom server url = {}", url)
        if (!url.matches(ClientConstants.URL_REG.toRegex())) {
            AlertUtils.error("地址格式不正确！", primaryStage)
            return
        }
        isCustom = true
        connectServer(url)
    }

    // 处理 ActionMessage
    private fun doAction(code: Int) {
        when (code) {
            // 另一半下线
            ActionCode.OFFLINE -> {
                // 解除绑定
                isBind = false
                // 更新 UI
                Platform.runLater { flushUI(UIState.UN_BIND) }
                AlertUtils.information("另一半断开连接！", primaryStage)
                log.info("与另一半解除绑定！")
            }

            ActionCode.UNBIND -> {
                // 解除绑定
                isBind = false
                // 更新 UI
                Platform.runLater { flushUI(UIState.UN_BIND) }
                AlertUtils.information("另一半解除绑定！", primaryStage)
                log.info("与另一半解除绑定！")
            }

            ActionCode.HEARTBEAT -> {
                log.debug("server heartbeat")

            }
        }
    }

    // 处理字符消息
    private fun doString(code: Int, content: String) {
        // 绑定
        when (code) {
            ActionCode.BIND -> {
                // 将客户端设置为已绑定
                isBind = true
                // 更新 UI
                Platform.runLater {

                    // 设置对方的星星号
                    friendInput.text = content
                    // 更换连接图标为断开连接图标
                    bindImage.image = breakIcon
                    // 将播放/暂停按钮设置为蓝色
                    if (isPlaying) {
                        playImage.image = pauseBlueIcon
                    } else {
                        playImage.image = playBlueIcon
                    }
                    // 解禁同步图标
                    syncImage.image = syncBlueIcon
                    if (player.media().isValid) {
                        syncPane.isDisable = false
                        syncPane.opacity = 1.0
                    }
                    // 转移焦点
                    volumeSlider.requestFocus()
                }
                log.info("与 {} 绑定成功！", content)
            }

            ActionCode.CONNECTED -> {
                id = content
                log.info("server connected, id = {}", id)
                Platform.runLater { selfNumberLabel.text = content }
            }

            ActionCode.BULLET_SCREEN -> Platform.runLater { showBulletScreen(content, friendColor) }
            ActionCode.SERVER_RESPONSE -> Platform.runLater { AlertUtils.error(content, primaryStage) }
        }
    }

    // 处理电影消息
    private fun doMovie(message: MovieMessage) {
        // 如果视频没有加载，则不做处理
        if (!player.media().isValid) {
            log.info("视频尚未加载！")
            return
        }
        when (message.actionCode) {
            ActionCode.MOVIE_PLAY -> {
                player.controls().play()
                playImage.image = pauseBlueIcon
            }

            ActionCode.MOVIE_PAUSE -> {
                player.controls().pause()
                playImage.image = playBlueIcon
            }

            ActionCode.MOVIE_STOP -> {
                player.controls().stop()
                playImage.image = playBlueIcon
            }

            ActionCode.MOVIE_SYNC -> {
                player.controls().setTime(message.milliseconds)
//                player.controls().setRate(message.rate)
                rateSpinner.valueFactory.value = message.rate.toDouble()
                player.controls().play()
                playImage.image = pauseBlueIcon
                log.info(
                    "同步播放 -- 进度：{}，倍速：{}",
                    getText(message.milliseconds),
                    message.rate
                )
            }
        }
    }

    private fun doError(ex: Exception) {
        AlertUtils.error("发生异常", ex.message, primaryStage)
    }

    // 刷新 UI
    private fun flushUI(code: Int) {
        when (code) {
            UIState.UN_BIND -> {
                // 清空另一半的星星号
                friendInput.text = ""
                // 更换断开连接图标为连接图标
                bindImage.image = linkIcon
                // 将播放/暂停按钮设置为黑色
                if (isPlaying) {
                    playImage.image = pauseBlackIcon
                } else {
                    playImage.image = playBlackIcon
                }
                // 禁止同步图标
                syncPane.isDisable = true
                syncPane.opacity = 0.6
                syncImage.image = syncBlackIcon
            }

            UIState.OPEN_VIDEO -> {
                playPane.isDisable = false
                playPane.opacity = 1.0
                videoSlider.isDisable = false
                volumeSlider.isDisable = false
                rateSpinner.isDisable = false
                bulletScreenInput.isDisable = false
                if (isBind()) {
                    syncPane.isDisable = false
                    syncPane.opacity = 1.0
                    syncImage.image = syncBlueIcon
                }
            }

            UIState.CLOSE_VIDEO -> {
                playPane.isDisable = true
                playPane.opacity = 0.6
                videoSlider.isDisable = true
                volumeSlider.isDisable = true
                rateSpinner.isDisable = true
                bulletScreenInput.isDisable = true
                // 禁止同步图标
                syncPane.isDisable = true
                syncPane.opacity = 0.6
                syncImage.image = syncBlackIcon
            }

            UIState.CONNECTED_SERVER -> {
                bindPane.isDisable = false
                bindPane.opacity = 1.0
                closeServerItem.isDisable = false
                connectDefaultServerItem.isDisable = true
                connectCustomServerItem.isDisable = true
            }

            UIState.CLOSED_SERVER -> {
                flushUI(UIState.UN_BIND)
                bindPane.isDisable = true
                bindPane.opacity = 0.6
                closeServerItem.isDisable = true
                connectDefaultServerItem.isDisable = false
                connectCustomServerItem.isDisable = false
            }
        }
    }

    private fun isBind(): Boolean {
        return client != null && isBind
    }

    private val isPlaying: Boolean
        get() = player.media().isValid && player.status().isPlaying

    private fun hasMedia(): Boolean {
        return player.media().isValid
    }

    // 播放
    private fun play() {
        if (isBind()) {
            client!!.send(MovieMessage.MOVIE_PLAY.toJson())
            playImage.image = pauseBlueIcon
        } else {
            player.controls().play()
            playImage.image = pauseBlackIcon
        }
    }

    // 暂停播放
    fun pause() {
        if (isBind()) {
            client!!.send(MovieMessage.MOVIE_PAUSE.toJson())
            playImage.image = playBlueIcon
        } else {
            player.controls().pause()
            playImage.image = playBlackIcon
        }
    }

    // 根据视频的状态播放或者暂停
    fun playOrPause(event: MouseEvent?) {
        if ((event != null) && (event.button != MouseButton.PRIMARY)) {
            return
        }
        if (isPlaying) {
            pause()
        } else {
            play()
        }
    }

    // 打开视频
    private fun openVideo() {
        // 创建文件选择器
        val fileChooser = FileChooser()

        // 添加过滤器
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("视频文件", VideoConstants.VIDEO_FILTER))

        // 打开文件选择器，返回选择的文件
        val path = fileChooser.showOpenDialog(primaryStage).path ?: return

        // 设置标题
        Platform.runLater {
            val tmp = path.split("\\")
            primaryStage.title = ClientConstants.TITLE + " - " + tmp[tmp.size - 1]
        }

        // 加载视频
        loadVideo(path)
    }

    private fun loadVideo(uri: String) {
        log.info("load video : {}", uri)
        player.media().prepare(uri)
        player.media().parsing().parse()
    }

    private fun showBulletScreen(content: String, color: Color) {
        val isFullScreen = primaryStage.isFullScreen
        val screen = Screen.getPrimary()
        // 设置 Text
        val text = Text(content)
        text.font = Font(16.0)
        text.fill = color
        // 获取宽高
        val textWidth = text.layoutBounds.width
        val textHeight = text.layoutBounds.height
        // 设置 y 坐标
        text.y = 25 + (Random().nextInt(5) + 1) * textHeight
        root.children.add(text)
        val width = videoImageView.fitWidth
        val layoutX = videoImageView.layoutX
        //        log.debug("width = {}, layoutX = {}", width, layoutX);

        // 创建动画
        val startX = if (isFullScreen) screen.bounds.width else width + layoutX - textWidth
        val time = if (isFullScreen) Duration.seconds(10.0) else Duration.seconds(6.0)
        val kf1 = KeyFrame(
            Duration.ZERO,
            "start",
            { },
            KeyValue(text.xProperty(), startX)
        )
        val kf2 = KeyFrame(
            time,
            "end",
            { root.children.remove(text) },
            KeyValue(text.xProperty(), 0 - textWidth)
        )
        val timeline = Timeline()
        timeline.keyFrames.addAll(kf1, kf2)
        timeline.play()
    }

    // 关闭软件
    fun close() {
        // 关闭服务器连接
        if (client != null) {
            try {
                client!!.closeBlocking()
                log.info("关闭服务器连接")
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            }
        }
        // 销毁媒体
        player.controls().stop()
        player.release()
        factory.release()
        log.info("销毁视频")
        // 关闭任务
        countTask.stop()
        log.debug("结束计时任务")
        log.info("客户端关闭")
    }


    // 检查更新
    private fun checkUpdate(isAutoCheck: Boolean) {
        log.debug("check update...")

        // 创建 HTTP 请求
        val request = HttpRequest.newBuilder()
            .uri(URI.create(ClientConstants.CHECK_UPDATE_URL))
            .header("Content-Type", "application/json")
            .timeout(java.time.Duration.ofSeconds(5))
            .build()

        // 发起请求
        val httpClient = HttpClient.newHttpClient()
        httpClient
            .sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply { response: HttpResponse<String?> ->
                // 如果状态码不是200，抛出Http异常
                if (response.statusCode() != 200) {
                    throw HttpException(response.statusCode())
                }
                response.body()
            }.thenApply { body: String? ->
                val mapper = ObjectMapper()
                val res = mapper.readTree(body)
                VersionInfo(res.get("tag_name").asText(), res.get("body").asText())
            }.thenAccept {
                handleUpdate(it, isAutoCheck)
            }
            .exceptionally { throwable: Throwable ->
                // 捕获异常
                val ex = throwable.cause
                throwable.printStackTrace()
                log.warn("{} : {}", ex!!.javaClass, ex.message)

                // 鉴别异常类型
                when (ex) {
                    is ResultException -> AlertUtils.error("请求出错", ex.message, primaryStage)
                    is HttpConnectTimeoutException -> checkUpdate(false)
                    is HttpTimeoutException -> AlertUtils.error("请求超时", "网络请求超时，请稍后再试。", primaryStage)
                    else -> {
                        val content =
                            "检查更新发生错误，请前往Github或者Gitee手动检查更新，或者联系作者。\n" +
                                    "Github: https://github.com/tonydon140/star-syncplayer \n" +
                                    "Gitee: https://gitee.com/shuilanjiao/star-syncplayer"
                        AlertUtils.error("未知错误", content, primaryStage)
                    }
                }
                null
            }
    }

    // 判断 latest 是否大于 current，若大于返回 true
    private fun judge(latest: String): Boolean {
        val currentList = ClientConstants.VERSION.substring(1).split(".")
        val latestList = latest.substring(1).split(".")
        for (i in currentList.indices) {
            return if (latestList[i].toInt() > currentList[i].toInt())
                true
            else if (latestList[i].toInt() == currentList[i].toInt()) {
                continue
            } else
                false
        }
        return false
    }

    // 处理请求结果
    private fun handleUpdate(versionInfo: VersionInfo, isAutoCheck: Boolean) {
        // 判断是否有新版本，不接受Beta版本更新
        if (!judge(versionInfo.version)) {
            if (!isAutoCheck)
                AlertUtils.information("当前版本" + ClientConstants.VERSION + "已是最新版本！", "", primaryStage)
            return
        }

        Platform.runLater {
            val alert = Alert(Alert.AlertType.CONFIRMATION)
            alert.headerText = "检测到新版本：${versionInfo.version}！当前版本可能无法正常使用！点击确定前往下载！"
            alert.contentText = versionInfo.info
            alert.initModality(Modality.WINDOW_MODAL)
            alert.initOwner(primaryStage)
            val optional = alert.showAndWait()
            optional
                .filter { buttonType: ButtonType -> buttonType == ButtonType.OK }
                .ifPresent { hostServices!!.showDocument(ClientConstants.LATEST_URL) }
        }
    }


    /**
     * 打开软件官网
     */
    private fun about() {
        hostServices!!.showDocument(ClientConstants.ABOUT_URL)
    }
}
