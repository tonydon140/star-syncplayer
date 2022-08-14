package top.tonydon;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.tonydon.client.WebClient;
import top.tonydon.constant.ClientConstants;
import top.tonydon.entity.ProjectVersion;
import top.tonydon.entity.VersionResult;
import top.tonydon.message.ActionCode;
import top.tonydon.message.Message;
import top.tonydon.message.common.BindMessage;
import top.tonydon.message.common.BulletScreenMessage;
import top.tonydon.message.common.MovieMessage;
import top.tonydon.message.common.Notification;
import top.tonydon.message.server.ServerConnectMessage;
import top.tonydon.task.CountTask;
import top.tonydon.util.AlertUtils;
import top.tonydon.util.JSONUtils;
import top.tonydon.util.VideoDuration;
import top.tonydon.util.observer.ClientObserver;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class ClientController {
    private final Logger log = LoggerFactory.getLogger(ClientController.class);

    /**************************************************************************
     * <p>
     * FXML组件
     *
     **************************************************************************/

    @FXML
    public Label selfNumberLabel;
    public Label targetNumberLabel;
    public AnchorPane root;
    public Button bindButton;
    public MediaView mediaView;
    public Slider videoSlider;
    public Slider volumeSlider;
    public Label volumeLabel;
    public VBox togetherVBox;
    public Label videoDurationLabel;
    public ImageView playOrPauseImageView;
    public AnchorPane playOrPausePane;
    public Button copyNumberButton;
    public Button connectServerButton;
    public ProgressIndicator connectProgress;
    public Spinner<Number> rateSpinner;
    public VBox controllerBox;
    public TextField bsTextField;
    public HBox leftCB;
    public HBox rightCB;

    /**************************************************************************
     *
     * 成员变量
     *
     **************************************************************************/

    private WebClient client;
    private Stage primaryStage;
    private Scene primaryScene;
    private VideoDuration videoDuration;
    private Robot robot;
    private CountTask countTask;

    private final Color selfColor = Color.WHITESMOKE;
    private final Color targetColor = Color.web("#FFFF00");

    private final Image PLAY_ICON;
    private final Image PAUSE_ICON;

    private boolean mouse;
    private boolean isMouseBottom;

    private HttpClient httpClient;
    private HttpRequest httpRequest;
    private HostServices hostServices;

    /**************************************************************************
     * <p>
     * 构造方法，进行成员变量的初始化
     *
     **************************************************************************/
    public ClientController() {
        // Icon
        this.PLAY_ICON = new Image(Objects.requireNonNull(getClass().getResource("icon/播放.png")).toString());
        this.PAUSE_ICON = new Image(Objects.requireNonNull(getClass().getResource("icon/暂停.png")).toString());

        this.videoDuration = new VideoDuration();
        this.robot = new Robot();
        this.countTask = new CountTask(TimeUnit.SECONDS, 1);

        // HTTP
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(java.time.Duration.ofSeconds(10))
                .build();
        this.httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(ClientConstants.CHECK_UPDATE_URL))
                .header("Content-Type", "application/json")
                .timeout(java.time.Duration.ofSeconds(10))
                .build();
        log.debug("this = {}", this);
    }

    /**************************************************************************
     * <p>
     * 初始化方法，此时组件还没有加载，可以进行一些事件的绑定
     *
     **************************************************************************/
    @FXML
    private void initialize() {
        // 1. 进度条的监听事件
        videoSlider.setOnMousePressed(event -> mouse = true);
        videoSlider.setOnMouseReleased(event -> {
            mouse = false;
            Duration duration = Duration.seconds(videoSlider.getValue());
            mediaView.getMediaPlayer().seek(duration);
            videoDuration.setCurrentDuration(duration);
            videoDurationLabel.setText(videoDuration.toString());
        });

        // 3. 音量进度条和 Label 绑定
        volumeSlider.valueProperty().addListener((observableValue, number, t1) -> {
            String label = (int) (t1.doubleValue() * 100) + "%";
            volumeLabel.setText(label);
        });

        // 弹幕长度限制
        bsTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 20) bsTextField.setText(oldValue);
        });
    }


    /**************************************************************************
     * <p>
     * 页面加载完毕后执行的初始化操作
     *
     **************************************************************************/
    public void init(Application application) {
        // 获得主场景
        this.primaryScene = root.getScene();
        // 获得主窗口
        this.primaryStage = (Stage) this.primaryScene.getWindow();
        // 设置播放按钮图标
        playOrPauseImageView.setImage(this.PLAY_ICON);
        // 获取 host
        this.hostServices = application.getHostServices();

        // 2. 获取主屏幕尺寸
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();


        // 2. 添加全屏效果
        primaryStage.fullScreenProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                mediaView.setFitWidth(screenWidth);
                mediaView.setFitHeight(screenHeight);

                // 设置控件的尺寸
                controllerBox.setPrefWidth(screenWidth);
                controllerBox.setOpacity(0.8);
                leftCB.setPrefWidth(screenWidth * 0.3);
                rightCB.setPrefWidth(screenWidth * 0.7);
            } else {
                // 恢复视频窗口尺寸
                mediaView.setFitWidth(ClientConstants.MOVIE_WIDTH);
                mediaView.setFitHeight(ClientConstants.MOVIE_HEIGHT);

                controllerBox.setOpacity(1);
                controllerBox.setVisible(true);
                controllerBox.setPrefWidth(ClientConstants.MOVIE_WIDTH);
                leftCB.setPrefWidth(ClientConstants.MOVIE_WIDTH * 0.3);
                rightCB.setPrefWidth(ClientConstants.MOVIE_WIDTH * 0.7);

                // 恢复鼠标
                primaryScene.setCursor(Cursor.DEFAULT);
            }
        });

        // 启动任务
        countTask.start();
        countTask.addObserver((old, cur) -> {
            // 当视频正在播放时，每搁一段时间移动一下鼠标
            boolean isPlay = cur % ClientConstants.MOUSE_MOVE_INTERVAL == 0
                    && mediaView.getMediaPlayer() != null
                    && mediaView.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING;
            if (isPlay) {
                Platform.runLater(() -> {
                    double mouseX = robot.getMouseX();
                    double mouseY = robot.getMouseY();
                    robot.mouseMove(mouseX + 1, mouseY + 1);
                    robot.mouseMove(mouseX, mouseY);
                    log.debug("mouse move");
                });
            }

            // 全屏状态下、鼠标不在控制栏附近、鼠标指针没有隐藏，则每隔大约两秒隐藏鼠标
            if (primaryStage.isFullScreen() && !isMouseBottom && primaryScene.getCursor() != Cursor.NONE && cur % 2 == 0)
                primaryScene.setCursor(Cursor.NONE);

//            log.debug("count = {}", cur);
        });

        // 监听全屏鼠标移动
        mediaView.setOnMouseMoved(event -> {
            // 不是全屏状态下直接返回
            if (!primaryStage.isFullScreen()) return;

            // 显示鼠标
            primaryScene.setCursor(Cursor.DEFAULT);

            // 鼠标是否在控制栏附近
            isMouseBottom = event.getScreenY() > (screenHeight - 80);
            controllerBox.setVisible(isMouseBottom);
        });
    }


    /**************************************************************************
     * <p>
     * 普通成员方法
     *
     **************************************************************************/

    public void play(MediaPlayer player) {
        player.play();
        playOrPauseImageView.setImage(PAUSE_ICON);
    }

    public void pause(MediaPlayer player) {
        player.pause();
        playOrPauseImageView.setImage(PLAY_ICON);
    }

    public void stop(MediaPlayer player) {
        player.stop();
        playOrPauseImageView.setImage(PLAY_ICON);
    }

    // 加载视频之后，解禁组件禁用
    private void enable() {
        // client 不为空且已绑定的情况下，解禁同步系列控件
        if (client != null && client.isBind()) togetherVBox.setDisable(false);
        // 解禁视频播放控件
        playOrPausePane.setDisable(false);
        playOrPausePane.setOpacity(1);
        videoSlider.setDisable(false);
        volumeSlider.setDisable(false);
        rateSpinner.setDisable(false);
        bsTextField.setDisable(false);
    }

    /**
     * 视频销毁之后，禁用组件
     */
    private void disable() {
        playOrPausePane.setDisable(true);
        playOrPausePane.setOpacity(0.5);
        videoSlider.setDisable(true);
        volumeSlider.setDisable(true);
    }

    /**
     * 解除绑定
     */
    public void unbindNumber() {
        // 1. 创建确认信息
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认消息");
        alert.setHeaderText("确定要解绑吗");

        // 2. 设置主窗口
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner(primaryStage);

        // 4. 显示窗口
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // 发送解除绑定消息
            client.send(new Notification(ActionCode.UNBIND).toJson());

            togetherVBox.setDisable(true);      // 禁用一起播放按钮组
            targetNumberLabel.setText("");      // 清空另一半的星星号
            bindButton.setText("绑定他/她");     // 重置绑定按钮名称
        }
    }


    /**
     * 绑定另一个人
     */
    public void bindNumber() {
        // 1. 创建对话框
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("绑定他/她");
        inputDialog.setHeaderText("绑定他/她");
        inputDialog.setContentText("请输入他/她的星星号：");

        // 设置输入框限制
        TextField editor = inputDialog.getEditor();
        editor.textProperty().addListener((observable, oldValue, newValue) -> {
            // 限制长度为 8 个数字
            if (newValue.length() > 8) editor.setText(oldValue);
            // 限制输入只为数字
            boolean isNumber = true;
            for (char ch : newValue.toCharArray()) {
                if (ch < '0' || ch > '9') {
                    isNumber = false;
                    break;
                }
            }
            if (!isNumber) editor.setText(oldValue);
        });

        // 2. 设置主窗口
        inputDialog.initModality(Modality.WINDOW_MODAL);
        inputDialog.initOwner(primaryStage);

        // 3. 显示窗口
        Optional<String> result = inputDialog.showAndWait();

        // 4. 点击确定后发送消息
        result.ifPresent(number -> {
            client.send(new BindMessage(number).toJson());
        });
    }

    /**
     * 软件关闭时执行，关闭服务器连接、销毁媒体
     */
    public void close() {
        // 关闭服务器连接
        if (client != null) {
            try {
                client.closeBlocking();
                log.debug("关闭服务器连接");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // 销毁媒体
        if (mediaView.getMediaPlayer() != null) {
            mediaView.getMediaPlayer().dispose();
            log.debug("销毁媒体");
        }
        // 关闭任务
        countTask.stop();
        log.debug("结束任务");
    }

    /**
     * 显示弹幕
     *
     * @param content 弹幕文本
     */
    private void showBulletScreen(String content, Color color) {
        boolean isFullScreen = primaryStage.isFullScreen();
        Screen screen = Screen.getPrimary();
        // 设置 Text
        Text text = new Text(content);
        text.setFont(new Font(16));
        text.setFill(color);
        // 获取宽高
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();
        // 设置 y 坐标
        text.setY((new Random().nextInt(4) + 1) * textHeight);
        root.getChildren().add(text);

        // 创建动画
        double startX = isFullScreen ? screen.getBounds().getWidth() : 960 - textWidth;
        Duration time = isFullScreen ? Duration.seconds(10) : Duration.seconds(6);
        KeyFrame kf1 = new KeyFrame(
                Duration.ZERO,
                "start",
                event -> {
                },
                new KeyValue(text.xProperty(), startX));
        KeyFrame kf2 = new KeyFrame(
                time,
                "end",
                event -> root.getChildren().remove(text),
                new KeyValue(text.xProperty(), 0 - textWidth));

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(kf1, kf2);
        timeline.play();
    }


    /**************************************************************************
     *
     * 组件方法
     *
     **************************************************************************/
    @FXML
    public void bindButtonAction() {
        if (client.isBind()) unbindNumber();
        else bindNumber();
    }

    @FXML
    public void selectVideo() {
        // 创建文件选择器
        FileChooser fileChooser = new FileChooser();
        // 设置过滤器，第一个参数是描述文本，第二个参数是过滤规则
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("mp4/flv", "*.mp4", "*.flv");
        fileChooser.getExtensionFilters().add(filter);          // 添加过滤器
        File file = fileChooser.showOpenDialog(primaryStage);   // 打开文件选择器，返回选择的文件

        // 如果未选择文件，直接返回
        if (file == null) return;

        // 如果之前已经选择了视频，则销毁之前的视频
        if (mediaView.getMediaPlayer() != null) {
            log.debug("销毁媒体");
            mediaView.getMediaPlayer().dispose();
        }

        // 将文件转为 uri 路径，加载媒体视频
        String uri = "file:" + file.toPath().toUri().getPath();
        Media media = new Media(uri);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        // 3. 媒体加载完毕后
        mediaPlayer.setOnReady(() -> {
            // 1. 按钮解禁
            enable();

            // 2. 设置进度条
            Duration totalDuration = mediaPlayer.getTotalDuration();
            videoSlider.setMax(totalDuration.toSeconds());
            videoSlider.setVisible(true);
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                if (!mouse) {
                    videoSlider.setValue(newValue.toSeconds());
                    videoDuration.setCurrentDuration(newValue);
                    videoDurationLabel.setText(videoDuration.toString());

                    // 当播放结束时暂停
                    double temp = totalDuration.toMillis() - newValue.toMillis();
                    if (temp <= 120) {
                        pause(mediaPlayer);
//                        log.debug("pause");
                    }
                }
            });

            // 绑定音量进度条
            mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty());
            // 绑定倍速
            mediaPlayer.rateProperty().bindBidirectional(rateSpinner.getValueFactory().valueProperty());

            // 3. 设置视频点击播放/暂停
            mediaView.setOnMouseClicked(event -> playOrPause(null));

            // 设置进度显示
            videoDuration.setTotalDuration(totalDuration);
            videoDurationLabel.setText(videoDuration.toString());

            log.debug("媒体加载完毕");
        });
    }

    /**
     * 播放或者暂停视频
     *
     * @param event 点击事件
     */
    @FXML
    public void playOrPause(MouseEvent event) {
        if (event == null || event.getButton() == MouseButton.PRIMARY) {
            MediaPlayer player = mediaView.getMediaPlayer();
            MediaPlayer.Status status = player.getStatus();

            if (status == MediaPlayer.Status.PLAYING) {
                pause(player);
            } else if (status == MediaPlayer.Status.STOPPED ||
                    status == MediaPlayer.Status.PAUSED ||
                    status == MediaPlayer.Status.READY) {
                play(player);
            }
        }
    }

    @FXML
    public void fullScreen(MouseEvent mouseEvent) {
        // 鼠标左键点击有效
        if (mouseEvent.getButton() != MouseButton.PRIMARY) return;

        // 设置全屏
        primaryStage.setFullScreen(!primaryStage.isFullScreen());
    }

    @FXML
    public void togetherPlay(ActionEvent actionEvent) {
        Message message = new MovieMessage(ActionCode.MOVIE_PLAY);
        client.send(message.toJson());
    }

    @FXML
    public void togetherPause(ActionEvent actionEvent) {
        Message message = new MovieMessage(ActionCode.MOVIE_PAUSE);
        client.send(message.toJson());
    }

    @FXML
    public void togetherStop() {
        Message message = new MovieMessage(ActionCode.MOVIE_STOP);
        client.send(message.toJson());
    }

    // 复制星星号
    @FXML
    public void copyNumber() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(client.getSelfNumber());
        clipboard.setContent(content);
    }

    /**
     * 连接服务器
     */
    @FXML
    public void connectServer() {
        // 关闭连接
        if (client != null) {
            try {
                client.closeBlocking();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            client = null;

            // 禁用一起播放按钮组
            togetherVBox.setDisable(true);
            // 重置星星号组件
            targetNumberLabel.setText("");      // 清空另一半的星星号
            bindButton.setText("绑定他/她");     // 重置绑定按钮名称
            bindButton.setDisable(true);
            copyNumberButton.setDisable(true);
            connectServerButton.setText("连接服务器");
            selfNumberLabel.setText("");
            return;
        }

        // 1. 创建对话框
        TextInputDialog inputDialog = new TextInputDialog();
        // 默认远程服务器
        inputDialog.getEditor().setText(ClientConstants.DEFAULT_URL);
        // 本地服务器
//        inputDialog.getEditor().setText(ClientConstants.LOCAL_URL);
        inputDialog.setTitle("连接服务器");
        inputDialog.setHeaderText("输入服务器地址，例如：" + ClientConstants.EXAMPLE_URL);
        inputDialog.setContentText("输入服务器地址：");

        // 2. 设置主窗口
        inputDialog.initModality(Modality.WINDOW_MODAL);
        inputDialog.initOwner(primaryStage);


        // 3. 显示窗口
        Optional<String> result = inputDialog.showAndWait();

        // 4. 点击确定后发送消息
        if (result.isEmpty()) return;

        // 判断地址是否正确
        String url = result.get();
        log.info("url = {}", url);
        if (!url.matches(ClientConstants.URL_REG)) {
            AlertUtils.error("地址格式不正确！", "", this.primaryStage);
            return;
        }

        connectProgress.setVisible(true);
        // 开启新的线程连接服务器
        new Thread(() -> {
            try {
                log.info("正在连接服务器...");
                client = new WebClient(new URI(url));
                boolean flag = client.connectBlocking();
                if (!flag) {
                    AlertUtils.error("服务器连接失败！请重试！", "", this.primaryStage);
                    log.error("连接服务器失败！");
                    return;
                }

                Platform.runLater(() -> connectProgress.setVisible(false));

                // 连接成功之后解禁组件
                copyNumberButton.setDisable(false);
                bindButton.setDisable(false);
                Platform.runLater(() -> connectServerButton.setText("断开连接"));

                // 添加观察者
                client.addObserver(new ClientObserver() {
                    @Override
                    public void onConnected(ServerConnectMessage message) {
                        Platform.runLater(() -> selfNumberLabel.setText(message.getNumber()));
                        AlertUtils.information("服务器连接成功！", "您的星星号：" + message.getNumber(), primaryStage);
                    }

                    @Override
                    public void onMovie(MovieMessage message) {
                        MediaPlayer player = mediaView.getMediaPlayer();
                        if (message.getActionCode() == ActionCode.MOVIE_PLAY) {
                            play(player);
                        } else if (message.getActionCode() == ActionCode.MOVIE_PAUSE) {
                            pause(player);
                        } else if (message.getActionCode() == ActionCode.MOVIE_STOP) {
                            stop(player);
                        } else if (message.getActionCode() == ActionCode.MOVIE_SYNC) {
                            player.seek(Duration.seconds(message.getSeconds()));
                            player.setRate(message.getRate());
                            play(player);
                        }
                    }

                    @Override
                    public void onBind(BindMessage message) {
                        // 将客户端设置为已绑定，并更新 ui
                        client.setBind(true);
                        Platform.runLater(() -> {
                            // 如果视频已经选择好，解禁按钮
                            if (mediaView.getMediaPlayer() != null)
                                togetherVBox.setDisable(false);
                            targetNumberLabel.setText(message.getTargetNumber());
                            bindButton.setText("解除绑定");
                        });
                    }

                    @Override
                    public void onUnbind() {
                        // 解除绑定
                        client.setBind(false);
                        Platform.runLater(() -> {
                            togetherVBox.setDisable(true); // 禁用一起播放按钮组
                            targetNumberLabel.setText("");      // 清空另一半的星星号
                            bindButton.setText("绑定他/她");     // 重置绑定按钮名称
                        });
                        AlertUtils.information("另一半解除绑定！", "", primaryStage);
                    }

                    @Override
                    public void onOffline() {
                        onUnbind();
                        AlertUtils.information("另一半断开连接！", "", primaryStage);
                    }

                    @Override
                    public void onBulletScreen(BulletScreenMessage message) {
                        Platform.runLater(() -> showBulletScreen(message.getContent(), targetColor));
                    }
                });
            } catch (URISyntaxException | InterruptedException e) {
                e.printStackTrace();
            }
        }, "ConnectServerThread").start();
    }


    /**
     * 同步播放
     */
    @FXML
    public void synchronization() {
        MediaPlayer player = mediaView.getMediaPlayer();
        double seconds = player.getCurrentTime().toSeconds();
        double rate = player.getRate();
        Message message = new MovieMessage(ActionCode.MOVIE_SYNC, seconds, rate);
        client.send(message.toJson());
    }

    /**
     * 发送弹幕
     */
    @FXML
    public void sendBulletScreen() {
        // 获取内容
        String content = bsTextField.getText();
        if (content.length() == 0) return;
        bsTextField.setText("");
        // 显示弹幕
        showBulletScreen(content, selfColor);
        // 如果已经建立了绑定，则发送弹幕消息
        if (client != null && client.isBind()) {
            client.send(new BulletScreenMessage(content).toJson());
        }
    }

    /**
     * 检查软件更新
     */
    @FXML
    public void checkUpdate() {
        log.debug("check update...");
        this.httpClient.sendAsync(this.httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept((body) -> {
                    // 解析 JSON
                    VersionResult result = JSONUtils.parse(body, VersionResult.class);
                    ProjectVersion version = result.getData();

                    // 有新版本
                    if (version.getVersionNumber() > ClientConstants.VERSION_NUMBER) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setHeaderText("发现新版本" + version + "！是否前往下载？");
                            alert.setContentText(version.getDescription());
                            alert.initModality(Modality.WINDOW_MODAL);
                            alert.initOwner(primaryStage);
                            alert.showAndWait()
                                    .filter(buttonType -> buttonType == ButtonType.OK)
                                    .ifPresent(response -> this.hostServices.showDocument(ClientConstants.LATEST_URL));
                        });
                    } else {
                        AlertUtils.information("当前版本" + ClientConstants.VERSION + "已是最新版本！", "", primaryStage);
                    }
                });
    }

    /**
     * 打开软件官网
     */
    public void about() {
        this.hostServices.showDocument(ClientConstants.ABOUT_URL);
    }
}