package top.tonydon;


import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
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
import top.tonydon.domain.VideoDuration;
import top.tonydon.message.Message;
import top.tonydon.message.client.*;
import top.tonydon.message.server.*;
import top.tonydon.task.CountTask;
import top.tonydon.util.ActionCode;
import top.tonydon.util.observer.ClientObserver;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;


public class ClientController {
    private final Logger log = LoggerFactory.getLogger(ClientController.class);

    /**************************************************************************
     *
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
    private boolean mouse = false;
    private VideoDuration videoDuration;

    private final Color selfColor = Color.WHITESMOKE;
    private final Color targetColor = Color.web("#FFFF00");

    private Image playIcon;
    private Image pauseIcon;

    /**************************************************************************
     *
     * 初始化方法
     *
     **************************************************************************/
    @FXML
    private void initialize() {
        // 开启一个线程加载资源初始化一些内容
        Thread loadResourceThread = new Thread(() -> {
            playIcon = new Image(Objects.requireNonNull(getClass().getResource("icon/播放.png")).toString());
            pauseIcon = new Image(Objects.requireNonNull(getClass().getResource("icon/暂停.png")).toString());
            log.info("应用程序资源加载完毕");
        }, "LoadResourceThread");
        loadResourceThread.start();

        // 实例化对象
        videoDuration = new VideoDuration();

        // 2. 进度条的监听事件
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

        // 等待线程执行结束
        try {
            loadResourceThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 页面加载完毕后执行的初始化操作
     */
    public void init() {
        // 初始化变量
        primaryStage = (Stage) root.getScene().getWindow(); // 获取主窗口
        playOrPauseImageView.setImage(playIcon);            // 设置播放按钮图标

        // 2. 获取主屏幕尺寸
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();

        // 创建控件任务
        CountTask countTask = new CountTask();
        countTask.ready();

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
                mediaView.setFitWidth(ClientConstants.MOVIE_WIDTH);
                mediaView.setFitHeight(ClientConstants.MOVIE_HEIGHT);

                countTask.stop();
                controllerBox.setOpacity(1);
                controllerBox.setVisible(true);
                controllerBox.setPrefWidth(ClientConstants.MOVIE_WIDTH);
                leftCB.setPrefWidth(ClientConstants.MOVIE_WIDTH * 0.3);
                rightCB.setPrefWidth(ClientConstants.MOVIE_WIDTH * 0.7);
            }
        });


        // 监听 value，若 value 为 4 的倍数则结束任务，且任务没有停止，隐藏控制栏
        countTask.addObserver((old, cur) -> {
            if (cur % 4 == 0 && !countTask.isStop()) {
                countTask.stop();
                controllerBox.setVisible(false);
            }
        });

