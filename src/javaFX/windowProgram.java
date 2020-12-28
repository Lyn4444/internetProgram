package javaFX;

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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Scanner;


public class windowProgram extends Application {

    private Button btnExit = new Button("退出");
    private Button btnSend = new Button("发送");
    private Button btnOpen = new Button("加载");
    private Button btnSave = new Button("保存");
    //待发送信息的文本框
    private TextField tfSend = new TextField();
    //显示信息的文本区域
    private TextArea taDisplay = new TextArea();
    //文件操作
    private TextFileIO textFileIO = new TextFileIO();



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane mainPane = new BorderPane();
        //内容显示区域
        VBox vBox = new VBox();
        vBox.setSpacing(10);//各控件之间的间隔
        //VBox面板中的内容距离四周的留空区域
        vBox.setPadding(new Insets(10,20,10,20));
        vBox.getChildren().addAll(new Label("信息显示区："), taDisplay,new Label("信息输入区："), tfSend);
        //设置显示信息区的文本区域可以纵向自动扩充范围
        VBox.setVgrow(taDisplay, Priority.ALWAYS);
        mainPane.setCenter(vBox);
        //底部按钮区域
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10,20,10,20));
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(btnSend,btnSave,btnOpen,btnExit);
        mainPane.setBottom(hBox);
        Scene scene = new Scene(mainPane,700,400);
        primaryStage.setScene(scene);
        primaryStage.show();

        //文本区域taDisplay控件只读设置
        taDisplay.setEditable(false);
        //退出按钮
        btnExit.setOnAction(event -> {System.exit(0);});
        //发送按钮
        btnSend.setOnAction(event -> {
            String msg = tfSend.getText();
            //文本区域taDisplay控件自动换行设置
            taDisplay.appendText(msg + "\n");
            tfSend.clear();
        });
        //回车响应功能
        tfSend.setOnKeyPressed(event -> {
            String msg = tfSend.getText();
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                if (event.isShiftDown()) {
                    if(!tfSend.getText().isEmpty()) {
                        taDisplay.appendText("echo: " + msg + "\n");
                        tfSend.clear();
                    }
                } else {
                    if(!tfSend.getText().isEmpty()) {
                        taDisplay.appendText(msg + "\n");
                        tfSend.clear();
                    }
                }
            }
        });
        //保存按钮
        btnSave.setOnAction(event -> {
            //添加当前时间信息进行保存
            textFileIO.append(LocalDateTime.now().withNano(0) + "" + "\n" + taDisplay.getText());
        });
        //加载按钮
        btnOpen.setOnAction(event -> {
            String msg = textFileIO.load();
            if(msg != null){
                taDisplay.clear();
                taDisplay.setText(msg);
            }
        });

    }
}


class TextFileIO {
    private PrintWriter pw = null;
    private Scanner sc = null;

    public TextFileIO() {
    }

    public void append(String msg) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(null);
        if(file == null) //用户放弃操作则返回
            return;
        //以追加模式utf-8的编码模式写到文件中
        try {
            pw = new PrintWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file, true), "utf-8"));
            pw.println(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pw.close();
        }
    }

    public String load() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if(file == null) //用户放弃操作则返回
            return null;
        StringBuilder sb = new StringBuilder();
        try {
            //读和写的编码要注意保持一致
            sc = new Scanner(file,"utf-8");
            while (sc.hasNext()) {
                sb.append(sc.nextLine() + "\n"); //补上行读取的行末尾回车
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sc.close();
        }
        return sb.toString();
    }
}
