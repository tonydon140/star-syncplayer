package top.tonydon.syncplayer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.tonydon.syncplayer.client.WebClient;
import top.tonydon.syncplayer.constant.RoomConstants;
import top.tonydon.syncplayer.constant.UI;
import top.tonydon.syncplayer.message.ActionCode;
import top.tonydon.syncplayer.message.common.ActionMessage;
import top.tonydon.syncplayer.message.common.MovieMessage;
import top.tonydon.syncplayer.util.AlertUtils;
import top.tonydon.syncplayer.util.observer.ClientObserver;

import java.net.URI;
import java.net.URISyntaxException;

public class RoomWindow {
    private static final Logger log = LoggerFactory.getLogger(RoomWindow.class);

    private Stage primaryStage;
    private Application application;

    // 布局
    private AnchorPane root;
    private HBox bodyHBox;
    private MenuBar menuBar;
    private AnchorPane leftPane;
    private AnchorPane rightPane;

    // 组件
    private Button createRoomBtn;
    private Button addRoomBtn;

    private String roomId;
    private String id;
    private boolean isConnection;
    private boolean isInRoom;


    public RoomWindow(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // 初始化布局
        this.root = new AnchorPane();
        this.menuBar = new MenuBar();
        this.bodyHBox = new HBox();
        this.leftPane = new AnchorPane();
        this.rightPane = new AnchorPane();

        // 初始化组件
        createRoomBtn = new Button("创建放映室");
        addRoomBtn = new Button("加入放映室");


        load();
    }

    // 页面显示后的初始化
    public void init(Application application) {
        this.application = application;
        connectServer();
    }

    public Parent getRoot() {
        return root;
    }

    public void load() {
        setMenuBar();
        setBody();
        setListener();
    }

    private void setListener() {
        root.widthProperty().addListener((observable, oldValue, newValue) -> updateWidth(newValue.doubleValue()));
        root.heightProperty().addListener((observable, oldValue, newValue) -> updateHeight(newValue.doubleValue()));

    }

    private void updateWidth(double width) {
        // 菜单栏同步变化
        menuBar.setPrefWidth(width);
        bodyHBox.setPrefWidth(width);
        leftPane.setPrefWidth(width * 0.7);
        defaultBox.setPrefWidth(width * 0.3);
        roomBox.setPrefWidth(width * 0.3);
    }

    private void updateHeight(double height) {
        double bodyHeight = height - 25;
        bodyHBox.setPrefHeight(bodyHeight);
        defaultBox.setPrefHeight(bodyHeight);
        roomBox.setPrefHeight(bodyHeight);
    }


    private void setMenuBar() {
        menuBar.setPrefHeight(25);

        // 打开视频
        Menu openVideoMenu = new Menu();
        Label openVideoLabel = new Label("打开视频");
        openVideoMenu.setGraphic(openVideoLabel);
//        openVideoLabel.setOnMouseClicked(event -> connectServer());

        // 服务器菜单
        Menu serverMenu = new Menu("服务器");
        MenuItem createRoomItem = new MenuItem("创建放映室");
        MenuItem addRoomItem = new MenuItem("加入放映室");
        serverMenu.getItems().add(createRoomItem);
        serverMenu.getItems().add(addRoomItem);
        createRoomItem.setOnAction(event -> createRoom());
        addRoomItem.setOnAction(event -> addRoom());

        // 帮助菜单
        Menu helpMenu = new Menu("帮助");
        MenuItem updateItem = new MenuItem("检查更新");
        MenuItem aboutItem = new MenuItem("关于");
        helpMenu.getItems().addAll(updateItem, aboutItem);
//        updateItem.setOnAction(actionEvent -> checkUpdate());
//        aboutItem.setOnAction(actionEvent -> about());

        menuBar.getMenus().add(openVideoMenu);
        menuBar.getMenus().add(serverMenu);
        menuBar.getMenus().add(helpMenu);
        root.getChildren().add(menuBar);
    }

    private void setBody() {
        this.root.getChildren().add(this.bodyHBox);

        bodyHBox.setLayoutY(25);


        setLeftPane(bodyHBox);
        setRightPane(bodyHBox);
    }

    private void setLeftPane(HBox parent) {
        parent.getChildren().add(leftPane);

        leftPane.setBackground(Background.fill(Color.ALICEBLUE));

    }

