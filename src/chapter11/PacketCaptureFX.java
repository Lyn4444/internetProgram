package chapter11;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;


public class PacketCaptureFX extends Application {

    private final TextArea taDisplay = new TextArea();

    private final Button btnFirst = new Button("开始抓包");
    private final Button btnSecond = new Button("停止抓包");
    private final Button btnThird = new Button("清空");
    private final Button btnFourth = new Button("设置");
    private final Button btnFifth = new Button("退出");

    private JpcapCaptor jpcapCaptor;
    private NetworkChoiceDialog configDialog;
    private Thread thread;
    private String keyData;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane mainPane = new BorderPane();

        taDisplay.setPrefWidth(800);
        taDisplay.setPrefHeight(450);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 20, 10, 20));
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(btnFirst, btnSecond, btnThird, btnFourth, btnFifth);

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 20, 10, 20));

        vBox.getChildren().addAll(taDisplay, hBox);

        mainPane.setCenter(vBox);

        Scene scene = new Scene(mainPane, 850, 500);
        primaryStage.setScene(scene);
        primaryStage.show();

        btnFirst.setOnAction(event -> {
            if (jpcapCaptor == null) {
                jpcapCaptor = configDialog.getJpcapCaptor();
                keyData = configDialog.getKeyData();
            }

            Task<Void> task = new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    while (!Thread.currentThread().isInterrupted()) {
                        jpcapCaptor.processPacket(1, new PacketReceive());
                    }
                    return null;
                }
            };
            thread = new Thread(task, "captureThread");
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        });

        btnSecond.setOnAction(event -> {
            interrupt("captureThread");
        });

        btnThird.setOnAction(event -> {
            taDisplay.clear();
        });

        btnFourth.setOnAction(event -> {
            try {
                configDialog = new NetworkChoiceDialog(primaryStage);
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
            jpcapCaptor = configDialog.getJpcapCaptor();
        });

        btnFifth.setOnAction(event -> {
            System.exit(0);
        });
    }

    private void interrupt(String threadName) {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        int noThreads = threadGroup.activeCount();
        Thread[] listThreads = new Thread[noThreads];
        threadGroup.enumerate(listThreads);
        for (int i = 0; i < noThreads; i++ ) {
            if (listThreads[i].getName().equals(threadName)) {
                listThreads[i].interrupt();
            }
        }
    }

     class PacketReceive implements PacketReceiver {
        @Override
        public void receivePacket(Packet packet) {
            System.out.println(packet.toString());;
            if (keyData == null || keyData.trim().equalsIgnoreCase("")) {
                return;
            }
            try {
                String[] keyList = keyData.split(" ");
                String msg = new String(packet.data, 0, packet.data.length, StandardCharsets.UTF_8);
                for (String key: keyList) {
                    if (msg.toUpperCase().contains(key.toUpperCase())) {
                        Platform.runLater(() -> {
                            taDisplay.appendText("数据部分：" + msg + "\n\n");
                        });
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
