package chapter11;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jpcap.JpcapSender;

import java.lang.reflect.InvocationTargetException;


public class SendPacketFX extends Application {

    private final TextField tfSrcPort = new TextField();
    private final TextField tfDstPort = new TextField();
    private final TextField tfSrcIP = new TextField();
    private final TextField tfDstIP = new TextField();
    private final TextField tfSrcMAC = new TextField();
    private final TextField tfDstMAC = new TextField();
    private final TextField tfData = new TextField();

    private final CheckBox cbSYN = new CheckBox("SYN");
    private final CheckBox cbACK = new CheckBox("ACK");
    private final CheckBox cbRST = new CheckBox("RST");
    private final CheckBox cbFIN = new CheckBox("FIN");

    private final Button btnSend = new Button("发送TCP包");
    private final Button btnSelect = new Button("选择网卡");
    private final Button btnQuit = new Button("退出");

    private NetworkChoiceDialog dialog;
    private JpcapSender sender;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane mainPane = new BorderPane();

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 20, 10, 20));
        hBox.setAlignment(Pos.CENTER);
        tfSrcPort.setPrefWidth(150);
        tfDstPort.setPrefWidth(150);
        hBox.getChildren().addAll(
                new Label("源端口："), tfSrcPort,
                new Label("目的端口："), tfDstPort
        );

        HBox hBox1 = new HBox();
        hBox1.setSpacing(10);
        hBox1.setPadding(new Insets(10, 20, 10, 20));
        hBox1.setAlignment(Pos.CENTER);
        cbSYN.setSelected(true);
        hBox1.getChildren().addAll(
                new Label("TCP标识位："), cbSYN, cbACK, cbRST, cbFIN
        );

        HBox hBox2 = new HBox();
        hBox2.setSpacing(10);
        hBox2.setPadding(new Insets(10, 20, 10, 20));
        hBox2.setAlignment(Pos.CENTER);
        hBox2.getChildren().addAll(
                btnSend, btnSelect, btnQuit
        );

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 20, 10, 20));
        vBox.getChildren().addAll(
                hBox, hBox1,
                new Label("源主机地址"), tfSrcIP,
                new Label("目的主机地址"), tfDstIP,
                new Label("源MAC地址"), tfSrcMAC,
                new Label("目的MAC地址"), tfDstMAC,
                new Label("发送的数据"), tfData,
                hBox2
        );

        mainPane.setCenter(vBox);

        Scene scene = new Scene(mainPane, 570, 530);
        primaryStage.setScene(scene);
        primaryStage.setTitle("发送自构包");
//        dialog = new NetworkChoiceDialog(primaryStage);
//        dialog.showAndWait();
//        sender = dialog.getSender();
        primaryStage.show();

        btnSend.setOnAction(event -> {
            if (sender == null) {
                sender = dialog.getSender();
                System.out.println(sender);
            }
            try {
                int srcPort = Integer.parseInt(tfSrcPort.getText().trim());
                int dstPort = Integer.parseInt((tfDstPort.getText().trim()));
                String srcHost = tfSrcIP.getText().trim();
                String dstHost = tfDstIP.getText().trim();
                String srcMAC = tfSrcMAC.getText().trim();
                String dstMAC = tfDstMAC.getText().trim();
                String data = tfData.getText();
                PacketSender.sendTCPPacket(sender, srcPort, dstPort, srcHost,
                        dstHost, data, srcMAC, dstMAC,cbSYN.isSelected(),
                        cbACK.isSelected(),cbRST.isSelected(),cbFIN.isSelected());

                new Alert(Alert.AlertType.INFORMATION, "已发送！").showAndWait();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            }
        });

        btnSelect.setOnAction(event -> {
            if (sender == null) {
                try {
                    dialog = new NetworkChoiceDialog(primaryStage);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
//                dialog.showAndWait();
                sender = dialog.getSender();
                System.out.println(sender);
            }
        });

        btnQuit.setOnAction(event -> System.exit(0));

    }
}
