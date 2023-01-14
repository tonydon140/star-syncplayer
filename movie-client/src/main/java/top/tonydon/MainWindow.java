package top.tonydon;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
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
import top.tonydon.constant.UI;
import top.tonydon.entity.ProjectVersion;
import top.tonydon.entity.VersionResult;
import top.tonydon.exception.HttpException;
import top.tonydon.exception.ResultException;
import top.tonydon.message.ActionCode;
import top.tonydon.message.common.BindMessage;
import top.tonydon.message.common.BulletScreenMessage;
import top.tonydon.message.common.MovieMessage;
import top.tonydon.message.common.Notification;
import top.tonydon.message.server.ServerConnectMessage;
import top.tonydon.message.server.ServerResponseMessage;
import top.tonydon.task.CountTask;
import top.tonydon.util.AlertUtils;
import top.tonydon.util.DurationUtils;
import top.tonydon.util.JSONUtils;
import top.tonydon.util.URIUtils;
import top.tonydon.util.observer.ClientObserver;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.*;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainWindow {
    private final Logger log = LoggerFactory.getLogger(MainWindow.class);

    private Stage primaryStage;
    private Scene primaryScene;
    private HostServices hostServices;

    private final Image PLAY_BLACK_ICON;
    private final Image PLAY_BLUE_ICON;
    private final Image PAUSE_BLACK_ICON;
    private final Image PAUSE_BLUE_ICON;
    private final Image LOVE_ICON;
    private final Image LINK_ICON;
    private final Image BREAK_ICON;
    private final Image SYNC_BLACK_ICON;
    private final Image SYNC_BLUE_ICON;
    private final Image FULL_SCREEN_ICON;
    private final Image SOUND_OPEN_ICON;
    private final Image SOUND_CLOSE_ICON;
    private final Color SELF_COLOR = Color.WHITESMOKE;
    private final Color TARGET_COLOR = Color.web("#FFFF00");


    private WebClient client;
    private final Robot robot;
    private final CountTask countTask;

    private MenuBar menuBar;
    private AnchorPane root;
    private AnchorPane playPane;
    private AnchorPane syncPane;
    private VBox controlBox;
    private FlowPane videoPane;

    private ImageView playImage;
    private ImageView bindImage;
    private ImageView syncImage;
    private ImageView soundImage;

    private Label volumeLabel;
    private Label selfNumberLabel;
    private Label timeLabel;
    private MediaView mediaView;
    private Slider videoSlider;
    private Slider volumeSlider;
    private Spinner<Number> rateSpinner;
    private TextField bulletScreenInput;
    private TextField friendInput;

    private boolean mouse;
    private boolean isMouseBottom;
    private boolean isMute;

    public MainWindow(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.robot = new Robot();
        this.countTask = new CountTask(TimeUnit.SECONDS, 1);

        this.PLAY_BLACK_ICON = new Image(Objects.requireNonNull(getClass().getResource("icon/play_black.png")).toString());
        this.PLAY_BLUE_ICON = new Image(Objects.requireNonNull(getClass().getResource("icon/play_blue.png")).toString());
        this.PAUSE_BLACK_ICON = new Image(Objects.requireNonNull(getClass().getResource("icon/pause_black.png")).toString());
        this.PAUSE_BLUE_ICON = new Image(Objects.requireNonNull(getClass().getResource("icon/pause_blue.png")).toString());
        this.LOVE_ICON = new Image(Objects.requireNonNull(getClass().getResource("icon/love.png")).toString());
        this.LINK_ICON = new Image(Objects.requireNonNull(getClass().getResource("icon/link_black.png")).toString());
        this.BREAK_ICON = new Image(Objects.requireNonNull(getClass().getResource("icon/break_black.png")).toString());
        this.SYNC_BLUE_ICON = new Image(Objects.requireNonNull(getClass().getResource("icon/sync_blue.png")).toString());
        this.SYNC_BLACK_ICON = new Image(Objects.requireNonNull(getClass().getResource("icon/sync_black.png")).toString());
        this.FULL_SCREEN_ICON = new Image(Objects.requireNonNull(getClass().getResource("icon/full_screen.png")).toString());
        this.SOUND_OPEN_ICON = new Image(Objects.requireNonNull(getClass().getResource("icon/sound_open.png")).toString());
        this.SOUND_CLOSE_ICON = new Image(Objects.requireNonNull(getClass().getResource("icon/sound_close.png")).toString());
    }


    private void setListener() {
        root.widthProperty().addListener((observable, oldValue, newValue) -> updateWidth(newValue.doubleValue()));
        root.heightProperty().addListener((observable, oldValue, newValue) -> updateHeight(newValue.doubleValue()));
    }

    private void updateWidth(double width) {
        // 如果不是全屏，做变换
        if (!primaryStage.isFullScreen()) {
            double needHeight = width / 16 * 9;
            double currentHeight = root.getHeight() - 86;
            if (currentHeight >= needHeight) {
                mediaView.setFitHeight(needHeight);
                mediaView.setFitWidth(width);
            } else {
                double currentWidth = currentHeight / 9 * 16;
                mediaView.setFitHeight(currentHeight);
                mediaView.setFitWidth(currentWidth);
            }
        }
        // 菜单栏同步变化
        menuBar.setPrefWidth(width);
        // 控制栏同步变化
        controlBox.setPrefWidth(width);
        videoPane.setPrefWidth(width);
    }

    private void updateHeight(double height) {
        height = height - 86;
        if (!primaryStage.isFullScreen()) {
            double needWidth = height / 9 * 16;
            double currentWidth = root.getWidth();
            if (currentWidth >= needWidth) {
                mediaView.setFitHeight(height);
                mediaView.setFitWidth(needWidth);
            } else {
                double currentHeight = currentWidth / 16 * 9;
                mediaView.setFitHeight(currentHeight);
                mediaView.setFitWidth(currentWidth);
            }
        }
        videoPane.setPrefHeight(height);
    }

    public void init(Application application) {
        // 获得主场景
        this.primaryScene = root.getScene();
        // 获得主窗口
        this.primaryStage = (Stage) this.primaryScene.getWindow();
        // 获取 host
        this.hostServices = application.getHostServices();
        // 连接服务器
        connectServer();
        volumeSlider.requestFocus();

        // 获取主屏幕尺寸
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();

        // 添加全屏效果
        primaryStage.fullScreenProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                videoPane.setLayoutY(0);
                controlBox.setOpacity(0.8);
                mediaView.setFitHeight(screenHeight);
                mediaView.setFitWidth(screenWidth);
            } else {
                // 恢复视频窗口尺寸
                videoPane.setLayoutY(25);
                controlBox.setOpacity(1);
                controlBox.setVisible(true);
                // 恢复鼠标
                primaryScene.setCursor(Cursor.DEFAULT);
                // 恢复尺寸
                updateWidth(root.getWidth());
                updateHeight(root.getHeight());
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
        });

        // 监听全屏鼠标移动
        mediaView.setOnMouseMoved(event -> {
            // 不是全屏状态下直接返回
            if (!primaryStage.isFullScreen()) return;

            // 显示鼠标
            primaryScene.setCursor(Cursor.DEFAULT);

            // 鼠标是否在控制栏附近
            isMouseBottom = event.getScreenY() > (screenHeight - 80);
            controlBox.setVisible(isMouseBottom);
        });
    }

    public Parent load() {
        root = new AnchorPane();

        setMenuBar();
        setCenter();
        setBottom();
        setListener();

        return root;
    }

    private void setMenuBar() {
        menuBar = new MenuBar();
        menuBar.setPrefHeight(25);

        // 打开视频
        Menu openVideoMenu = new Menu();
        Label openVideoLabel = new Label("打开视频");
        openVideoMenu.setGraphic(openVideoLabel);
        openVideoLabel.setOnMouseClicked(event -> openVideo());

        Menu helpMenu = new Menu("帮助");
        MenuItem updateItem = new MenuItem("检查更新");
        updateItem.setOnAction(actionEvent -> checkUpdate());
        MenuItem aboutItem = new MenuItem("关于");
        aboutItem.setOnAction(actionEvent -> about());
        helpMenu.getItems().addAll(updateItem, aboutItem);

        menuBar.getMenus().add(openVideoMenu);
        menuBar.getMenus().add(helpMenu);
        root.getChildren().add(menuBar);
    }


    private void setCenter() {
        videoPane = new FlowPane();
        videoPane.setLayoutY(25);
        videoPane.setBackground(Background.fill(Color.rgb(16, 16, 16)));
        videoPane.setAlignment(Pos.CENTER);
        mediaView = new MediaView();
        videoPane.getChildren().add(mediaView);
        root.getChildren().add(videoPane);
    }


    private void setBottom() {
        controlBox = new VBox();
        AnchorPane.setBottomAnchor(controlBox, 0.0);
        controlBox.setPadding(new Insets(5, 0, 10, 0));
        controlBox.setBackground(Background.fill(Color.valueOf("f4f4f4")));

        // 设置进度条
        setVideoSlider(controlBox);
        // 设置底部控制栏
        setBottomControlBox(controlBox);

        root.getChildren().add(controlBox);
    }

    // 视频进度条
    private void setVideoSlider(VBox videoControlBox) {
        videoSlider = new Slider();
        videoSlider.setDisable(true);
        videoControlBox.getChildren().add(videoSlider);
        // 进度条的监听事件
        videoSlider.setOnMousePressed(event -> mouse = true);
        videoSlider.setOnMouseReleased(event -> {
            mouse = false;
            Duration duration = Duration.seconds(videoSlider.getValue());
            mediaView.getMediaPlayer().seek(duration);
            timeLabel.setText(DurationUtils.getText(duration));
        });
    }

    // 底部控制栏
    private void setBottomControlBox(VBox videoControlBox) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        gridPane.setHgap(10);

        setConnectBox(gridPane);
        setPlayOrPauseItem(gridPane);
        setSyncItem(gridPane);
        setTimeItem(gridPane);
        setBulletScreenItem(gridPane);
        setSoundItem(gridPane);
        setRateItem(gridPane);
        setFullScreen(gridPane);

        videoControlBox.getChildren().add(gridPane);
    }

    // 时间组件
    private void setTimeItem(GridPane gridPane) {
        timeLabel = new Label("0:00/0:00");
        timeLabel.setFont(new Font(15));
        GridPane.setMargin(timeLabel, new Insets(0, 0, 0, 10));
        gridPane.add(timeLabel, 0, 0);
    }

    // 绑定组件
    private void setConnectBox(GridPane gridPane) {
        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER);

        // 爱心图标
        ImageView loveImage = new ImageView(this.LOVE_ICON);

        // 自己的星星号
        selfNumberLabel = new Label();
        selfNumberLabel.setTooltip(new Tooltip("星星号，点击复制"));
        selfNumberLabel.setCursor(Cursor.HAND);
        selfNumberLabel.setOnMouseClicked(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(client.getSelfNumber());
            clipboard.setContent(content);
        });

        // 朋友的星星号
        friendInput = new TextField();
        friendInput.setPrefWidth(95);
        friendInput.setPromptText("她/他的星星号");
        // 现在只能输入8位数字
        friendInput.textProperty().addListener((observable, oldValue, newValue) -> {
            // 限制长度为 8 个数字
            if (newValue.length() > 8) friendInput.setText(oldValue);
            // 限制输入只为数字
            boolean isNumber = true;
            for (char ch : newValue.toCharArray()) {
                if (ch < '0' || ch > '9') {
                    isNumber = false;
                    break;
                }
            }
            if (!isNumber) friendInput.setText(oldValue);
        });

        // 绑定按钮
        AnchorPane linkPane = new AnchorPane();
        bindImage = new ImageView(this.LINK_ICON);
        linkPane.setCursor(Cursor.HAND);
        linkPane.getChildren().add(bindImage);
        // 点击事件，点击进行绑定
        linkPane.setOnMouseClicked(event -> {
            if (isBind()) {
                // 如果已经绑定了就解除绑定
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
                }
                flushUI(UI.UN_BIND);
            } else {
                String number = friendInput.getText();
                // 校验星星号
                if (number.length() != 8) {
                    AlertUtils.error("星星号必须是8位数字", "", primaryStage);
                    return;
                }
                client.send(new BindMessage(number).toJson());
            }
        });


        hBox.getChildren().add(loveImage);
        hBox.getChildren().add(selfNumberLabel);
        hBox.getChildren().add(friendInput);
        hBox.getChildren().add(linkPane);

        gridPane.add(hBox, 1, 0);
    }

    // 播放/暂停组件
    private void setPlayOrPauseItem(GridPane gridPane) {
        playPane = new AnchorPane();
        playPane.setDisable(true);
        playPane.setCursor(Cursor.HAND);
        playPane.setOpacity(0.6);
        playPane.setOnMouseClicked(this::playOrPause);
        playImage = new ImageView(this.PLAY_BLACK_ICON);
        playPane.getChildren().add(playImage);

        gridPane.add(playPane, 2, 0);
    }

    // 同步组件
    private void setSyncItem(GridPane gridPane) {
        syncPane = new AnchorPane();
        syncPane.setCursor(Cursor.HAND);
        syncPane.setDisable(true);
        syncPane.setOpacity(0.6);
        syncPane.setOnMouseClicked(event -> {
            MediaPlayer player = mediaView.getMediaPlayer();
            double seconds = player.getCurrentTime().toSeconds();
            double rate = player.getRate();
            client.send(new MovieMessage(ActionCode.MOVIE_SYNC, seconds, rate).toJson());
        });

        syncImage = new ImageView(this.SYNC_BLACK_ICON);
        syncPane.getChildren().add(syncImage);
        gridPane.add(syncPane, 3, 0);
    }

    // 弹幕组件
    private void setBulletScreenItem(GridPane gridPane) {
        bulletScreenInput = new TextField();
        bulletScreenInput.setPromptText("发送弹幕~");
        gridPane.add(bulletScreenInput, 4, 0);

        // 发送弹幕
        bulletScreenInput.setOnAction(event -> {
            // 获取内容
            String content = bulletScreenInput.getText();
            if (content.length() == 0) return;
            bulletScreenInput.setText("");
            // 显示弹幕
            showBulletScreen(content, SELF_COLOR);
            // 如果已经建立了绑定，则发送弹幕消息
            if (client != null && client.isBind()) {
                client.send(new BulletScreenMessage(content).toJson());
            }
        });
    }

    // 声音组件
    private void setSoundItem(GridPane gridPane) {
        HBox hBox = new HBox(5);
        hBox.setAlignment(Pos.CENTER);

        // 声音图标，点击静音
        AnchorPane pane = new AnchorPane();
        soundImage = new ImageView(this.SOUND_OPEN_ICON);
        pane.getChildren().add(soundImage);
        pane.setCursor(Cursor.HAND);
        pane.setOnMouseClicked(event -> {
            if (isMute) {
                soundImage.setImage(SOUND_OPEN_ICON);
                if (hasMedia()) {
                    mediaView.getMediaPlayer().setMute(false);
                }
            } else {
                soundImage.setImage(SOUND_CLOSE_ICON);
                if (hasMedia()) {
                    mediaView.getMediaPlayer().setMute(true);
                }
            }
            isMute = !isMute;
        });

        hBox.getChildren().add(pane);
        volumeSlider = new Slider();
        volumeSlider.setMax(1);
        volumeSlider.setValue(0.8);
        hBox.getChildren().add(volumeSlider);
        volumeLabel = new Label("80%");
        volumeLabel.setAlignment(Pos.CENTER_RIGHT);
        volumeLabel.setPrefWidth(40);
        volumeLabel.setFont(new Font(15));
        hBox.getChildren().add(volumeLabel);
        // 3. 音量进度条和 Label 绑定
        volumeSlider.valueProperty().addListener((observableValue, number, t1) -> {
            String label = (int) (t1.doubleValue() * 100) + "%";
            volumeLabel.setText(label);
        });
        gridPane.add(hBox, 5, 0);
    }

    // 倍速组件
    private void setRateItem(GridPane gridPane) {
        rateSpinner = new Spinner<>(0.5, 2.5, 1, 0.05);
        rateSpinner.setDisable(true);
        rateSpinner.setEditable(true);
        rateSpinner.setPrefWidth(80);
        rateSpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        gridPane.add(rateSpinner, 6, 0);
    }

    // 全屏组件
    private void setFullScreen(GridPane gridPane) {
        AnchorPane pane = new AnchorPane();
        pane.setCursor(Cursor.HAND);
        pane.getChildren().add(new ImageView(this.FULL_SCREEN_ICON));

        pane.setOnMouseClicked(event -> {
            // 鼠标左键点击有效
            if (event.getButton() != MouseButton.PRIMARY) return;

            // 设置全屏
            primaryStage.setFullScreen(!primaryStage.isFullScreen());
        });

        gridPane.add(pane, 7, 0);
    }

    // 连接服务器
    private void connectServer() {
        // 开启新的线程连接服务器
        new Thread(() -> {
            try {
                log.info("正在连接服务器...");
                client = new WebClient(new URI(ClientConstants.DEFAULT_URL));
                boolean flag = client.connectBlocking();
                if (!flag) {
                    client = null;
                    AlertUtils.error("服务器连接失败！请检查更新或联系作者！", "", this.primaryStage);
                    log.error("连接服务器失败！");
                    return;
                }

                // 添加观察者
                client.addObserver(new ClientObserver() {
                    @Override
                    public void onConnected(ServerConnectMessage message) {
                        Platform.runLater(() -> selfNumberLabel.setText(message.getNumber()));
                    }

                    @Override
                    public void onMovie(MovieMessage message) {
                        MediaPlayer player = mediaView.getMediaPlayer();
                        // 如果视频没有加载，则不做处理
                        if (player == null) {
                            log.info("视频尚未加载！");
                            return;
                        }
                        if (message.getActionCode() == ActionCode.MOVIE_PLAY) {
                            player.play();
                            playImage.setImage(PAUSE_BLUE_ICON);
                        } else if (message.getActionCode() == ActionCode.MOVIE_PAUSE) {
                            player.pause();
                            playImage.setImage(PLAY_BLUE_ICON);
                        } else if (message.getActionCode() == ActionCode.MOVIE_STOP) {
                            player.stop();
                            playImage.setImage(PLAY_BLUE_ICON);
                        } else if (message.getActionCode() == ActionCode.MOVIE_SYNC) {
                            player.seek(Duration.seconds(message.getSeconds()));
                            player.setRate(message.getRate());
                            player.play();
                            playImage.setImage(PAUSE_BLUE_ICON);
                            log.info("同步播放 -- 进度：{}，倍速：{}", DurationUtils.getText(message.getSeconds()), message.getRate());
                        }
                    }

                    @Override
                    public void onBind(BindMessage message) {
                        // 将客户端设置为已绑定
                        client.setBind(true);
                        // 更新 UI
                        Platform.runLater(() -> {
                            // 设置对方的星星号
                            friendInput.setText(message.getTargetNumber());
                            // 更换连接图标为断开连接图标
                            bindImage.setImage(BREAK_ICON);
                            // 将播放/暂停按钮设置为蓝色
                            if (isPlaying()) {
                                playImage.setImage(PAUSE_BLUE_ICON);
                            } else {
                                playImage.setImage(PLAY_BLUE_ICON);
                            }
                            // 解禁同步图标
                            syncImage.setImage(SYNC_BLUE_ICON);
                            if (mediaView.getMediaPlayer() != null) {
                                syncPane.setDisable(false);
                                syncPane.setOpacity(1);
                            }
                            // 转移焦点
                            volumeSlider.requestFocus();
                        });
                        log.info("与 {} 绑定成功！", message.getTargetNumber());
                    }

                    @Override
                    public void onUnbind() {
                        // 解除绑定
                        client.setBind(false);
                        // 更新 UI
                        Platform.runLater(() -> flushUI(UI.UN_BIND));
                        AlertUtils.information("另一半解除绑定！", "", primaryStage);
                        log.info("与另一半解除绑定！");
                    }

                    @Override
                    public void onOffline() {
                        // 解除绑定
                        client.setBind(false);
                        // 更新 UI
                        Platform.runLater(() -> flushUI(UI.UN_BIND));
                        AlertUtils.information("另一半断开连接！", "", primaryStage);
                        log.info("与另一半解除绑定！");
                    }

                    @Override
                    public void onBulletScreen(BulletScreenMessage message) {
                        Platform.runLater(() -> showBulletScreen(message.getContent(), TARGET_COLOR));
                    }

                    @Override
                    public void onServerMessage(ServerResponseMessage message) {
                        Platform.runLater(() -> AlertUtils.error(message.getMsg(), "", primaryStage));
                    }
                });
            } catch (URISyntaxException | InterruptedException e) {
                e.printStackTrace();
            }
        }, "ConnectServerThread").start();
    }

    // 刷新 UI
    private void flushUI(int code) {
        if (code == UI.UN_BIND) {
            // 清空另一半的星星号
            friendInput.setText("");
            // 更换断开连接图标为连接图标
            bindImage.setImage(LINK_ICON);
            // 将播放/暂停按钮设置为黑色
            if (isPlaying()) {
                playImage.setImage(PAUSE_BLACK_ICON);
            } else {
                playImage.setImage(PLAY_BLACK_ICON);
            }
            // 禁止同步图标
            syncPane.setDisable(true);
            syncPane.setOpacity(0.6);
            syncImage.setImage(SYNC_BLACK_ICON);
        } else if (code == UI.OPEN_VIDEO) {
            playPane.setDisable(false);
            playPane.setOpacity(1);
            videoSlider.setDisable(false);
            volumeSlider.setDisable(false);
            rateSpinner.setDisable(false);
            bulletScreenInput.setDisable(false);
            if (isBind()) {
                syncPane.setDisable(false);
                syncPane.setOpacity(1);
                syncImage.setImage(SYNC_BLUE_ICON);
            }
        } else if (code == UI.CLOSE_VIDEO) {
            playPane.setDisable(true);
            playPane.setOpacity(0.6);
            videoSlider.setDisable(true);
            volumeSlider.setDisable(true);
            rateSpinner.setDisable(true);
            bulletScreenInput.setDisable(true);
            // 禁止同步图标
            syncPane.setDisable(true);
            syncPane.setOpacity(0.6);
            syncImage.setImage(SYNC_BLACK_ICON);
        }
    }

    private boolean isBind() {
        return client != null && client.isBind();
    }

    private boolean isPlaying() {
        MediaPlayer player = mediaView.getMediaPlayer();
        return player != null && player.getStatus() == MediaPlayer.Status.PLAYING;
    }

    private boolean hasMedia() {
        return mediaView.getMediaPlayer() != null;
    }

    // 播放
    public void play(MediaPlayer player) {
        if (isBind()) {
            client.send(MovieMessage.MOVIE_PLAY.toJson());
            playImage.setImage(this.PAUSE_BLUE_ICON);
        } else {
            player.play();
            playImage.setImage(this.PAUSE_BLACK_ICON);
        }
    }

    // 暂停播放
    public void pause(MediaPlayer player) {
        if (isBind()) {
            client.send(MovieMessage.MOVIE_PAUSE.toJson());
            playImage.setImage(this.PLAY_BLUE_ICON);
        } else {
            player.pause();
            playImage.setImage(this.PLAY_BLACK_ICON);
        }
    }

    // 根据视频的状态播放或者暂停
    public void playOrPause(MouseEvent event) {
        if (event != null && event.getButton() != MouseButton.PRIMARY) {
            return;
        }
        MediaPlayer player = mediaView.getMediaPlayer();
        if (isPlaying()) {
            pause(player);
        } else {
            play(player);
        }
    }


    // 打开视频
    private void openVideo() {
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
            mediaView.getMediaPlayer().dispose();
            flushUI(UI.CLOSE_VIDEO);
            log.info("视频销毁成功");
        }

        // 将文件转为 uri 路径，加载媒体视频
        String uri = URIUtils.encoder("file:" + file.toPath().toUri().getPath());
        log.info(uri);

        // 加载视频
        loadVideo(uri, 0);
    }

    private void loadVideo(String uri, int count) {
        // 重复次数，最多重复3次
        count++;
        if (count >= 4) {
            AlertUtils.warning("视频加载开了会小差，再试一次吧。", "", primaryStage);
            log.error("视频加载三次失败！");
            return;
        }

        final int finalCount = count;
        // 开启新线程加载视频
        new Thread(() -> {
            MediaPlayer mediaPlayer;
            try {
                mediaPlayer = new MediaPlayer(new Media(uri));
            } catch (Exception exception) {
                exception.printStackTrace();
                AlertUtils.error(exception.getMessage(), "", primaryStage);
                return;
            }

            mediaView.setMediaPlayer(mediaPlayer);

            // 3. 媒体加载完毕后
            mediaPlayer.setOnReady(() -> {
                // 1. 按钮解禁
                flushUI(UI.OPEN_VIDEO);

                // 2. 设置进度条
                Duration totalDuration = mediaPlayer.getTotalDuration();
                videoSlider.setMax(totalDuration.toSeconds());
                videoSlider.setVisible(true);
                mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    if (!mouse) {
                        videoSlider.setValue(newValue.toSeconds());
                        timeLabel.setText(DurationUtils.getText(newValue));

                        // 当播放结束时暂停
                        double temp = totalDuration.toMillis() - newValue.toMillis();
                        if (temp <= 120) {
                            pause(mediaPlayer);
                        }
                    }
                });

                // 绑定音量进度条
                mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty());
                // 绑定倍速
                mediaPlayer.rateProperty().bindBidirectional(rateSpinner.getValueFactory().valueProperty());

                // 3. 设置视频点击播放/暂停
                mediaView.setOnMouseClicked(e2 -> playOrPause(null));

                // 设置进度显示
                DurationUtils.setTotal(totalDuration);
                timeLabel.setText(DurationUtils.getText(0));

                // 设置是否静音
                mediaPlayer.setMute(isMute);

                log.info("视频加载成功");
            });

            mediaPlayer.setOnError(() -> {
                log.warn("视频加载错误，尝试第" + finalCount + "次重新加载...");
                loadVideo(uri, finalCount);
            });
        }).start();
    }

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

        double width = mediaView.getFitWidth();
        double layoutX = mediaView.getLayoutX();
