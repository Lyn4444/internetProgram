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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class URLClientFX extends Application {

    private TextArea taDisplay = new TextArea();
    private TextField tfURL = new TextField();
    private Button btSend = new Button("发送");
    private Button btExit = new Button("退出");
    private BufferedReader br;

    public String receive(){
        String msg = null;
        try{
            msg = br.readLine();
        }catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return msg;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane mainPane = new BorderPane();
        taDisplay.setWrapText(true);
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(15, 20, 10, 15));
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.getChildren().addAll(btSend, btExit);
        mainPane.setBottom(hBox);

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(15,20,10,15));
        vBox.getChildren().addAll(new Label("网页信息显示区："), taDisplay, new Label("输入URL地址："), tfURL);
        mainPane.setCenter(vBox);

        Scene scene = new Scene(mainPane);
        primaryStage.setTitle("URLClientFX");
        primaryStage.setScene(scene);
        primaryStage.show();

        btSend.setOnAction(e -> {
            taDisplay.clear();
            String address = tfURL.getText().trim();
            try {
                URL url = new URL(address);
                System.out.printf("连接%s成功！\n", address);

                //获得url的字节流输入
                InputStream in = url.openStream();
                //装饰成字符输入流
                br = new BufferedReader(new InputStreamReader(in, "utf-8"));
                new Thread(() -> {
                    String msg = null;
                    while((msg = receive()) != null){
                        String temMsg = msg;
                        Platform.runLater(() -> {
                            taDisplay.appendText(temMsg + '\n');
                        });
                    }

                }).start();
            }catch (IOException ex){
                taDisplay.appendText("URL地址输入不合规则！");
            }
        });

        btExit.setOnAction(e -> {
            System.exit(0);
        });
    }
}
