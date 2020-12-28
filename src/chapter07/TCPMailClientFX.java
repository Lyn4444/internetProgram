package chapter07;

import chapter03.TCPClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class TCPMailClientFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Button btnExit = new Button("退出");
    private Button btnSend = new Button("发送");
    private Button btnConnect = new Button("连接");
    private TextField tfSend = new TextField();
    private TextArea taDisplay = new TextArea();
    private TextField tfIP = new TextField();
    private TextField tfPort = new TextField();
    private TCPMailClient tcpMailClient;
    private Thread readTread;
    private String ip;
    private String port;

    public void exit() {
        if(tcpMailClient != null){
            //向服务器发送关闭连接的约定信息
            tcpMailClient.send("bye");
        }
        System.exit(0);
    }
    public void send() {
        String sendMsg = tfSend.getText();
        tcpMailClient.send(sendMsg);//向服务器发送一串字符
        taDisplay.appendText("客户端发送：" + sendMsg + "\n");
//        String receiveMsg = tcpClient.receive();//从服务器接收一行字符
//        taDisplay.appendText(receiveMsg + "\n");
        tfSend.clear();
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane mainPane = new BorderPane();

        //只读和自动换行
        taDisplay.setWrapText(true);
        taDisplay.setEditable(false);
        //连接服务器前禁用发送按钮
        btnSend.setDisable(true);

        //退出按钮的响应
        btnExit.setOnAction((ActionEvent e) -> {
            exit();

        });

        // 直接×掉窗口
        primaryStage.setOnCloseRequest(e ->{
            exit();
        });

        //发送按钮的响应
        btnSend.setOnAction((ActionEvent e) -> {
            if(!btnSend.isDisable()) {
                send();
            }
        });
        //回车响应
        tfSend.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.getKeyCode("Enter")){
                btnSend.fire();
            }
        });

        btnConnect.setOnAction(event ->{
            ip = tfIP.getText().trim();
            port = tfPort.getText().trim();
            try{
                //tcpClient不是局部变量，是本程序定义的一个TCPClient类型的成员变量
                tcpMailClient = new TCPMailClient(ip, port);
                //成功连接服务器，接收服务器发来的第一条欢迎信息

                String firstMsg = tcpMailClient.receive();
                taDisplay.appendText(firstMsg + "\n");
                btnSend.setDisable(false);    //连接服务器成功后启用发送按钮
            } catch (Exception e) {
                taDisplay.appendText("服务器连接失败！" + e.getMessage() + "\n");
                btnSend.setDisable(true);
            }
            readTread = new Thread(() -> {
                String msg = null;
                while((msg = tcpMailClient.receive()) != null) {
                    String temMsg = msg;
                    Platform.runLater(() -> {
                        taDisplay.appendText(temMsg + "\n");
                    });
                }
                Platform.runLater(() -> {
                    taDisplay.appendText("对话已关闭！\n");
                });
            });
            readTread.start();

        });

        HBox hBox1 = new HBox();
        hBox1.setSpacing(10);
        hBox1.setPadding(new Insets(15,20,10,10));
        hBox1.setAlignment(Pos.TOP_CENTER);
        hBox1.getChildren().addAll(new Label("IP地址："), tfIP, new Label("端口："), tfPort, btnConnect);
        mainPane.setTop(hBox1);

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(15,20,10,10));
        vBox.getChildren().addAll(new Label("信息显示区:"),taDisplay,
                new Label("信息输入区:"),tfSend);
        mainPane.setCenter(vBox);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(15,20,10,10));
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.getChildren().addAll(btnSend,btnExit);
        mainPane.setBottom(hBox);

        Scene scene = new Scene(mainPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
