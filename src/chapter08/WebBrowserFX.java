package chapter08;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.List;

/**
 * @ProjectName internetProgram
 * @ClassName WebBrowserFX
 * @Description TODO
 * @Author Lyn
 * @Date 2020/10/26 16:10
 * @Version 1.0
 */
public class WebBrowserFX extends Application {

    private final Button btnNew = new Button("刷新");
    private final Button btnAhead = new Button("前进");
    private final Button btnBehind = new Button("后退");
    private final Button btnFirst = new Button("首页");
    private final Button btnTurn = new Button("跳转");

    private final TextField tfSend = new TextField();

    private WebEngine webEngine;
    private WebHistory history;


    @Override
    public void start(Stage primaryStage) throws Exception {

        btnAhead.setDisable(true);
        btnBehind.setDisable(true);

        tfSend.setPrefWidth(600);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        BorderPane mainPane = new BorderPane();
        HBox hBoxTop = new HBox();
        hBoxTop.setSpacing(10);
        hBoxTop.setAlignment(Pos.CENTER);
        hBoxTop.getChildren().addAll(btnNew, btnAhead, btnBehind, btnFirst, tfSend, btnTurn);
        mainPane.setTop(hBoxTop);

        WebView webView = new WebView();
        webEngine = webView.getEngine();
        history = webEngine.getHistory();
        List<WebHistory.Entry> list = history.getEntries();

        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                tfSend.setText(list.get(history.getCurrentIndex()).getUrl());
                btnAhead.setDisable(false);
                btnBehind.setDisable(false);
                if (history.getCurrentIndex() == 0) {
                    btnAhead.setDisable(true);
                }
                if (history.getCurrentIndex() == list.size() - 1) {
                    btnBehind.setDisable(true);
                }
            }
        });


        scrollPane.setContent(webView);
        mainPane.setCenter(scrollPane);

        Scene scene = new Scene(mainPane, 1050, 800);
        primaryStage.setScene(scene);
        primaryStage.show();


        btnTurn.setOnAction(event -> {
            String address = tfSend.getText().trim();
            webEngine.setUserAgent("java/1.8");
            webEngine.load(address);
            btnAhead.setDisable(false);
            btnBehind.setDisable(false);
        });

        btnFirst.setOnAction(event -> {
            webEngine.load(list.get(0).getUrl());
        });

        btnNew.setOnAction(event -> {
            webEngine.load(list.get(history.getCurrentIndex()).getUrl());
        });

        btnAhead.setOnAction(event -> {
            history.go(-1);
        });

        btnBehind.setOnAction(event -> {
            history.go(1);
        });
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
