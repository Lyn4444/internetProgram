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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class HTTPClientFX extends Application {

    private final Button btnExit = new Button("退出");
    private final Button btnSend = new Button("发送");
    private final Button btnDelete = new Button("清空");
    private final Button btnRequest = new Button("网页请求");
    private final Button bitConnect = new Button("连接");

    private final TextField tfSend = new TextField();
    private final TextArea taDisplay = new TextArea();

    private final TextField idInput = new TextField();
    private final TextField portInput = new TextField();

    private HTTPClient httpClient;
    private HTTPSClient httpsClient;

    private boolean flag;

    private InputStream inputStream;
    private BufferedReader br;

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
        hBoxTop.getChildren().addAll(new Label("网页地址："), idInput, new Label("端口："), portInput, bitConnect);
        mainPane.setTop(hBoxTop);

        HBox hBoxBottom = new HBox();
        hBoxBottom.setSpacing(10);
        hBoxBottom.setPadding(new Insets(10, 20, 10, 20));
        hBoxBottom.setAlignment(Pos.CENTER_RIGHT);
        hBoxBottom.getChildren().addAll(btnRequest, btnDelete, btnSend, btnExit);
        mainPane.setBottom(hBoxBottom);

        vBox.getChildren().addAll(new Label("网页信息显示区："), taDisplay, new Label("信息输入区："), tfSend);
        VBox.setVgrow(taDisplay, Priority.ALWAYS);
        mainPane.setCenter(vBox);

        Scene scene = new Scene(mainPane, 850, 500);
        primaryStage.setScene(scene);
        primaryStage.show();


        taDisplay.setEditable(false);

        btnSend.setOnAction(event -> {
            String address = tfSend.getText();
            taDisplay.clear();
            try {
                URL url = new URL(address);
                System.out.printf("链接%s成功\n", address);
                inputStream = url.openStream();
                br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                new Thread(() -> {
                    String msg = null;
                    taDisplay.appendText("对话开启\n");
                    while ((msg = receive()) != null){
                        String msgTemp = msg;
                        Platform.runLater(() -> {
                            taDisplay.appendText(msgTemp + "\n");
                        });
                    }
                    Platform.runLater(() -> {
                        taDisplay.appendText("对话关闭\n");
                    });
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnDelete.setOnAction(event -> {
            taDisplay.clear();
        });

        btnRequest.setOnAction(event -> {
            // "GET / HTTP/1.1"    第一个'/' -> 默认网页即首页 默认使用HTTP协议，1.1版本
            StringBuffer requestHead = new StringBuffer("GET / HTTP/1.1" + "\r\n");
            requestHead.append("HOST: " + idInput.getText() + "\r\n");
            // '*' -> 可接收任何格式
            requestHead.append("Accept: */*" + "\r\n");
            requestHead.append("Accept-Language: zh-cn" + "\r\n");
            // 检测用户端使用的设备
            requestHead.append("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64)" + "\r\n");
            requestHead.append("Connection: Keep-Alive" + "\r\n\r\n");

            if (flag) {
                httpClient.send(requestHead.toString());
            } else {
                httpsClient.send(requestHead.toString());
            }
        });


        btnExit.setOnAction(event -> {
            System.exit(0);
        });


        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });


        bitConnect.setOnAction(event -> {
            String ip = idInput.getText().trim();
            String port = portInput.getText().trim();
            System.out.println(ip);
            System.out.println(port);

            if (!port.equals("443")) {
                flag = true;
                try {
                    httpClient = new HTTPClient(ip, port);
                    new Thread(() -> {
                        String msg = null;
                        taDisplay.appendText("对话开启\n");
                        while ((msg = httpClient.receive()) != null) {
                            String msgTemp = msg;
                            Platform.runLater(() -> {
                                taDisplay.appendText(msgTemp + "\n");
                            });
                        }
                        Platform.runLater(() -> {
                            taDisplay.appendText("对话关闭\n");
                        });
                    }).start();
                } catch (Exception e) {
                    taDisplay.appendText("服务器连接失败！" + e.getMessage() + "\n");
                }
            }
            else {
                flag = false;
                try {
                    httpsClient = new HTTPSClient(ip, port);
                    new Thread(() -> {
                        String msg = null;
                        taDisplay.appendText("对话开启\n");
                        while ((msg = httpsClient.receive()) != null) {
                            String msgTemp = msg;
                            Platform.runLater(() -> {
                                taDisplay.appendText(msgTemp + "\n");
                            });
                        }
                        Platform.runLater(() -> {
                            taDisplay.appendText("对话关闭\n");
                        });
                    }).start();
                } catch (Exception e) {
                    taDisplay.appendText("服务器连接失败！" + e.getMessage() + "\n");
                }
            }
        });
    }

    private String receive() {
        String msg = null;
        try {
            //从网络输入字符流中读信息，每次只能接受一行信息
            //如果不够一行（无行结束符），则该语句阻塞，
            // 直到条件满足，程序才往下运行
            msg = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }
}
