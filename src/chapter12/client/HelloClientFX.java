package chapter12.client;

import chapter12.RmiKitServiceImpl;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rmi.RmiMsgService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class HelloClientFX extends Application {

    private final TextArea taDisplay = new TextArea();
    private final TextField tfMessage = new TextField();
    private final TextField tfNum = new TextField();
    private final TextField tfName = new TextField();

    private final Button btnEcho = new Button("发送信息");
    private final Button btnGetTime = new Button("发送学号和姓名");

    private RmiMsgService rmiMsgService;
    private RmiKitServiceImpl rmiKitService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox main = new VBox();
        main.setSpacing(10);
        main.setPadding(new Insets(10, 20, 10, 20));

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 20, 10, 20));
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(new Label("输入信息："), tfMessage,
                btnEcho, new Label("学号："), tfNum, new Label("姓名："), tfName, btnGetTime);
        main.getChildren().addAll(new Label("信息输入区："), taDisplay, hBox);

        Scene scene = new Scene(main);
        primaryStage.setScene(scene);
        primaryStage.show();


        new Thread(this::rmiInit).start();

        btnEcho.setOnAction(event -> {
            try {
                String msg = tfMessage.getText().trim();
                taDisplay.appendText(rmiMsgService.send(msg) + "\n");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        btnGetTime.setOnAction(event -> {
            try {
                String num = tfNum.getText().trim();
                String name = tfName.getText().trim();
                String msg = rmiMsgService.send(num, name);
                taDisplay.appendText(msg + "\n");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });


    }

    public void rmiInit() {
        try {
            Registry registry = LocateRegistry.getRegistry("202.116.195.71", 1099);
            System.out.println("RMI远程服务别名列表");

            for (String name: registry.list()) {
                System.out.println(name);
            }

             rmiKitService = (RmiKitServiceImpl) registry.lookup("RmiKitService");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
