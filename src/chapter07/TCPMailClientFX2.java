package chapter07;
/* -*- coding: utf-8 -*-
 * @Author: Jewfer
 * @Date: 2020/10/18 18:23
 */

import javafx.application.Application;
import javafx.application.Platform;
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

import java.io.IOException;


public class TCPMailClientFX2 extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    private TCPMailClient tcpMailClient;
    private TextField tfServerIP = new TextField("smtp.qq.com");
    private TextField tfServerPort = new TextField("25");
    private TextField tfSendAddr = new TextField("3077002706@qq.com");
    private TextField tfRcieveAddr = new TextField("ljhothers_2020@qq.com");
    private TextField tfMailTitle = new TextField("测试");
    private TextArea tfContent = new TextArea("验证码");
    private TextArea taDisplay = new TextArea();
    private Button btSend = new Button("发送");
    private Button btExit = new Button("退出");
    private Thread readTread;
    private Thread sendTread;

    public void exit() {
        if(tcpMailClient != null){
            //向服务器发送关闭连接的约定信息
            tcpMailClient.send("bye");
        }
        System.exit(0);
    }
    @Override
    public void start(Stage primaryStage) {

        btSend.setOnAction(e ->{
            String smtpAddr = tfServerIP.getText().trim();
            String smtpPort = tfServerPort.getText().trim();
            try{
                tcpMailClient = new TCPMailClient(smtpAddr, smtpPort);
                // 开两个线程即可边发送信息边接受信息
                // 开一个线程用于发送
                readTread = new Thread(() -> {
                    String msg = null;
                    while ((msg = tcpMailClient.receive()) != null) {
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
                // 开一个接受服务器信息的新线程
                sendTread = new Thread(() ->{
                    tcpMailClient.send("HELO myfriend");
                    tcpMailClient.send("AUTH LOGIN");
                    String username = "3077002706@qq.com";
                    String authCode = "ylvaflbdsoqsdcdi";
                    String msg = new sun.misc.BASE64Encoder().encode(username.getBytes());
                    tcpMailClient.send(msg);
                    msg = new sun.misc.BASE64Encoder().encode(authCode.getBytes());
                    tcpMailClient.send(msg);
                    msg = "MAIL FROM:<" + tfSendAddr.getText().trim() + ">";
                    tcpMailClient.send(msg);
                    msg = "RCPT TO:<" + tfRcieveAddr.getText().trim() + ">";
                    tcpMailClient.send(msg);
                    msg = "DATA";
                    tcpMailClient.send(msg);
                    msg = "FROM:" + tfSendAddr.getText().trim();
                    tcpMailClient.send(msg);
                    msg = "Subject:" + tfMailTitle.getText().trim();
                    tcpMailClient.send(msg);
                    msg = "To:" + tfSendAddr.getText().trim();
                    tcpMailClient.send(msg);
                    msg = "\n";
                    tcpMailClient.send(msg);
                    msg = tfContent.getText().trim();
                    tcpMailClient.send(msg);
                    msg = ".";
                    tcpMailClient.send(msg);
                    msg = "QUIT";
                    tcpMailClient.send(msg);
                });
                sendTread.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        tfContent.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.getKeyCode("Enter")){
                btSend.fire();
            }
        });
        btExit.setOnAction(e ->{
            exit();
        });
        primaryStage.setOnCloseRequest(e ->{
            exit();
        });
        BorderPane mainPane = new BorderPane();
        HBox hBox1 = new HBox();
        hBox1.setSpacing(5);
        hBox1.setPadding(new Insets(15,20,10,20));
        hBox1.setAlignment(Pos.CENTER);
        hBox1.getChildren().addAll(new Label("邮件服务器地址："), tfServerIP, new Label("邮件服务器端口："), tfServerPort);
        HBox hBox2 = new HBox();
        hBox2.setSpacing(5);
        hBox2.setPadding(new Insets(15,20,10,20));
        hBox2.setAlignment(Pos.CENTER);
        hBox2.getChildren().addAll(new Label("邮件发送者地址："), tfSendAddr, new Label("邮件接受者地址："), tfRcieveAddr);
        HBox hBox3 = new HBox();
        hBox3.setSpacing(5);
        hBox3.setPadding(new Insets(15,20,10,20));
        hBox3.setAlignment(Pos.CENTER);
        tfMailTitle.setPrefWidth(470);
        hBox3.getChildren().addAll(new Label("邮件标题："), tfMailTitle);
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(15,20,10,20));
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.getChildren().addAll(hBox1, hBox2,hBox3);
        mainPane.setTop(vBox);

        HBox hBox4 = new HBox();
        hBox4.setSpacing(5);
        hBox4.setPadding(new Insets(15,20,10,20));
        hBox4.setAlignment(Pos.CENTER);
        VBox vBox1 = new VBox();
        vBox1.getChildren().addAll(new Label("邮件正文"), tfContent);
        VBox vBox3 = new VBox();
        vBox3.getChildren().addAll(new Label("服务器反馈信息"), taDisplay);
        hBox4.getChildren().addAll(vBox1, vBox3);
        VBox vBox2 = new VBox();
        vBox2.getChildren().add(hBox4);
        mainPane.setCenter(vBox2);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(5,20,10,10));
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.getChildren().addAll(btSend, btExit);
        mainPane.setBottom(hBox);

        Scene scene = new Scene(mainPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