        mediaView.setOnMouseMoved(event -> {
            // 不是全屏状态下直接返回
            if (!primaryStage.isFullScreen()) return;

            // 如果鼠标在控制栏附件，则始终展示控件
            if (event.getScreenY() > (screenHeight - 80)) {
                controllerBox.setVisible(true);
                if (!countTask.isStop()) countTask.stop();
                return;
            }

            // 如果任务是取消状态，重启任务
            if (countTask.isStop()) {
                controllerBox.setVisible(true);
                countTask.restart();
            } else {
                // 任务是运行状态，重置 count 值
                countTask.setCount(1);
            }
        });
    }


    /**************************************************************************
     *
     * 普通成员方法
     *
     **************************************************************************/

    // 显示提示窗口
    public void showAlert(String headText, Alert.AlertType alertType) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(alertType.toString());
            alert.setHeaderText(headText);
            alert.initModality(Modality.WINDOW_MODAL);
            alert.initOwner(primaryStage);
            alert.show();
        });
    }

    public void play(MediaPlayer player) {
        player.play();
        playOrPauseImageView.setImage(pauseIcon);
    }

    public void pause(MediaPlayer player) {
        player.pause();
        playOrPauseImageView.setImage(playIcon);
    }

    public void stop(MediaPlayer player) {
        player.stop();
        playOrPauseImageView.setImage(playIcon);
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
     *
     * @param actionEvent 点击事件
     */
    public void unbindNumber(ActionEvent actionEvent) {
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
            ClientUnbindMessage message = new ClientUnbindMessage(client.getSelfNumber());
            client.send(message.toJson());

            togetherVBox.setDisable(true);      // 禁用一起播放按钮组
            targetNumberLabel.setText("");      // 清空另一半的星星号
            bindButton.setText("绑定他/她");     // 重置绑定按钮名称
        }
    }


    /**
     * 绑定另一个人
     *
     * @param actionEvent 点击事件
     */
    public void bindNumber(ActionEvent actionEvent) {
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
        result.ifPresent(s -> {
            ClientBindMessage clientBindMessage = new ClientBindMessage(client.getSelfNumber(), s);
            client.send(clientBindMessage.toJson());
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
    public void bindButtonAction(ActionEvent actionEvent) {
        if (client.isBind()) unbindNumber(actionEvent);
        else bindNumber(actionEvent);
    }

    @FXML
    public void selectVideo(ActionEvent actionEvent) {
        // 创建文件选择器
        FileChooser fileChooser = new FileChooser();
        // 设置过滤器，第一个参数是描述文本，第二个参数是过滤规则
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("MP4 文件（*.mp4）", "*.mp4");
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
            Duration duration = mediaPlayer.getTotalDuration();
            videoSlider.setMax(duration.toSeconds());
            videoSlider.setVisible(true);
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                if (!mouse) {
                    videoSlider.setValue(newValue.toSeconds());
                    videoDuration.setCurrentDuration(newValue);
                    videoDurationLabel.setText(videoDuration.toString());
                }
            });

            // 绑定音量进度条
            mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty());
            // 绑定倍速
            mediaPlayer.rateProperty().bindBidirectional(rateSpinner.getValueFactory().valueProperty());/**/

            // 3. 设置视频点击播放/暂停
            mediaView.setOnMouseClicked(event -> playOrPause(null));

            // 设置进度显示
            videoDuration.setTotalDuration(duration);
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
        Message message = new ClientMovieMessage(client.getSelfNumber(), ActionCode.PLAY);
        client.send(message.toJson());
    }

    @FXML
    public void togetherPause(ActionEvent actionEvent) {
        Message message = new ClientMovieMessage(client.getSelfNumber(), ActionCode.PAUSE);
        client.send(message.toJson());
    }

    @FXML
    public void togetherStop(ActionEvent actionEvent) {
        Message message = new ClientMovieMessage(client.getSelfNumber(), ActionCode.STOP);
        client.send(message.toJson());
    }

    // 复制星星号
    @FXML
    public void copyNumber(ActionEvent actionEvent) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(client.getSelfNumber());
        clipboard.setContent(content);
    }

    // 连接服务器
    @FXML
    public void connectServer(ActionEvent actionEvent) {
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
//        inputDialog.getEditor().setText(ClientConsts.DEFAULT_URL);
        inputDialog.getEditor().setText(ClientConstants.LOCAL_URL);
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
            showAlert("地址格式不正确！", Alert.AlertType.ERROR);
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
                    showAlert("服务器连接失败！请重试！", Alert.AlertType.ERROR);
                    log.error("连接服务器失败！");
                    return;
                }
                showAlert("服务器连接成功！", Alert.AlertType.INFORMATION);
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
                    }

                    @Override
                    public void onMovie(ServerMovieMessage message) {
                        MediaPlayer player = mediaView.getMediaPlayer();
                        if (message.getActionCode() == ActionCode.PLAY) {
                            play(player);
                        } else if (message.getActionCode() == ActionCode.PAUSE) {
                            pause(player);
                        } else if (message.getActionCode() == ActionCode.STOP) {
                            stop(player);
                        } else if (message.getActionCode() == ActionCode.SYNC) {
                            player.seek(Duration.seconds(message.getSeconds()));
                            player.setRate(message.getRate());
                            play(player);
                        }
                    }

                    @Override
                    public void onBind(ServerBindMessage message) {
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
                    public void onUnBind(ServerUnbindMessage message) {
                        // 解除绑定
                        client.setBind(false);
                        Platform.runLater(() -> {
                            togetherVBox.setDisable(true); // 禁用一起播放按钮组
                            targetNumberLabel.setText("");      // 清空另一半的星星号
                            bindButton.setText("绑定他/她");     // 重置绑定按钮名称
                        });
                    }

                    @Override
                    public void onOffline(ServerOfflineMessage message) {
                        onUnBind(new ServerUnbindMessage());
                        showAlert("另一半断开连接", Alert.AlertType.INFORMATION);
                    }

                    @Override
                    public void onBulletScreen(ServerBulletScreenMessage message) {
                        Platform.runLater(() -> showBulletScreen(message.getContent(), targetColor));
                    }
                });
            } catch (URISyntaxException | InterruptedException e) {
                e.printStackTrace();
            }
        }, "ConnectServerThread").start();
    }


    @FXML
    public void synchronization(ActionEvent actionEvent) {
        MediaPlayer player = mediaView.getMediaPlayer();
        double seconds = player.getCurrentTime().toSeconds();
        double rate = player.getRate();
        Message message = new ClientMovieMessage(client.getSelfNumber(), ActionCode.SYNC, seconds, rate);
        client.send(message.toJson());
    }

    @FXML
    public void sendBulletScreen(ActionEvent actionEvent) {
        // 获取内容
        String content = bsTextField.getText();
        if (content.length() == 0) return;
        bsTextField.setText("");
        // 显示弹幕
        showBulletScreen(content, selfColor);
        // 如果已经建立了绑定，则发送弹幕消息
        if (client != null && client.isBind()) {
            Message message = new ClientBulletScreenMessage(client.getSelfNumber(), content);
            client.send(message.toJson());
        }
    }


}