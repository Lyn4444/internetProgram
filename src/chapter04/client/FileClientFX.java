package chapter04.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;


public class FileClientFX extends Application {

    private String ip;
    private String port;

    private final Button btnExit = new Button("退出");
    private final Button btnSend = new Button("发送");
    private final Button bitConnect = new Button("连接");
    private final Button bitDownload = new Button("下载");

    private final TextField tfSend = new TextField();
    private final TextArea taDisplay = new TextArea();

    private final TextField idInput = new TextField();
    private final TextField portInput = new TextField();

    private FileDialogClient fileDialogClient;
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
        hBoxBottom.getChildren().addAll(btnSend, bitDownload, btnExit);
        mainPane.setBottom(hBoxBottom);

        vBox.getChildren().addAll(new Label("信息显示区："), taDisplay, new Label("信息输入区："), tfSend);
        VBox.setVgrow(taDisplay, Priority.ALWAYS);
        mainPane.setCenter(vBox);

        Scene scene = new Scene(mainPane, 850, 500);
        primaryStage.setScene(scene);
        primaryStage.show();


        taDisplay.setEditable(false);
        taDisplay.selectionProperty().addListener(((observable, oldValue, newValue) -> {
            if (!taDisplay.getSelectedText().equals(""))
                tfSend.setText((taDisplay.getSelectedText()));
        }));

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
            exit();
        });


        primaryStage.setOnCloseRequest(event -> {
            exit();
        });


        bitConnect.setOnAction(event -> {
            ip = idInput.getText().trim();
            port = portInput.getText().trim();

            try {
                fileDialogClient = new FileDialogClient(ip,port);
                thread = new Thread(() -> {
                    String msg = null;
                    while((msg = fileDialogClient.receive()) != null) {
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

        bitDownload.setOnAction(event -> {
            if (tfSend.getText().equals("")) return;
            String fName = tfSend.getText().trim();
            tfSend.clear();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(fName);
            File saveFile = fileChooser.showSaveDialog(null);
            if (saveFile == null) return;
            try {
                new FileDataClient(ip, "2020").getFile(saveFile);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(saveFile.getName() + " 下载完毕！");
                alert.showAndWait();
                fileDialogClient.send("客户端开启下载");

            } catch (IOException e) {
                e.printStackTrace();
            }

        });

    }


    public void clientConnectServer(String Msg) {
        if (!Msg.isEmpty()) {
            fileDialogClient.send(Msg);
            taDisplay.appendText("客户端发送：" + Msg + "\n");
            tfSend.clear();
        }
    }


    private void exit(){
        if(fileDialogClient != null) {
            fileDialogClient.send("bye");
            thread.interrupt();
            fileDialogClient.close();
        }
        System.exit(0);
    }
}
