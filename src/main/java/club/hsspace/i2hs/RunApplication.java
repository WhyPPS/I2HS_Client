package club.hsspace.i2hs;

import club.hsspace.i2hs.controller.InitController;
import club.hsspace.whypps.framework.run.WhyPPSFramework;
import club.hsspace.whypps.manage.ContainerManage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

public class RunApplication extends Application {

    private static final Logger logger = LoggerFactory.getLogger(InitController.class);

    private static ContainerManage containerManage;

    private static WhyPPSFramework whyPPSFramework;

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(RunApplication.class.getResource("init.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        InitController controller = fxmlLoader.getController();

        stage.setTitle("正在准备客户端...");
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

        containerManage.getFromClass(ApplicationThread.class).run(() -> {
            try {
                containerManage.injection(controller);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public static void main(WhyPPSFramework whyPPSFramework, ContainerManage containerManage) {
        RunApplication.whyPPSFramework = whyPPSFramework;
        RunApplication.containerManage = containerManage;
        launch();
    }
}