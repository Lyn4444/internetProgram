package chapter11;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ProjectName internetProgram
 * @ClassName ConfigDialog
 * @Description TODO
 * @Author Lyn
 * @Date 2020/11/8 23:20
 * @Version 1.0
 * @Function
 */

public class ConfigDialog {
    private JpcapCaptor jpcapCaptor;//用于返回给主窗体
    //网卡列表
    private final NetworkInterface[] devices = JpcapCaptor.getDeviceList();
    private JpcapSender sender;
    private final Stage stage = new Stage();//对话框窗体

    private boolean flag = false;

    private TextField tfKeyData;

    //parentStage表示主程序的stage，传值可通过这种构造方法参数的方式
    public ConfigDialog(Stage parentStage) throws InvocationTargetException, IllegalAccessException {
        //设置该对话框的父窗体为调用者的那个窗体
        stage.initOwner(parentStage);
        //设置为模态窗体，即不关闭就不能切换焦点
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
        stage.setTitle("选择网卡并设置参数");

        //窗体主容器
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 20, 10, 20));

        //网卡选择列表，使用组合下拉框控件
        ComboBox<String> cob = new ComboBox<>();
        cob.setMaxWidth(500);
        for (int i = 0; i < devices.length; i++) {
            Pattern pattern = Pattern.compile("\\{(.*?)\\}");
            Matcher matcher = pattern.matcher(devices[i].name);
            String registry = null;
            while (matcher.find()) {
                registry = matcher.group(0);
            }
            if (registry == null) {
                cob.getItems().add(i + " : " + devices[i].description);
            }
            else {
                int DRIVER_CLASS_ROOT = WinRegistry.HKEY_LOCAL_MACHINE;
                String DRIVER_CLASS_PATH = "SYSTEM\\CurrentControlSet\\Control\\Class";
                String NETCFG_INSTANCE_KEY = "NetCfgInstanceId";
                for (String driverClassSubkey : WinRegistry.readStringSubKeys(DRIVER_CLASS_ROOT, DRIVER_CLASS_PATH, 0)) {
                    for (String driverSubkey : WinRegistry.readStringSubKeys(DRIVER_CLASS_ROOT, DRIVER_CLASS_PATH + "\\" + driverClassSubkey, 0)) {
                        String path = DRIVER_CLASS_PATH + "\\" + driverClassSubkey + "\\" + driverSubkey;
                        String netCfgInstanceId = WinRegistry.readString(DRIVER_CLASS_ROOT, path, NETCFG_INSTANCE_KEY, 0);
                        String theDriverName = "";
                        if (netCfgInstanceId != null && netCfgInstanceId.equalsIgnoreCase(registry)) {
                            theDriverName = trimOrDefault(WinRegistry.readString(DRIVER_CLASS_ROOT, path, "DriverDesc", 0), "");
                            cob.getItems().add(i + " : " + theDriverName);
                            break;
                        }
                    }
                }
            }
        }
        //默认选择第一项
        cob.getSelectionModel().selectFirst();

        //设置抓包过滤
        TextField tfFilter = new TextField();

        //设置抓包大小（一般建议在68-1514之间，默认1514）
        TextField tfSize = new TextField("1514");

        //是否设置混杂模式
        CheckBox cb = new CheckBox("是否设置为混杂模式");
        cb.setSelected(true); //默认选中
        //底部确定和取消按钮
        HBox hBoxBottom = new HBox();

        Button btnConfirm = new Button("确定");
        Button btnCancel = new Button("取消");
        hBoxBottom.setAlignment(Pos.CENTER);
        hBoxBottom.setSpacing(10);
        hBoxBottom.setPadding(new Insets(10, 20, 10, 20));
        hBoxBottom.getChildren().addAll(btnConfirm, btnCancel);

        Hyperlink hyperlink = new Hyperlink();
        hyperlink.setText("设置抓包过滤器（点击浏览参考语法）：");

        tfKeyData = new TextField();

        //将各组件添加到主容器
        vBox.getChildren().addAll(new Label("请选择网卡："), cob,
                hyperlink, tfFilter,
                new Label("包中数据包含的关键字，匹配则显示数据内容（多个关键字为or关系，用空格隔开）"), tfKeyData,
                new Label("设置抓包大小（建议介于68~1514之间）："), tfSize, cb,
                new Separator(),
                hBoxBottom
        );

        Scene scene = new Scene(vBox, 500, 120);
        stage.setScene(scene);
        stage.show(); //不要显示对话框，由主窗体调用显示
        //确定按钮动作事件
        btnConfirm.setOnAction(event -> {
            try {
                int index = cob.getSelectionModel().getSelectedIndex();
                sender = JpcapSender.openDevice(devices[index]);
                //选择的网卡接口
                NetworkInterface networkInterface = devices[index];
                //抓包大小
                int snapLen = Integer.parseInt(tfSize.getText().trim());
                //是否混杂模式
                boolean promisc = cb.isSelected();
                jpcapCaptor = JpcapCaptor.openDevice(networkInterface, snapLen,
                        promisc, 20);
                if (jpcapCaptor != null) {
                    flag = true;
                }
                jpcapCaptor.setFilter(tfFilter.getText(), true);
                stage.hide();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            }
        });

        //取消按钮动作事件
        btnCancel.setOnAction(event -> {
            stage.hide();
        });

        hyperlink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Desktop.getDesktop().browse(new URI("https://cosy.univ-reims.fr/~lsteffenel/cours/Master1/Reseaux/0910/captureFilters.htm"));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean isClick() {
        return flag;
    }

    //主程序调用，获取设置了参数的JpcapCaptor对象
    public JpcapCaptor getJpcapCaptor() {
        System.out.println(jpcapCaptor);
        return jpcapCaptor;
    }

    //主程序调用，阻塞式显示界面
    public void showAndWait() {
        stage.showAndWait();
    }

    private static String trimOrDefault (String str, String def) {
        str = (str == null) ? "" : str.trim();
        return str.isEmpty() ? def : str;
    }

    public String getKeyData() {
        return tfKeyData.getText();
    }

    public JpcapSender getSender() {
        return sender;
    }

}