    private void setRightPane(HBox parent) {
        parent.getChildren().add(rightPane);

        loadRoomBox();
        loadDefaultBox();

        rightPane.getChildren().add(defaultBox) ;
    }

    private VBox defaultBox = new VBox();
    private VBox roomBox = new VBox();


    private Label roomTitleLabel = new Label();

    private TextFlow commentPane = new TextFlow();
    private TextField commentInput = new TextField();
    private Button sendCommentBtn = new Button("发送");

    // 加载放映室窗口
    private void loadRoomBox(){
        roomBox.setBackground(Background.fill(Color.web("#11111D")));
        roomBox.setSpacing(10);

        // 放映室标题
        roomTitleLabel.setFont(Font.font(16));
        roomTitleLabel.setCursor(Cursor.HAND);
        Tooltip tooltip = new Tooltip("点击复制");
        tooltip.setShowDelay(Duration.ZERO);
        roomTitleLabel.setTooltip(tooltip);

        // 评论区
        commentPane.setLineSpacing(10);
        commentPane.getChildren().add(new Text("1232\n"));
        commentPane.getChildren().add(new Text("1232\n"));
        commentPane.getChildren().add(new Text("1232\n"));
        commentPane.setPrefHeight(500);

        roomBox.getChildren().add(roomTitleLabel);
        roomBox.getChildren().add(commentPane);
        roomBox.getChildren().add(commentInput);
        roomBox.getChildren().add(sendCommentBtn);
        TextFlow textFlow = new TextFlow();
//        text
    }

    // 加载默认窗口
    private void loadDefaultBox() {
        defaultBox.setSpacing(20);
        defaultBox.setAlignment(Pos.CENTER);
        defaultBox.setBackground(Background.fill(Color.BLUEVIOLET));


        createRoomBtn.setDisable(true);
        addRoomBtn.setDisable(true);


        createRoomBtn.setOnAction(event -> createRoom());
        addRoomBtn.setOnAction(event -> addRoom());


        defaultBox.getChildren().add(createRoomBtn);
        defaultBox.getChildren().add(addRoomBtn);
    }

    private WebClient client;

    private void connectServer() {
        // 开启新的线程连接服务器
        new Thread(() -> {
            try {
                client = new WebClient(new URI(RoomConstants.DEFAULT_URL));
                boolean flag = client.connectBlocking();

                // 连接失败
                if (!flag) {
                    client = null;
                    AlertUtils.error("服务器连接失败！请检查更新或联系作者！", "", this.primaryStage);
                    log.error("server connection fail!");
                    return;
                }

                // 连接服务器成功
                flushUI(UI.CONNECTED_SERVER);

                // 添加观察者
                client.addObserver(new ClientObserver() {
                    @Override
                    public void onAction(int code) {
                        doAction(code);
                    }

                    @Override
                    public void onString(int code, String content) {
                        doString(code, content);
                    }

                    @Override
                    public void onMovie(MovieMessage message) {
                        doMovie(message);
                    }
                });
            } catch (URISyntaxException | InterruptedException e) {
                e.printStackTrace();
            }
        }, "ConnectServerThread").start();
    }


    private void doAction(int actionCode) {

    }

    private void doString(int actionCode, String content) {
        switch (actionCode) {
            // 放映室创建成功
            case ActionCode.ROOM_CREATED -> doRoomCreated(content);
            case ActionCode.CONNECTED -> doConnected(content);
        }
    }



    private void doConnected(String id) {
        this.id = id;
        log.info("server connected, id = {}", id);
    }

    private void doRoomCreated(String roomId) {
        this.roomId = roomId;
        log.info("room created, room id = {}", roomId);

        Platform.runLater(()->{
            switchToRoom();
            roomTitleLabel.setText("放映室ID：" + roomId);
        });
    }

    private void switchToRoom(){
        rightPane.getChildren().set(0, roomBox);
    }

    private void doMovie(MovieMessage message) {


    }

    private void flushUI(int code) {
        switch (code) {
            case UI.CONNECTED_SERVER -> {
                createRoomBtn.setDisable(false);
                addRoomBtn.setDisable(false);
            }
        }
    }

    private void createRoom() {
        client.send(ActionMessage.CREATE_ROOM.toJson());
    }

    private void addRoom() {

    }

    public void close(){
        // 关闭服务器连接
        if (client != null) {
            try {
                client.closeBlocking();
                log.info("server connection closed");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
