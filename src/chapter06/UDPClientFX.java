package chapter06;

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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class UDPClientFX extends Application {



    private final Button btnExit = new Button("退出");
    private final Button btnSend = new Button("发送");
    private final Button bitConnect = new Button("连接");

    private final TextField tfSend = new TextField();
    private final TextArea taDisplay = new TextArea();

    private final TextField idInput = new TextField();
    private final TextField portInput = new TextField();

    private UDPClient udpClient;
    private Thread thread;


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        BorderPane mainPane = new BorderPane();
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 20, 10, 20));

        HBox hBoxTop = new HBox();
        hBoxTop.setSpacing(10);
        hBoxTop.setPadding(new Insets(10, 20, 10, 20));
        hBoxTop.setAlignment(Pos.CENTER);
        hBoxTop.getChildren().addAll(new Label("ip地址："), idInput, new Label("端口："), portInput, bitConnect);
        mainPane.setTop(hBoxTop);

        HBox hBoxBottom = new HBox();
        hBoxBottom.setSpacing(10);
        hBoxBottom.setPadding(new Insets(10, 20, 10, 20));
        hBoxBottom.setAlignment(Pos.CENTER_RIGHT);
        hBoxBottom.getChildren().addAll(btnSend, btnExit);
        mainPane.setBottom(hBoxBottom);

        vBox.getChildren().addAll(new Label("信息显示区："), taDisplay, new Label("信息输入区："), tfSend);
        VBox.setVgrow(taDisplay, Priority.ALWAYS);
        mainPane.setCenter(vBox);

        Scene scene = new Scene(mainPane, 850, 500);
        primaryStage.setScene(scene);
        primaryStage.show();


        taDisplay.setEditable(false);

        btnSend.setOnAction(event -> {
//            String msg = tfSend.getText();
//            clientConnectServer(msg);
            StringBuffer Msg = new StringBuffer();
            Msg.append("20181002837&罗杰鸿" + "\n");
            Msg.append("1604907194:307663 /10.173.42.152->/202.116.192.6 protocol(6) priority(0)  hop(128)  offset(0) ident(8273) TCP 63409 > 443 seq(1499039141) win(64240)  S" + "\n");
            Msg.append("1604907194:319919 /202.116.192.6->/10.173.42.152 protocol(6) priority(0)  hop(58)  offset(0) ident(0) TCP 443 > 63408 seq(2889468560) win(29200) ack 2739637171  S" + "\n");
            Msg.append("1604907194:320123 /10.173.42.152->/202.116.192.6 protocol(6) priority(0)  hop(128)  offset(0) ident(8274) TCP 63408 > 443 seq(2739637171) win(513) ack 2889468561" + "\n");
            udpClient.send(Msg.toString());
            taDisplay.appendText(Msg + "\n");
        });



        tfSend.setOnKeyPressed(event -> {
            String msg = tfSend.getText();
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                clientConnectServer(msg);
            }
        });


        btnExit.setOnAction(event -> {
            exit();
        });


        primaryStage.setOnCloseRequest(event -> {
            exit();
        });


        bitConnect.setOnAction(event -> {
            String ip = idInput.getText().trim();
            String port = portInput.getText().trim();

            try {
                udpClient = new UDPClient(ip, port);
                System.out.println("ok");
                thread = new Thread(() -> {
                    String msg = null;
                    while((msg = udpClient.receive()) != null) {
                        String msgTemp = msg;
                        Platform.runLater(() -> {
                            taDisplay.appendText(msgTemp + "\n");
                        });
                    }
                    Platform.runLater(() -> {
                        taDisplay.appendText("对话关闭\n");
                    });
                });
                thread.start();
            } catch (Exception e) {
                taDisplay.appendText("服务器连接失败！" + e.getMessage() + "\n");
            }
        });
    }


    private void clientConnectServer(String Msg) {
        if (!Msg.isEmpty()) {
            udpClient.send(Msg);
            taDisplay.appendText("客户端发送：" + Msg + "\n");
            tfSend.clear();
        }
    }


    private void exit(){
        if(udpClient != null){
            udpClient.close();
        }
        System.exit(0);
    }
}
