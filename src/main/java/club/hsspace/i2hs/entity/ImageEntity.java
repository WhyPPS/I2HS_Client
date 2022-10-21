package club.hsspace.i2hs.entity;

import javafx.beans.property.SimpleStringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: ImageEntity
 * @CreateTime: 2022/8/2
 * @Comment: 图像实体类
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class ImageEntity {

    private static final Logger logger = LoggerFactory.getLogger(ImageEntity.class);

    private String id;

    private final SimpleStringProperty num;

    private final SimpleStringProperty name;

    private final SimpleStringProperty tag;

    private final SimpleStringProperty enable;

    private final SimpleStringProperty uploadTime;

    public ImageEntity(String id, String num, String name, String tag, String enable, String uploadTime) {
        this.id = id;
        this.num = new SimpleStringProperty(num);
        this.name = new SimpleStringProperty(name);
        this.tag = new SimpleStringProperty(tag);
        this.enable = new SimpleStringProperty(enable);
        this.uploadTime = new SimpleStringProperty(uploadTime);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNum() {
        return num.get();
    }

    public SimpleStringProperty numProperty() {
        return num;
    }

    public void setNum(String num) {
        this.num.set(num);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getTag() {
        return tag.get();
    }

    public SimpleStringProperty tagProperty() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag.set(tag);
    }

    public String getEnable() {
        return enable.get();
    }

    public SimpleStringProperty enableProperty() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable.set(enable);
    }

    public String getUploadTime() {
        return uploadTime.get();
    }

    public SimpleStringProperty uploadTimeProperty() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime.set(uploadTime);
    }
}
