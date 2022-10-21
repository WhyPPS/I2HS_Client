package club.hsspace.i2hs.controller;

import club.hsspace.i2hs.ContainerEnum;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.handle.EquityHandle;
import com.alibaba.fastjson.JSONObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @ClassName: uploadImgController
 * @CreateTime: 2022/8/1
 * @Comment: 上传图片控制层
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class UploadImgController {

    private static final Logger logger = LoggerFactory.getLogger(UploadImgController.class);

    @FXML
    private TextField name;

    @FXML
    private TextField path;

    @FXML
    private TextField tag;

    @FXML
    private VBox vBox;

    @Injection(name = ContainerEnum.GlobalDataStream)
    private DataStream dataStream;

    public record UploadEntity(String name, String tag, String path) {
    }

    public UploadEntity getUploadEntity() {
        return new UploadEntity(name.getText(), tag.getText(), path.getText());
    }

    private FileChooser fileChooser = new FileChooser();

    @FXML
    void fromFile(ActionEvent event) {
        File file = fileChooser.showOpenDialog(vBox.getScene().getWindow());
        if(file == null)
            return;

        path.setText(file.getPath());
        if(name.getText().trim().equals(""))
            name.setText(file.getName());
    }

}
