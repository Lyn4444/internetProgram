package chapter08;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HTTPSClientFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Button btConnect = new Button("连接");
    private Button btRequest = new Button("网页请求");
    private Button btEmpty = new Button("清空");
    private Button btExit = new Button("退出");
    private TextField tfAddr = new TextField();
    private TextField tfPort = new TextField();
    private TextArea taDisplay = new TextArea();
    private HTTPSClient httpsClient;

    @Override
    public void start(Stage primaryStage) {
        BorderPane mainPane = new BorderPane();

        HBox hBox1 = new HBox();
        hBox1.setAlignment(Pos.TOP_CENTER);
        hBox1.setSpacing(10);
        hBox1.setPadding(new Insets(15, 20, 10, 20));
        hBox1.getChildren().addAll(new Label("网页地址："), tfAddr, new Label("端口"), tfPort, btConnect);
        mainPane.setTop(hBox1);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(new Label("网页信息显示区："), taDisplay);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(15, 20, 10, 20));
        mainPane.setCenter(vBox);

        HBox hBox2 = new HBox();
        hBox2.setAlignment(Pos.BOTTOM_RIGHT);
        hBox2.setSpacing(10);
        hBox2.setPadding(new Insets(20, 20, 10, 20));
        hBox2.getChildren().addAll(btRequest, btEmpty, btExit);
        mainPane.setBottom(hBox2);

        Scene scene = new Scene(mainPane);
        primaryStage.setScene(scene);
        primaryStage.show();

        btConnect.setOnAction(e -> {
            String address = tfAddr.getText().trim();
            String port = tfPort.getText().trim();
            try{
                httpsClient = new HTTPSClient(address, port);
                taDisplay.appendText("连接服务器成功！\n");
            }catch (IOException ex){
                taDisplay.appendText("服务器连接失败！" + ex.getMessage() + "\n");
            }
            new Thread(() -> {
                String msg = null;
                while((msg = httpsClient.receive()) != null){
                    String temMsg = msg;
                    Platform.runLater(() -> {
                        taDisplay.appendText(temMsg + "\n");
                    });
                }
                Platform.runLater(() -> {
                    taDisplay.appendText("对话已关闭！\n");
                });
            }).start();
        });

        btRequest.setOnAction(e ->{
            StringBuffer requestHead = new StringBuffer("GET / HTTP/1.1" + "\r\n");
            requestHead.append("HOST:" + tfAddr.getText().trim() + "\r\n");
            requestHead.append("Accept: */*" + "\r\n");
            requestHead.append("Accept-Language: zh-cn" + "\r\n");
            requestHead.append("User-Agent:Mozilla/5.0(Windows NT 10.0; Win64; x64)" + "\r\n");
            requestHead.append("Connection:Keep-Alive" + "\r\n\r\n");
            httpsClient.send(requestHead.toString());
        });

        btEmpty.setOnAction(e -> {
            taDisplay.clear();
        });

        btExit.setOnAction(e -> {
            if(httpsClient != null){
                httpsClient.close();
            }
            System.exit(0);
        });
    }
}
