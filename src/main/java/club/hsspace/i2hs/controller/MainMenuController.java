package club.hsspace.i2hs.controller;

import club.hsspace.i2hs.ContainerEnum;
import club.hsspace.i2hs.RunApplication;
import club.hsspace.i2hs.entity.ImageEntity;
import club.hsspace.whypps.action.Init;
import club.hsspace.whypps.action.Injection;
import club.hsspace.whypps.handle.DataStream;
import club.hsspace.whypps.handle.EquityHandle;
import club.hsspace.whypps.model.senior.Code;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @ClassName: MainMenuController
 * @CreateTime: 2022/7/17
 * @Comment: 主菜单控制器
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class MainMenuController {

    private static final Logger logger = LoggerFactory.getLogger(MainMenuController.class);

    @FXML
    private TableView<ImageEntity> imageTable;

    @FXML
    private ImageView imageView;

    @FXML
    private Label picSize;

    @FXML
    private Label picSpace;

    @FXML
    private Label thumSize;

    @FXML
    private Label thumSpace;

    @FXML
    private Label uploadTime;

    @FXML
    private TextField pageNow;

    @FXML
    private TextField pageRowCount;

    @FXML
    private Label pageTotal;

    public void showPicture(byte[] imageByte, String thumSpace, String thumSize, String picSpace, String picSize, String uploadTime) {
        Platform.runLater(() -> {
            Image image = new Image(new ByteArrayInputStream(imageByte));
            imageView.setImage(image);

            this.thumSpace.setText(thumSpace);
            this.thumSize.setText(thumSize);
            this.picSpace.setText(picSpace);
            this.picSize.setText(picSize);
            this.uploadTime.setText(uploadTime);
        });
    }

    private void refreshPageNum() {
        EquityHandle eh = dataStream.getEquityHandle();
        eh.sendData(dataR -> Platform.runLater(() -> {
            Integer count = dataR.data.getInteger("value");
            Integer rowCount = Integer.parseInt(pageRowCount.getText());
            pageTotal.setText("/" + (count + rowCount - 1) / rowCount);
        }), "/getImageCount", null);
    }

    @Init
    public void test() {
        refreshPageNum();
        reloadTableData();
    }

    @Injection(name = ContainerEnum.GlobalDataStream)
    private DataStream dataStream;

    private final ObservableList<ImageEntity> data = FXCollections.observableArrayList();

    public void reloadTableData() {
        EquityHandle eh = dataStream.getEquityHandle();
        JSONObject jo = new JSONObject();
        int page = Integer.parseInt(pageNow.getText());
        jo.put("page", page);
        int rowCount = Integer.parseInt(pageRowCount.getText());
        jo.put("rowCount", rowCount);
        eh.sendData(dataR -> {
            if (dataR.code.code() == 10000) {
                JSONArray value = dataR.data.getJSONArray("value");
                AtomicInteger id = new AtomicInteger((page + rowCount - 1) / rowCount);
                List<ImageEntity> collect = value.stream().map(n -> {
                    JSONObject object = (JSONObject) n;
                    ImageEntity imageEntity = new ImageEntity(object.getString("id"), "" + id.getAndIncrement(), object.getString("name"), object.getString("tag"), object.getString("enable"), object.getString("uploadTime"));
                    return imageEntity;
                }).collect(Collectors.toList());
                Platform.runLater(() -> {
                    data.clear();
                    data.addAll(collect);
                });
                logger.info("获取图像列表成功");
            } else {
                logger.info("获取图像列表失败");
            }
        }, "/getImage", jo);
    }

    private String imageId = "";

    @FXML
    void initialize() {
        ObservableList<TableColumn<ImageEntity, ?>> columns = imageTable.getColumns();
        String[] names = {"num", "name", "tag", "enable", "uploadTime"};
        for (int i = 0; i < columns.size(); i++) {
            TableColumn column = columns.get(i);
            column.setCellValueFactory(new PropertyValueFactory<ImageEntity, String>(names[i]));
        }
        imageTable.setItems(data);

        imageTable.setRowFactory(tv -> {
            TableRow<ImageEntity> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    ImageEntity item = row.getItem();
                    imageId = item.getId();
                    EquityHandle eh = dataStream.getEquityHandle();
                    eh.sendBin(binRDataLink -> {
                        JSONObject jo = binRDataLink.getData().data;
                        showPicture(binRDataLink.getExtraData(),
                                jo.getString("thumSpace"),
                                jo.getString("thumSize"),
                                jo.getString("picSpace"),
                                jo.getString("picSize"),
                                jo.getString("uploadTime"));
                    }, "/lookupImage", new JSONObject(Map.of("imageId", item.getId())), null);
                }
            });
            return row;
        });
    }

    @FXML
    void imageCreateChain(ActionEvent event) {
        if (imageId.equals(""))
            return;

        EquityHandle eh = dataStream.getEquityHandle();
        eh.sendData(dataR -> {
            JSONObject url = dataR.data;

            Platform.runLater(() -> {
                Dialog dialog = new Dialog<>();
                dialog.setTitle("复制外链地址");

                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

                FXMLLoader fxmlLoader = new FXMLLoader(RunApplication.class.getResource("outerChain.fxml"));
                Parent load = null;
                try {
                    load = fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                OuterChainController controller = fxmlLoader.getController();

                dialog.getDialogPane().setContent(load);

                controller.setLink(url.getString("imageUrl"));
                controller.setLinkThum(url.getString("thumUrl"));
                dialog.showAndWait();
            });
        }, "/createChain", new JSONObject(Map.of("imageId", imageId)));
    }

    @FXML
    void imageDelete(ActionEvent event) {
        if (imageId.equals(""))
            return;

        EquityHandle eh = dataStream.getEquityHandle();
        eh.sendData(dataR -> {
            if (dataR.code == Code.OK) {
                logger.info("删除图片{}", imageId);

                Platform.runLater(() -> {
                    refreshPageNum();
                    reloadTableData();
                });
            }
        }, "/deleteImage", new JSONObject(Map.of("imageId", imageId)));

    }

    @FXML
    void uploadImg(ActionEvent actionEvent) {

        Dialog<UploadImgController.UploadEntity> dialog = new Dialog<>();
        dialog.setTitle("上传图片");

        ButtonType loginButtonType = new ButtonType("录入", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        FXMLLoader fxmlLoader = new FXMLLoader(RunApplication.class.getResource("uploadImg.fxml"));
        Parent load = null;
        try {
            load = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        UploadImgController controller = fxmlLoader.getController();

        dialog.getDialogPane().setContent(load);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return controller.getUploadEntity();
            }
            return null;
        });

        Optional<UploadImgController.UploadEntity> result = dialog.showAndWait();

        result.ifPresent(uploadEntity -> {
            byte[] image = new byte[0];
            File file = new File(uploadEntity.path());
            try (InputStream is = new FileInputStream(file)) {
                image = is.readAllBytes();
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject jo = new JSONObject();
            jo.put("name", uploadEntity.name());
            jo.put("tag", uploadEntity.tag());

            String filename = file.getName();
            if (filename.lastIndexOf(".") == -1) {
                jo.put("type", "jpg");
            }
            jo.put("type", filename.substring(filename.lastIndexOf(".")+1));

            EquityHandle eh = dataStream.getEquityHandle();
            eh.sendBin(binRDataLink -> {
                Code code = binRDataLink.getData().code;
                if (code == Code.OK) {
                    logger.info("上传成功");
                    Platform.runLater(() -> {
                        refreshPageNum();
                        reloadTableData();
                    });
                } else {
                    logger.info("上传失败，状态码{}", code.code());
                }
            }, "/uploadImage", jo, image);
        });

    }

}
