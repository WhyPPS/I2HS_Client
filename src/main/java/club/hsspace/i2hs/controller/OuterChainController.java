package club.hsspace.i2hs.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: OuterChainController
 * @CreateTime: 2022/8/9
 * @Comment: 外链生成控制器
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
public class OuterChainController {

    private static final Logger logger = LoggerFactory.getLogger(OuterChainController.class);

    @FXML
    private TextField link;

    @FXML
    private TextField linkThum;

    @FXML
    private Label succ;

    @FXML
    private Label succThum;

    public void setLink(String link) {
        this.link.setText(link);
    }

    public void setLinkThum(String linkThum){
        this.linkThum.setText(linkThum);
    }

    @FXML
    void copy(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(link.getText());
        clipboard.setContent(clipboardContent);
        succ.setVisible(true);
        succThum.setVisible(false);
    }

    @FXML
    void copyThum(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(linkThum.getText());
        clipboard.setContent(clipboardContent);
        succ.setVisible(false);
        succThum.setVisible(true);
    }

}
