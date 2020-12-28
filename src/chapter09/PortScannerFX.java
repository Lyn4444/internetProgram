package chapter09;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;


public class PortScannerFX extends Application {

    private final Button bitFirst = new Button("扫描");
    private final Button bitSecond = new Button("快速扫描");
    private final Button bitThree = new Button("多线程扫描");
    private final Button bitFour = new Button("退出");

    private final TextArea taDisplay = new TextArea();

    private final TextField firstInput = new TextField();
    private final TextField secondInput = new TextField();
    private final TextField threeInput = new TextField();

    private final ProgressBar progressBar = new ProgressBar();

    private String ip = null;
    private String start = null;
    private String end = null;

    public static AtomicInteger portCount = new AtomicInteger(0);

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        BorderPane mainPane = new BorderPane();
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 20, 10, 20));
        progressBar.setPrefWidth(600);
        vBox.getChildren().addAll(new Label("扫描结果："), taDisplay, progressBar);
        VBox.setVgrow(taDisplay, Priority.ALWAYS);
        mainPane.setCenter(vBox);


        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 20, 10, 20));
        hBox.setAlignment(Pos.CENTER);
        secondInput.setPrefWidth(100);
        threeInput.setPrefWidth(100);
        hBox.getChildren().addAll(new Label("目的主机IP:"), firstInput, new Label("开始端口号:"), secondInput, new Label("结束端口号:"), threeInput);
        HBox hBox1 = new HBox();
        hBox1.setSpacing(10);
        hBox1.setPadding(new Insets(10, 20, 10, 20));
        hBox1.setAlignment(Pos.CENTER);
        hBox1.getChildren().addAll(bitFirst, bitSecond, bitThree, bitFour);
        VBox vBox1 = new VBox();
        vBox1.setSpacing(20);
        vBox1.setPadding(new Insets(10, 20, 10, 20));
        vBox1.getChildren().addAll(hBox, hBox1);
        mainPane.setBottom(vBox1);


        Scene scene = new Scene(mainPane, 850, 500);
        primaryStage.setScene(scene);
        primaryStage.show();


        taDisplay.setEditable(false);

        bitFirst.setOnAction(event -> {
            ip = firstInput.getText().trim();
            start = secondInput.getText().trim();
            end = threeInput.getText().trim();

            for (int i = Integer.parseInt(start); i <= Integer.parseInt(end); i++ ) {
                try {
                    Socket socket = new Socket(ip, i);
                    socket.close();
                    String msg = "端口" + i + " is open\n";
                    Platform.runLater(()-> taDisplay.appendText(msg));
                } catch (IOException e) {
                    String msg = "端口" + i + " is closed\n";
                    taDisplay.appendText(msg);
                }
                progressBar.setProgress(1.0 * (i - Integer.parseInt(start)) / (Integer.parseInt(end) - Integer.parseInt(start)));
                progressBar.getProgress();
            }
        });

        bitSecond.setOnAction(event -> {
            ip = firstInput.getText().trim();
            start = secondInput.getText().trim();
            end = threeInput.getText().trim();

            for (int i = Integer.parseInt(start); i <= Integer.parseInt(end); i++ ) {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, i), 150);
                    socket.close();
                    String msg = "端口" + i + " is open\n";
                    Platform.runLater(()-> taDisplay.appendText(msg));
                } catch (IOException e) {
                    String msg = "端口" + i + " is closed\n";
                    taDisplay.appendText(msg);
                }
                progressBar.setProgress(1.0 * (i - Integer.parseInt(start)) / (Integer.parseInt(end) - Integer.parseInt(start)));
                progressBar.getProgress();
            }
        });

        bitThree.setOnAction(event -> {
            for (int i = 0; i < 10; i++ ) {
                new Thread(new ScanHandler(i)).start();
            }
        });

        bitFour.setOnAction(event -> System.exit(0));
    }


    class ScanHandler implements Runnable {
        private final int totalThreadNum;//用于端口扫描的总共线程数，默认为10
        private final int threadNo;//线程号，表示第几个线程

        public ScanHandler(int threadNo) {
            this.totalThreadNum = 10;
            this.threadNo = threadNo;
        }

        public ScanHandler(int threadNo, int totalThreadNum) {
            this.totalThreadNum = totalThreadNum;
            this.threadNo = threadNo;
        }

        @Override
        public void run() {
            ip = firstInput.getText().trim();
            start = secondInput.getText().trim();
            end = threeInput.getText().trim();

            for (int i = Integer.parseInt(start) + threadNo; i <= Integer.parseInt(end); i = i + totalThreadNum) {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, i), 200);
                    socket.close();
                    String msg = "端口" + i + " is open\n";
                    Platform.runLater(()-> taDisplay.appendText(msg));
                } catch (IOException e) {
//                    String msg = "端口" + i + " is closed\n";
//                    Platform.runLater(()->{
//                        taDisplay.appendText(msg);
//                    });
                }
                portCount.getAndIncrement();
            }
            if (portCount.get() == (Integer.parseInt(end) - Integer.parseInt(start) + 1)) {
                portCount.incrementAndGet();//加1，使得不再输出下面的线程扫描结束的信息
                Platform.runLater(()-> taDisplay.appendText("\n----------------多线程扫描结束--------------------\n"));
            }
        }
    }

}


