package chapter02;

import javafx.application.Application;
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


public class TCPClientFX extends Application {

    private final Button btnExit = new Button("退出");
    private final Button btnSend = new Button("发送");
    private final Button bitConnect = new Button("连接");

    private final TextField tfSend = new TextField();
    private final TextArea taDisplay = new TextArea();

    private final TextField idInput = new TextField();
    private final TextField portInput = new TextField();

    private TCPClient tcpClient;

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
            String msg = tfSend.getText();
            clientConnectServer(msg);
        });



        tfSend.setOnKeyPressed(event -> {
            String msg = tfSend.getText();
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                clientConnectServer(msg);
            }
        });


        btnExit.setOnAction(event -> {
            if(tcpClient != null){
                //向服务器发送关闭连接的约定信息
                tcpClient.send("bye");
                tcpClient.close();
            }
            System.exit(0);
        });


        primaryStage.setOnCloseRequest(event -> {
            if(tcpClient != null){
                //向服务器发送关闭连接的约定信息
                tcpClient.send("bye");
                tcpClient.close();
            }
            System.exit(0);
        });


        bitConnect.setOnAction(event -> {
            String ip = idInput.getText().trim();
            String port = portInput.getText().trim();

            try {
                //tcpClient不是局部变量，是本程序定义的一个TCPClient类型的成员变量
                tcpClient = new TCPClient(ip,port);
                //成功连接服务器，接收服务器发来的第一条欢迎信息
                String firstMsg = tcpClient.receive();
                taDisplay.appendText(firstMsg + "\n");
            } catch (Exception e) {
                taDisplay.appendText("服务器连接失败！" + e.getMessage() + "\n");
            }
        });
    }


    public void clientConnectServer(String Msg) {
        if (!Msg.isEmpty()) {
            tcpClient.send(Msg);
            taDisplay.appendText("客户端发送：" + Msg + "\n");
            String receiveMsg = tcpClient.receive();
            taDisplay.appendText(receiveMsg + "\n");
            tfSend.clear();
        }
    }
}
