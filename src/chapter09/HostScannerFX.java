package chapter09;

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
import java.net.InetAddress;


public class HostScannerFX extends Application {

    private final Button bitConnect = new Button("主机扫描");
    private final Button bitDone = new Button("执行命令");

    private final TextArea taDisplay = new TextArea();

    private final TextField firstInput = new TextField();
    private final TextField secondInput = new TextField();

    private final TextField cmdInput = new TextField();



    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        BorderPane mainPane = new BorderPane();
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 20, 10, 20));
        vBox.getChildren().addAll(new Label("扫描结果："), taDisplay);
        VBox.setVgrow(taDisplay, Priority.ALWAYS);
        mainPane.setCenter(vBox);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 20, 10, 20));
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(new Label("起始地址:"), firstInput, new Label("结束地址:"), secondInput, bitConnect);
        HBox hBox1 = new HBox();
        hBox1.setSpacing(10);
        hBox1.setPadding(new Insets(10, 20, 10, 20));
        hBox1.setAlignment(Pos.CENTER);
        cmdInput.setPrefWidth(450);
        hBox1.getChildren().addAll(new Label("输入命令格式："), cmdInput, bitDone);
        VBox vBox1 = new VBox();
        vBox.getChildren().addAll(hBox, hBox1);
        mainPane.setBottom(vBox1);


        Scene scene = new Scene(mainPane, 750, 500);
        primaryStage.setScene(scene);
        primaryStage.show();


        taDisplay.setEditable(false);



        bitConnect.setOnAction(event -> {
            String first = firstInput.getText().trim();
            String second = secondInput.getText().trim();

            int start = ipToInt(first);
            int end = ipToInt(second);

            try {
                new Thread(() -> {
                    for (int i = start; i <= end; i++ ) {
                        String host = intToIp(i);
                        Platform.runLater(() -> {
                            try {
                                InetAddress addr = InetAddress.getByName(host);
                                boolean status = addr.isReachable(10);
                                if (status) {
                                    taDisplay.appendText(host + " is reachable!\n");
                                } else {
                                    taDisplay.appendText(host + " is not reachable.\n");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        bitDone.setOnAction(event-> {
            try {
                String cmd = cmdInput.getText();
                Process process = Runtime.getRuntime().exec(cmd);
                InputStream inputStream = process.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "gbk"));
                String msg = null;
                while ((msg = bufferedReader.readLine()) != null) {
                    String msgTemp = msg;
                    Platform.runLater(() -> {
                        taDisplay.appendText(msgTemp + "\n");
                    });
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        });

    }

    public static int ipToInt(String ip) {
        String[] ipArray = ip.split("\\.");
        int num = 0;
        for (int i = 0; i < ipArray.length; i ++ ) {
            int valueOfSection = Integer.parseInt(ipArray[i]);
            num = (valueOfSection << 8 * (3 - i)) | num;
        }
        return num;
    }

    public static String intToIp(int ipInt) {
        return String.valueOf((ipInt >> 24) & 0xff) + '.' +
                ((ipInt >> 16) & 0xff) + '.' +
                ((ipInt >> 8) & 0xff) + '.' + (ipInt & 0xff);
    }
}