//        log.debug("width = {}, layoutX = {}", width, layoutX);

        // 创建动画
        double startX = isFullScreen ? screen.getBounds().getWidth() : width + layoutX - textWidth;
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

    // 关闭软件
    public void close() {
        // 关闭服务器连接
        if (client != null) {
            try {
                client.closeBlocking();
                log.info("关闭服务器连接");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // 销毁媒体
        if (mediaView.getMediaPlayer() != null) {
            mediaView.getMediaPlayer().dispose();
            log.info("销毁视频");
        }
        // 关闭任务
        countTask.stop();
        log.debug("结束计时任务");
        log.info("客户端关闭");
    }

    // 检查更新
    public void checkUpdate() {
        log.debug("check update...");

        // 创建 HTTP 请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ClientConstants.CHECK_UPDATE_URL))
                .header("Content-Type", "application/json")
                .timeout(java.time.Duration.ofSeconds(5))
                .build();

        // 发起请求
        HttpClient httpClient = HttpClient.newHttpClient();
        httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    // 如果状态码不是200，抛出Http异常
                    if (response.statusCode() != 200) {
                        throw new HttpException(response.statusCode());
                    }
                    return response.body();
                }).thenAccept(body -> {
                    // 解析 JSON
                    VersionResult result = JSONUtils.parse(body, VersionResult.class);
                    ProjectVersion version = result.getData();

                    // 如果 code 不是 200，抛出结果异常
                    if (result.getCode() != 200) {
                        throw new ResultException(result.getMsg());
                    }

                    // 有新版本
                    if (version.getVersionNumber() > ClientConstants.VERSION_NUMBER) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setHeaderText("发现新版本" + version.getVersion() + "！是否前往下载？");
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
                }).exceptionally(throwable -> {
                    // 捕获异常
                    Throwable ex = throwable.getCause();
                    throwable.printStackTrace();
                    log.warn("{} : {}", ex.getClass(), ex.getMessage());

                    // 鉴别异常类型
                    if (ex instanceof ResultException) {
                        AlertUtils.error("请求出错", ex.getMessage(), this.primaryStage);
                    } else if (ex instanceof HttpConnectTimeoutException) {
                        checkUpdate();
                    } else if (ex instanceof HttpTimeoutException) {
                        AlertUtils.error("请求超时", "网络请求超时，请稍后再试。", this.primaryStage);
                    } else {
                        // HTTP 错误、连接错误、等等
                        AlertUtils.error("网络错误", "检查更新发生错误，请稍后再试。", this.primaryStage);
                    }
                    return null;
                });
    }

    /**
     * 打开软件官网
     */
    public void about() {
        this.hostServices.showDocument(ClientConstants.ABOUT_URL);
    }
}
