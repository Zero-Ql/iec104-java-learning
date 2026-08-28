package cloud.yunyat.controller;

import cloud.yunyat.controller.iec104.WindowService;
import cloud.yunyat.model.impl.iec104.enums.IEC104_TypeIdentifier;
import cloud.yunyat.model.pojo.*;
import cloud.yunyat.model.service.ClientConnectionManager;
import cloud.yunyat.model.service.ConnectionCallback;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

import static cloud.yunyat.controller.tools.tools.isExist;
import static cloud.yunyat.controller.tools.tools.showWarning;

/**
 * FXML 控制器，负责主界面的 UI 交互逻辑
 * 包括侧边栏管理、设备树管理、编辑器标签页管理
 */
@Log4j2
public class MainControl implements Initializable {

    private double xOffset = 0;
    private double yOffset = 0;

    @Setter
    private WindowService windowService;

    /**
     * 连接管理器，注入 Platform::runLater 作为 UI 线程执行器
     */
    private final ClientConnectionManager connectionManager = new ClientConnectionManager(new ConnectionCallback() {
        // ========== ConnectionCallback 接口实现 ==========
        @Override
        public void onStatusUpdate(String message) {
            if (statusLabel != null) statusLabel.setText(message);
        }

        @Override
        public void onDeviceStateChanged(DeviceWrapper wrapper) {
            updateButtonStatus(wrapper);
            deviceTable.refresh();
        }

        @Override
        public void onWarning(String title, String header, String content) {
            showWarning(title, header, content);
        }
    }, Platform::runLater);

    @FXML
    HBox customTitleBar;

    @FXML
    ToggleButton projectBtn;
    @FXML
    ToggleButton settingsBtn;
    @FXML
    ToggleButton messageDisplay;

    @FXML
    Button startBtn;
    @FXML
    Button stopBtn;

    @FXML
    HBox mainContainer;

    @FXML
    Label leftPanelTitle;
    @FXML
    Label rightPanelTitle;

    @FXML
    VBox leftProjectPanel;
    @FXML
    VBox rightProjectPanel;
    // @FXML
    //   VBox
    @FXML
    VBox leftPanel;
    @FXML
    VBox rightPanel;

    @FXML
    TreeView<Object> leftProjectTree;

    @FXML
    ContextMenu masterContextMenu;
    @FXML
    ContextMenu rtuContextMenu;
    @FXML
    ContextMenu ycContextMenu;

    @FXML
    TableView<Device> deviceTable;
    @FXML
    TableColumn<Device, String> nameColumn;
    @FXML
    TableColumn<Device, String> ipColumn;
    @FXML
    TableColumn<Device, Integer> portColumn;
    @FXML
    TableColumn<Device, Integer> deviceStatusColumn;

    @FXML
    TableView<Rtu> RTUTable;
    @FXML
    TableColumn<Device, String> rtuNameColumn;
    @FXML
    TableColumn<Device, String> coaColumn;
    @FXML
    TableColumn<Device, Boolean> isActiveColumn;

    // 遥测Tab定义
    @FXML
    TableView<AnalogInput> YcTable;
    @FXML
    TableColumn<AnalogInput, String> YcNameColumn;
    @FXML
    TableColumn<AnalogInput, IEC104_TypeIdentifier> YcTypeColumn;
    @FXML
    TableColumn<AnalogInput, Number> YcPointColumn;
    @FXML
    TableColumn<AnalogInput, Number> YcValueColumn;
    @FXML
    TableColumn<AnalogInput, Number> YcQualityColumn;
    @FXML
    TableColumn<AnalogInput, LocalDateTime> YcTimeColumn;
    @FXML
    TableColumn<AnalogInput, Number> YcMaxColumn;
    @FXML
    TableColumn<AnalogInput, Number> YcMinColumn;
    @FXML
    TableColumn<AnalogInput, Number> YcCoefficientColumn;

    // 遥信Tab定义
    @FXML
    TableView<StatusInput> YxTable;
    @FXML
    TableColumn<StatusInput, String> YxNameColumn;
    @FXML
    TableColumn<StatusInput, IEC104_TypeIdentifier> YxTypeColumn;
    @FXML
    TableColumn<StatusInput, Number> YxPointColumn;
    @FXML
    TableColumn<StatusInput, Boolean> YxValueColumn;
    @FXML
    TableColumn<StatusInput, Number> YxQualityColumn;
    @FXML
    TableColumn<StatusInput, LocalDateTime> YxTimeColumn;
    @FXML
    TableColumn<StatusInput, Boolean> YxInvertColumn;

    // TODO 缺少Yk和Yt的表格和列

