package club.hsspace.i2hs.controller;

import club.hsspace.i2hs.ApplicationThread;
import club.hsspace.i2hs.ContainerEnum;
import club.hsspace.i2hs.RunApplication;
import club.hsspace.whypps.action.Init;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.framework.manage.RunningSpace;
import club.hsspace.whypps.framework.run.WhyPPSFramework;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.handle.EquityHandle;
import club.hsspace.whypps.manage.ContainerManage;
import club.hsspace.whypps.model.senior.Code;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

/**
 * @ClassName: LoginController
 * @CreateTime: 2022/7/17
 * @Comment: 登录控制器
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private PasswordField password;

    @FXML
    private Label pswError;

    @FXML
    private TextField userName;

    @FXML
    private VBox root;

    @Injection(name = ContainerEnum.GlobalDataStream)
    private DataStream dataStream;

    @Injection
    private ContainerManage containerManage;

    @Injection
    private WhyPPSFramework whyPPSFramework;

    @Injection
    private ApplicationThread applicationThread;

    @Init
    public void initField(RunningSpace runningSpace) throws IOException {
        Properties prop = new Properties();
        prop.load(runningSpace.getInputStream("/user.properties"));

        userName.setText(prop.getProperty("user.userName", ""));
        password.setText(prop.getProperty("user.password", ""));
    }

    @FXML
    void login(ActionEvent event) {
        EquityHandle eh = dataStream.getEquityHandle();
        JSONObject jo = new JSONObject();
        jo.put("userName", userName.getText());
        jo.put("password", password.getText());
        eh.sendData(dataR -> {
            if (dataR.code == Code.OK) {
                logger.info("登陆成功");

                applicationThread.run(() -> {
                    FXMLLoader fxmlLoader = new FXMLLoader(RunApplication.class.getResource("mainMenu.fxml"));
                    Scene scene = null;
                    try {
                        scene = new Scene(fxmlLoader.load());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    MainMenuController controller = fxmlLoader.getController();

                    Stage initStage = (Stage) root.getScene().getWindow();
                    Scene finalScene = scene;
                    Platform.runLater(() -> {
                        initStage.close();
                        Stage stage = new Stage();
                        stage.setTitle("系统主菜单");
                        stage.setScene(finalScene);
                        stage.show();

                        stage.setOnCloseRequest(ev -> {
                            logger.info("正在关闭程序");
                            try {
                                whyPPSFramework.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        try {
                            LoginController.this.containerManage.injection(controller);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    });
                });

            } else {
                logger.info("登陆失败");
                Platform.runLater(() -> pswError.setVisible(true));
            }
        }, "/login", jo);
    }

}
