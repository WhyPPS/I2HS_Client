package club.hsspace.i2hs.controller;

import club.hsspace.i2hs.ContainerEnum;
import club.hsspace.i2hs.RunApplication;
import club.hsspace.whypps.action.Init;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.framework.manage.RunningSpace;
import club.hsspace.whypps.framework.run.WhyPPSFramework;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.handle.TcpHandle;
import club.hsspace.whypps.manage.Authentication;
import club.hsspace.whypps.manage.ContainerManage;
import club.hsspace.whypps.manage.LocalHost;
import club.hsspace.whypps.model.Certificate;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

/**
 * @ClassName: InitController
 * @CreateTime: 2022/7/17
 * @Comment: 初始化应用
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class InitController {

    private static final Logger logger = LoggerFactory.getLogger(InitController.class);

    @FXML
    private Label inform;

    @FXML
    private BorderPane root;

    private Properties prop;

    @Injection
    private ContainerManage containerManage;

    @Injection
    private WhyPPSFramework whyPPSFramework;

    @Init
    private void initLink(LocalHost localHost, RunningSpace runningSpace, Authentication authentication) throws IOException, InterruptedException {
        prop = new Properties();
        prop.load(runningSpace.getInputStream("/server.properties"));
        String address = prop.getProperty("server.address", "127.0.0.1");
        String port = prop.getProperty("server.port", "2683");

        TcpHandle connect = localHost.connect(address, Integer.parseInt(port));
        logger.info("已建立链接{}", connect.getSocket().getInetAddress());
        Platform.runLater(() -> inform.setText("正在申请本地证书"));
        Thread.sleep(100);

        String cer = prop.getProperty("server.certificate");
        Certificate remote = authentication.getCertificate(cer);
        Certificate localCer = connect.applyCertificate(remote, null);
        logger.info("已申请本地证书{}", localCer.getSign());
        Platform.runLater(() -> inform.setText("正在建立DataStream链接"));
        Thread.sleep(100);

        DataStream dataStream = connect.buildConnection(remote);
        containerManage.registerObject(ContainerEnum.GlobalDataStream, dataStream);
        logger.info("已建立DataStream链接");
        Platform.runLater(() -> inform.setText("链接服务器网关成功，正在初始化界面"));
        Thread.sleep(100);

        FXMLLoader fxmlLoader = new FXMLLoader(RunApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LoginController controller = fxmlLoader.getController();

        Stage initStage = (Stage) root.getScene().getWindow();
        Platform.runLater(() -> {
            initStage.close();
            Stage stage = new Stage();
            stage.setTitle("登入系统......");
            stage.setScene(scene);
            stage.show();

            stage.setOnCloseRequest(event -> {
                logger.info("正在关闭程序");
                try {
                    whyPPSFramework.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            try {
                containerManage.injection(controller);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });

    }

    @FXML
    void initialize() {

    }

}