    @FXML
    TabPane editorTabPane;
    @FXML
    Tab deviceListTab;
    @FXML
    Tab RTUListTab;
    @FXML
    Tab YcListTab;
    @FXML
    Tab YxListTab;
    @FXML
    Tab YkListTab;
    @FXML
    Tab YtListTab;

    List<Tab> editorTabs;

    @FXML
    Label statusLabel;
    @FXML
    Label lineColLabel;
    @FXML
    Label encodingLabel;
    @FXML
    Label fileTypeLabel;
    @FXML
    Label editStatusLabel;

    // ========== 初始化 ==========

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeUI();
        setupEventHandlers();
        setupDraggableTitleBar();
    }

    private void initializeUI() {
        editorTabs = List.of(YcListTab, YxListTab, YkListTab, YtListTab);
        editorTabPane.getTabs().clear();

        leftPanel.prefWidthProperty().bind(mainContainer.widthProperty().multiply(0.25));
        rightPanel.prefWidthProperty().bind(mainContainer.widthProperty().multiply(0.25));

        if (leftProjectTree.getRoot() != null) {
            DeviceWrapper rootWrapper = new DeviceWrapper("master", null);
            leftProjectTree.getRoot().setValue(rootWrapper);
        }

        // 初始化设备表格
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));
        portColumn.setCellValueFactory(new PropertyValueFactory<>("port"));
        deviceStatusColumn.setCellValueFactory(new PropertyValueFactory<>("deviceStatusColumn"));

        // 初始化RTU表格
        rtuNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        coaColumn.setCellValueFactory(new PropertyValueFactory<>("COA"));
        isActiveColumn.setCellValueFactory(new PropertyValueFactory<>("enable"));

        // 初始化Yc表格
        YcNameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        YcTypeColumn.setCellValueFactory(cell -> cell.getValue().typeIdentifierProperty());
        YcPointColumn.setCellValueFactory(cell -> cell.getValue().pointProperty());
        YcValueColumn.setCellValueFactory(cell -> cell.getValue().valueProperty());
        YcQualityColumn.setCellValueFactory(cell -> cell.getValue().qualityProperty());
        YcTimeColumn.setCellValueFactory(cell -> cell.getValue().timeProperty());
        YcMaxColumn.setCellValueFactory(cell -> cell.getValue().maxProperty());
        YcMinColumn.setCellValueFactory(cell -> cell.getValue().minProperty());
        YcCoefficientColumn.setCellValueFactory(cell -> cell.getValue().coefficientProperty());

        // 初始化Yx表格
        YxNameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        YxTypeColumn.setCellValueFactory(cell -> cell.getValue().typeIdentifierProperty());
        YxPointColumn.setCellValueFactory(cell -> cell.getValue().pointProperty());
        YxValueColumn.setCellValueFactory(cell -> cell.getValue().valueProperty());
        YxQualityColumn.setCellValueFactory(cell -> cell.getValue().qualityProperty());
        YxTimeColumn.setCellValueFactory(cell -> cell.getValue().timeProperty());
        YxInvertColumn.setCellValueFactory(cell -> cell.getValue().invertProperty());
    }

    // ========== 事件处理 ==========

    private void setupEventHandlers() {
        leftProjectTree.setCellFactory(e -> new TreeCell<>() {
            private final Tooltip masterTooltip = new Tooltip("右键添加主站");
            private final Tooltip rtuTooltip = new Tooltip("右键添加RTU");
            private final Tooltip ycTooltip = new Tooltip("右键添加遥测记录");

            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setTooltip(null);
                    setContextMenu(null);
                } else if (item instanceof DeviceWrapper deviceWrapper
                        && "master".equals(deviceWrapper.getDisplayName())) {
                    setText(deviceWrapper.getDisplayName());
                    setTooltip(masterTooltip);
                    setContextMenu(masterContextMenu);
                } else if (item instanceof DeviceWrapper deviceWrapper) {
                    setText(deviceWrapper.getDisplayName());
                    setTooltip(rtuTooltip);
                    setContextMenu(rtuContextMenu);
                } else if (item instanceof RtuWrapper rtuWrapper) {
                    setText(rtuWrapper.getDisplayName());
                    setTooltip(ycTooltip);
                    setContextMenu(ycContextMenu);
                }
            }
        });

        leftProjectTree.setContextMenu(null);

        leftProjectTree.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null && newV.getValue() != null) {
                refreshTablesData(newV);
                openDeviceInEditor(newV.getValue());
                updateButtonStatus(newV.getValue());
            }
        });
    }

    private void setupDraggableTitleBar() {
        if (customTitleBar == null) return;
        customTitleBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        customTitleBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) customTitleBar.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    // ========== 窗口控制 ==========

    @FXML
    private void minimizeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void maximizeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.hide();
        new Thread(() -> {
            Platform.exit();
            System.exit(0);
        }).start();
    }

    // ========== 侧边栏控制 ==========

    @FXML
    private void handleSidebarToggle(ActionEvent e) {
        if (!(e.getSource() instanceof ToggleButton btn)) return;
        String side = (String) btn.getUserData();
        boolean isSelected = btn.isSelected();
        switch (side) {
            case "站端" -> {
                setPanelState(leftPanel, leftProjectPanel, projectBtn, isSelected);
                if (isSelected) leftPanelTitle.setText(side);
            }
            case "报文显示" -> {
                setPanelState(rightPanel, rightProjectPanel, messageDisplay, isSelected);
                if (isSelected) rightPanelTitle.setText(side);
            }
            default -> log.debug("点击了设置或未知的侧边栏: {}", side);
        }
    }

    @FXML
    private void closeSidebar(ActionEvent e) {
        if (!(e.getSource() instanceof ToggleButton btn)) return;
        String side = (String) btn.getUserData();
        if ("leftBtn".equals(side)) {
            setPanelState(leftPanel, leftProjectPanel, projectBtn, false);
        } else {
            setPanelState(rightPanel, rightProjectPanel, messageDisplay, false);
        }
        btn.setSelected(false);
    }

    private void setPanelState(VBox mainPanel, VBox innerPanel, ToggleButton toggleBtn, boolean isVisible) {
        mainPanel.setVisible(isVisible);
        mainPanel.setManaged(isVisible);
        if (innerPanel != null) {
            innerPanel.setVisible(isVisible);
            innerPanel.setManaged(isVisible);
        }
        if (toggleBtn != null && toggleBtn.isSelected() != isVisible) {
            toggleBtn.setSelected(isVisible);
        }
    }

    // ========== 设备/RTU/遥测 CRUD ==========

    @FXML
    private void addDevice() {
        if (windowService == null) return;
        windowService.showAddDeviceDialog(device -> {
            String name = device.getName().trim();
            TreeItem<Object> root = leftProjectTree.getRoot();
            TreeItem<Object> parentItem = leftProjectTree.getSelectionModel().getSelectedItem();
            if (parentItem == null) parentItem = root;

            boolean isDuplicate = parentItem.getChildren().stream()
                    .anyMatch(child -> child.getValue() instanceof DeviceWrapper dw
                            && dw.getDisplayName().equalsIgnoreCase(name));
            if (isDuplicate) {
                showWarning("警告", "添加设备失败", "设备名 '" + name + "' 已存在");
                return;
            }

            TreeItem<Object> deviceItem = new TreeItem<>(new DeviceWrapper(name, device));
            parentItem.getChildren().add(deviceItem);
            if (leftProjectTree.getSelectionModel().getSelectedItem() == parentItem) {
                deviceTable.getItems().add(device);
            }
            parentItem.setExpanded(true);
        });
    }

    @FXML
    private void deleteDevice() {
        TreeItem<Object> selectedItem = leftProjectTree.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.getValue() instanceof DeviceWrapper wrapper) {
            if (connectionManager.isRunning(wrapper.getDisplayName())) {
                showWarning("警告", "删除设备失败", "该设备目前在运行状态，无法删除");
                return;
            }
            ObservableList<TreeItem<Object>> children = leftProjectTree.getRoot().getChildren();
            children.remove(selectedItem);
            deviceTable.getItems().remove(wrapper.getDevice());
            if (children.isEmpty()) {
                leftProjectTree.getSelectionModel().select(leftProjectTree.getRoot());
            } else {
                editorTabPane.getTabs().clear();
            }
        }
    }

    @FXML
    private void addRTU() {
        if (windowService == null) return;
        windowService.showAddRtuDialog(rtu -> {
            String name = rtu.getName().trim();
            TreeItem<Object> root = leftProjectTree.getRoot();
            TreeItem<Object> parentItem = leftProjectTree.getSelectionModel().getSelectedItem();
            if (parentItem == null) parentItem = root;

            boolean isDuplicate = parentItem.getChildren().stream()
                    .anyMatch(child -> child.getValue() instanceof RtuWrapper r
                            && r.getDisplayName().equalsIgnoreCase(name));
            if (isDuplicate) {
                showWarning("警告", "添加RTU失败", "RTU名 '" + name + "' 已存在");
                return;
            }

            TreeItem<Object> rtuItem = new TreeItem<>(new RtuWrapper(name, rtu));
            parentItem.getChildren().add(rtuItem);
            if (leftProjectTree.getSelectionModel().getSelectedItem() == parentItem) {
                RTUTable.getItems().add(rtu);
            }
            parentItem.setExpanded(true);
            if (parentItem.getValue() instanceof DeviceWrapper dw) {
                dw.getDevice().getRtuList().add(rtu);
            }
        });
    }

    @FXML
    private void addYcData() {
        TreeItem<Object> selectedItem = leftProjectTree.getSelectionModel().getSelectedItem();
        if (selectedItem == null || !(selectedItem.getValue() instanceof RtuWrapper)) {
            showWarning("警告", "添加遥测失败", "请选择一个RTU节点");
            return;
        }
        windowService.showAddYcDialog(ycConf -> {
            ObservableList<AnalogInput> tableItems = YcTable.getItems();
            boolean hasDuplicate = false;
            for (AnalogInput existingYc : tableItems) {
                if (isExist(existingYc, ycConf)) {
                    hasDuplicate = true;
                    break;
                }
            }
            if (hasDuplicate) {
                showWarning("警告", "添加遥测失败", "遥测点 '" + ycConf.getName() + "' 已存在");
                return;
            }
            tableItems.add(ycConf);
        });
    }

    // ========== 设备启停 ==========

    @FXML
    private void startDevice() {
        TreeItem<Object> selectedItem = leftProjectTree.getSelectionModel().getSelectedItem();
        if (selectedItem == null || !(selectedItem.getValue() instanceof DeviceWrapper wrapper)) {
            if (statusLabel != null) statusLabel.setText("当前选中节点为空或无效");
            return;
        }
        if ("master".equals(wrapper.getDisplayName())) return;
        connectionManager.startDevice(wrapper);
    }

    @FXML
    private void stopDevice() {
        TreeItem<Object> selectedItem = leftProjectTree.getSelectionModel().getSelectedItem();
        if (selectedItem == null || !(selectedItem.getValue() instanceof DeviceWrapper wrapper)) return;
        stopBtn.setDisable(true);
        connectionManager.stopDevice(wrapper);
    }

    // ========== UI 辅助方法 ==========

    private void updateButtonStatus(Object wrapper) {
        if (wrapper instanceof DeviceWrapper dw && !"master".equals(dw.getDisplayName())) {
            Device device = dw.getDevice();
            if (device != null) {
                boolean isRunning = connectionManager.isRunning(dw.getDisplayName());
                startBtn.setDisable(isRunning);
                stopBtn.setDisable(!isRunning);
            }
        } else {
            startBtn.setDisable(true);
            stopBtn.setDisable(true);
        }
    }

    private void openDeviceInEditor(Object deviceWrapper) {
        if (deviceWrapper instanceof DeviceWrapper wrapper) {
            if ("master".equals(wrapper.getDisplayName())) {
                if (!editorTabPane.getTabs().contains(deviceListTab)) {
                    deviceListTab.setClosable(true);
                    editorTabPane.getTabs().add(deviceListTab);
                }
                editorTabPane.getSelectionModel().select(deviceListTab);
            } else {
                if (!editorTabPane.getTabs().contains(RTUListTab)) {
                    RTUListTab.setClosable(true);
                    editorTabPane.getTabs().add(RTUListTab);
                }
                editorTabPane.getSelectionModel().select(RTUListTab);
            }
            editStatusLabel.setText("已保存");
        } else if (deviceWrapper instanceof RtuWrapper) {
            for (Tab e : editorTabs) {
                if (!editorTabPane.getTabs().contains(e)) {
                    e.setClosable(true);
                    editorTabPane.getTabs().add(e);
                }
            }
            if (!editorTabs.isEmpty()) {
                Tab selectedTab = editorTabPane.getSelectionModel().getSelectedItem();
                if (!editorTabs.contains(selectedTab)) {
                    editorTabPane.getSelectionModel().select(editorTabs.getFirst());
                }
            }
        }
    }

    private void refreshTablesData(TreeItem<Object> selectedItem) {
        if (selectedItem == null || selectedItem.getValue() == null) return;
        Object wrapper = selectedItem.getValue();
        if (wrapper instanceof DeviceWrapper dw) {
            if ("master".equals(dw.getDisplayName())) {
                deviceTable.getItems().clear();
                for (TreeItem<Object> child : selectedItem.getChildren()) {
                    if (child.getValue() instanceof DeviceWrapper childDw && childDw.getDevice() != null) {
                        deviceTable.getItems().add(childDw.getDevice());
                    }
                }
            } else {
                RTUTable.getItems().clear();
                for (TreeItem<Object> child : selectedItem.getChildren()) {
                    if (child.getValue() instanceof RtuWrapper rw && rw.getRtu() != null) {
                        RTUTable.getItems().add(rw.getRtu());
                    }
                }
            }
        } else if (wrapper instanceof RtuWrapper rw) {
            if (rw.getRtu() != null) {
                YcTable.setItems(rw.getRtu().getYcList());
                YxTable.setItems(rw.getRtu().getYxList());
            }
        }
    }
}