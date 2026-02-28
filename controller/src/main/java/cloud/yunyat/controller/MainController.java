package cloud.yunyat.controller;

import cloud.yunyat.controller.iec104.WindowService;
import cloud.yunyat.model.service.MessageService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.net.URL;
import java.util.ResourceBundle;

@Log4j2
public class MainController implements Initializable {

    @Setter
    private MessageService messageService;
    @Setter
    private WindowService windowService;

    @FXML
    private ToggleButton projectBtn;
    @FXML
    private ToggleButton settingsBtn;
    @FXML
    private ToggleButton communicationBtn;

    @FXML
    private SplitPane mainSplitPane;
    @FXML
    private VBox sidePanel;
    @FXML
    private Label panelTitle;

    @FXML
    private VBox projectPanel;
    @FXML
    private TreeView<String> projectTree;

    @FXML
    private TabPane editorTabPane;
    @FXML
    private Tab deviceListTab;

    @FXML
    private Label statusLabel;
    @FXML
    private Label lineColLabel;
    @FXML
    private Label encodingLabel;
    @FXML
    private Label fileTypeLabel;
    @FXML
    private Label editStatusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始隐藏侧边栏
        sidePanel.setVisible(false);
        sidePanel.setManaged(false);
        projectPanel.setVisible(false);
        projectPanel.setManaged(false);

        editorTabPane.getTabs().remove(deviceListTab);


        // 注册站端按钮监听器，在按钮被点击时显示面板
        projectBtn.setOnAction(e -> {
            if (projectBtn.isSelected()) {
                showSidePanel("站端", projectPanel);
                settingsBtn.setSelected(false);
            } else {
                hideSidePanel();
            }
        });

        settingsBtn.setOnAction(e -> {
            if (settingsBtn.isSelected()) {
                // 如果有设置面板可在这里显示
                showSidePanel("设置", null);
                projectBtn.setSelected(false);
            } else {
                hideSidePanel();
            }
        });

        // 创建单元格工厂
        projectTree.setCellFactory(e -> {
            TreeCell<String> cell = new TreeCell<>() {
                // 设置可复用标签
                private final Tooltip tooltip = new Tooltip("右键添加主站");

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    // 当单元格为空时清除当前的文本、图标、提示
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        setTooltip(null);
                    } else {
                        setText(item);
                        setTooltip(tooltip);

                        // 你甚至可以根据内容设置不同的提示
//                        if (item.endsWith(".java")) {
//                            tooltip.setText("这是一个 Java 源文件");
//                        }
                    }
                }
            };
            return cell;
        });

        // 树节点点击：如果是叶子节点就打开一个新的 Tab（模拟打开文件）
        projectTree.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null && !newV.isLeaf()) {
                openDeviceInEditor(newV.getValue());
            }
        });

        // 为已存在的 tab 中的 TextArea 添加监听器（示例）
//        editorTabPane.getTabs().forEach(tab -> {
//            if (tab.getContent() instanceof TextArea) {
//                attachCaretListener((TextArea) tab.getContent());
//            }
//        });
    }

    private void showSidePanel(String title, VBox content) {
        sidePanel.setVisible(true);
        sidePanel.setManaged(true);
        mainSplitPane.setDividerPositions(0.2);
        panelTitle.setText(title == null ? "" : title);

        boolean showProject = content == projectPanel;
        projectPanel.setVisible(showProject);
        projectPanel.setManaged(showProject);

    }

    @FXML
    private void addDevice() {
        if (windowService != null) {
            windowService.showAddDeviceDialog(deviceName -> {
                String name = deviceName.get("name");
                ObservableList<TreeItem<String>> children = projectTree.getRoot().getChildren();

                boolean isExist = children.stream().anyMatch(item -> item.getValue().equals(name));

                if (isExist) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("警告");
                    alert.setHeaderText("添加失败");
                    alert.setContentText("设备名 '" + name + "' 已存在");
                    alert.showAndWait();
                } else {
                    // TODO 待添加Item属性
                    children.add(new TreeItem<>(name));
                }
            });
        }
    }


    @FXML
    private void closeSidePanel() {
        hideSidePanel();
        mainSplitPane.setDividerPositions(0);
    }

    private void hideSidePanel() {
        sidePanel.setVisible(false);
        sidePanel.setManaged(false);
        projectBtn.setSelected(false);
        settingsBtn.setSelected(false);
    }

    private void openDeviceInEditor(String deviceName) {
        // 如果已经打开则直接切换
        for (Tab t : editorTabPane.getTabs()) {
            if (t.getText().equals(deviceName)) {
                editorTabPane.getSelectionModel().select(t);
                return;
            }
        }

        editorTabPane.getTabs().clear();


//        Tab tab = new Tab(deviceName);
//        TextArea ta = new TextArea("// 打开设备: " + deviceName + " ");
        // 设置内容区域自动换行
//        ta.setWrapText(true);
        // 设置不可编辑
//        ta.setEditable(false);
        // 将 TextArea 设置为 tab 页的内容区域
//        tab.setContent();
        // 设置 tab 页可关闭
//        tab.setClosable(true);
        // 获取选项卡列表并将 tab 页添加进去

        deviceListTab.setClosable(true);
        editorTabPane.getTabs().add(deviceListTab);
        // 选择并设置标签页
        editorTabPane.getSelectionModel().select(deviceListTab);

//        attachCaretListener(ta);
//        updateFileTypeAndEncoding(filename);
        editStatusLabel.setText("已保存");
    }
//
//    private void attachCaretListener(TextArea ta) {
//        ta.caretPositionProperty().addListener((obs, oldPos, newPos) -> {
//            int pos = newPos.intValue();
//            String text = ta.getText();
//            int line = 1;
//            int col = pos + 1;
//            if (pos > 0 && pos <= text.length()) {
//                int lastNewline = text.lastIndexOf(' ', Math.max(0, pos - 1));
//                line = (int) text.substring(0, pos).chars().filter(ch -> ch == ' ').count() + 1;
//                col = pos - (lastNewline == -1 ? -1 : lastNewline);
//            }
//            lineColLabel.setText("行: " + line + ", 列: " + col);
//        });
//
//        ta.textProperty().addListener((obs, oldT, newT) -> {
//            editStatusLabel.setText("未保存");
//        });
//    }
//
//    private void updateFileTypeAndEncoding(String filename) {
//        encodingLabel.setText("UTF-8");
//        String lower = filename.toLowerCase();
//        if (lower.endsWith(".java") || lower.endsWith(".class") || lower.endsWith(".jar")) {
//            fileTypeLabel.setText("Java");
//        } else if (lower.endsWith(".xml") || lower.endsWith(".fxml")) {
//            fileTypeLabel.setText("XML");
//        } else {
//            fileTypeLabel.setText("文本");
//        }
//    }
}