package cloud.yunyat.controller;

import cloud.yunyat.controller.iec104.WindowService;
import cloud.yunyat.model.impl.iec104.enums.IEC104_TypeIdentifier;
import cloud.yunyat.model.master.IEC104_Client;
import cloud.yunyat.model.pojo.*;
import cloud.yunyat.model.service.MessageService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import static cloud.yunyat.controller.tools.tools.isExist;
import static cloud.yunyat.controller.tools.tools.showWarning;

/**
 * 主控制器类，负责管理主界面的交互逻辑
 * 包括侧边栏管理、设备树管理、编辑器标签页管理
 */
@Log4j2
public class MainController implements Initializable {

    @FXML
    private HBox customTitleBar;
    private double xOffset = 0;
    private double yOffset = 0;


    @Setter
    private MessageService messageService;
    @Setter
    private WindowService windowService;

    private Thread currentClientThread;
    private final Map<String, IEC104_Client> activeClients = new ConcurrentHashMap<>();

    @FXML
    private ToggleButton projectBtn;
    @FXML
    private ToggleButton settingsBtn;
    @FXML
    private ToggleButton messageDisplay;

    @FXML
    private Button startBtn;
    @FXML
    private Button stopBtn;

    @FXML
    private HBox mainContainer;

    @FXML
    private Label leftPanelTitle;
    @FXML
    private Label rightPanelTitle;

    @FXML
    private VBox leftProjectPanel;
    @FXML
    private VBox rightProjectPanel;
    //    @FXML
//    private VBox
    @FXML
    private VBox leftPanel;
    @FXML
    private VBox rightPanel;

    @FXML
    private TreeView<Object> leftProjectTree;

    @FXML
    private ContextMenu masterContextMenu;
    @FXML
    private ContextMenu rtuContextMenu;
    @FXML
    private ContextMenu ycContextMenu;

    @FXML
    private TableView<Device> deviceTable;
    @FXML
    private TableColumn<Device, String> nameColumn;
    @FXML
    private TableColumn<Device, String> ipColumn;
    @FXML
    private TableColumn<Device, Integer> portColumn;
    @FXML
    private TableColumn<Device, Integer> deviceStatusColumn;

    @FXML
    private TableView<Rtu> RTUTable;
    @FXML
    private TableColumn<Device, String> rtuNameColumn;
    @FXML
    private TableColumn<Device, String> coaColumn;
    @FXML
    private TableColumn<Device, Boolean> isActiveColumn;

    // 遥测Tab定义
    @FXML
    private TableView<AnalogInput> YcTable;
    @FXML
    private TableColumn<AnalogInput, String> YcNameColumn;
    @FXML
    private TableColumn<AnalogInput, IEC104_TypeIdentifier> YcTypeColumn;
    @FXML
    private TableColumn<AnalogInput, Integer> YcPointColumn;
    @FXML
    private TableColumn<AnalogInput, Double> YcValueColumn;
    @FXML
    private TableColumn<AnalogInput, Integer> YcQualityColumn;
    @FXML
    private TableColumn<AnalogInput, LocalDateTime> YcTimeColumn;
    @FXML
    private TableColumn<AnalogInput, Double> YcMaxColumn;
    @FXML
    private TableColumn<AnalogInput, Double> YcMinColumn;
    @FXML
    private TableColumn<AnalogInput, Double> YcCoefficientColumn;

    // 遥信Tab定义
    @FXML
    private TableView<StatusInput> YxTable;
    @FXML
    private TableColumn<StatusInput, String> YxNameColumn;
    @FXML
    private TableColumn<StatusInput, IEC104_TypeIdentifier> YxTypeColumn;
    @FXML
    private TableColumn<StatusInput, Integer> YxPointColumn;
    @FXML
    private TableColumn<StatusInput, Boolean> YxValueColumn;
    @FXML
    private TableColumn<StatusInput, Integer> YxQualityColumn;
    @FXML
    private TableColumn<StatusInput, LocalDateTime> YxTimeColumn;
    @FXML
    private TableColumn<StatusInput, Boolean> YxInvertColumn;

    // TODO 缺少Yk和Yt的表格和列

    @FXML
    private TabPane editorTabPane;
    @FXML
    private Tab deviceListTab;
    @FXML
    private Tab RTUListTab;
    @FXML
    private Tab YcListTab;
    @FXML
    private Tab YxListTab;
    @FXML
    private Tab YkListTab;
    @FXML
    private Tab YtListTab;

    private List<Tab> editorTabs;

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
        initializeUI();

        setupEventHandlers();

        setupDraggableTitleBar();

        // 为已存在的 tab 中的 TextArea 添加监听器（示例）
//        editorTabPane.getTabs().forEach(tab -> {
//            if (tab.getContent() instanceof TextArea) {
//                attachCaretListener((TextArea) tab.getContent());
//            }
//        });
    }

    private void initializeUI() {
        editorTabs = List.of(YcListTab, YxListTab, YkListTab, YtListTab);
        editorTabPane.getTabs().clear();

        leftPanel.prefWidthProperty().bind(mainContainer.widthProperty().multiply(0.25));
        rightPanel.prefWidthProperty().bind(mainContainer.widthProperty().multiply(0.25));

        // 确保TreeView有正确的根节点
        if (leftProjectTree.getRoot() != null) {
            // 创建根节点，使用虚拟的DeviceWrapper
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
        YcPointColumn.setCellValueFactory(cell -> cell.getValue().pointProperty().asObject());
        YcValueColumn.setCellValueFactory(cell -> cell.getValue().valueProperty().asObject());
        YcQualityColumn.setCellValueFactory(cell -> cell.getValue().qualityProperty().asObject());
        YcTimeColumn.setCellValueFactory(cell -> cell.getValue().timeProperty());
        YcMaxColumn.setCellValueFactory(cell -> cell.getValue().maxProperty().asObject());
        YcMinColumn.setCellValueFactory(cell -> cell.getValue().minProperty().asObject());
        YcCoefficientColumn.setCellValueFactory(cell -> cell.getValue().coefficientProperty().asObject());

        // 初始化Yx表格
        YxNameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        YxTypeColumn.setCellValueFactory(cell -> cell.getValue().typeIdentifierProperty());
        YxPointColumn.setCellValueFactory(cell -> cell.getValue().pointProperty().asObject());
        YxValueColumn.setCellValueFactory(cell -> cell.getValue().valueProperty().asObject());
        YxQualityColumn.setCellValueFactory(cell -> cell.getValue().qualityProperty().asObject());
        YxTimeColumn.setCellValueFactory(cell -> cell.getValue().timeProperty());
        YxInvertColumn.setCellValueFactory(cell -> cell.getValue().invertProperty().asObject());
    }

    private void setupEventHandlers() {
        // 创建单元格工厂
        leftProjectTree.setCellFactory(e -> new TreeCell<>() {
            // 设置可复用标签
            private final Tooltip masterTooltip = new Tooltip("右键添加主站");
            private final Tooltip rtuTooltip = new Tooltip("右键添加RTU");
            private final Tooltip ycTooltip = new Tooltip("右键添加遥测记录");


            /**
             * 更新树形表格单元格的显示内容
             * 根据设备类型设置不同的显示文本、提示信息和右键菜单
             *
             * @param item 当前单元格对应的 DeviceWrapper 对象，代表设备包装器
             * @param empty 标识当前单元格是否为空
             */
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);

                // 当单元格为空时清除当前的文本、图标、提示和右键菜单
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setTooltip(null);
                    setContextMenu(null);
                } else if (item instanceof DeviceWrapper deviceWrapper && "master".equals(deviceWrapper.getDisplayName())) {
                    // 主站设备
                    setText(deviceWrapper.getDisplayName());
                    setTooltip(masterTooltip);
                    setContextMenu(masterContextMenu);
                } else if (item instanceof DeviceWrapper deviceWrapper) {
                    // RTU 通道
                    setText(deviceWrapper.getDisplayName());
                    setTooltip(rtuTooltip);
                    setContextMenu(rtuContextMenu);
                } else if (item instanceof RtuWrapper rtuWrapper) {
                    // 表格
                    setText(rtuWrapper.getDisplayName());
                    setTooltip(ycTooltip);
                    setContextMenu(ycContextMenu);
                }
            }
        });

        leftProjectTree.setContextMenu(null);

        // 监听项目树的选择变化事件
        leftProjectTree.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null && newV.getValue() != null) {
                refreshTablesData(newV);
                openDeviceInEditor(newV.getValue());
            }
        });
    }

    /**
     * 实现自定义标题栏的拖拽功能
     */
    private void setupDraggableTitleBar() {
        if (customTitleBar == null) return;

        // 鼠标按下时，记录相对偏移量
        customTitleBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        // 鼠标拖动时，根据偏移量更新窗口位置
        customTitleBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) customTitleBar.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    /**
     * 实现自定义最小化功能
     */
    @FXML
    private void minimizeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * 实现自定义最大化功能
     */
    @FXML
    private void maximizeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    /**
     * 实现自定义关闭窗口功能
     */
    @FXML
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.hide();

        // 新启动一个线程来执行 JVM退出和资源清理
        new Thread(() -> {
            Platform.exit();
            System.exit(0);
        }).start();
    }

    @FXML
    private void handleSidebarToggle(ActionEvent e) {
        // 提前返回 (Guard Clause)，减少 if 嵌套层次
        if (!(e.getSource() instanceof ToggleButton btn)) {
            return;
        }

        String side = (String) btn.getUserData();
        boolean isSelected = btn.isSelected();

        // 使用增强型 switch (Java 14+) 提升可读性
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
        if (!(e.getSource() instanceof ToggleButton btn)) {
            return;
        }

        String side = (String) btn.getUserData();
        // 强行设置为 false
        if ("leftBtn".equals(side)) {
            setPanelState(leftPanel, leftProjectPanel, projectBtn, false);
        } else {
            setPanelState(rightPanel, rightProjectPanel, messageDisplay, false);
        }

        btn.setSelected(false);
    }

    /**
     * 统一管理面板的 可见性、布局参与度 以及 关联按钮的状态
     *
     * @param mainPanel  外层主面板 (如 leftPanel)
     * @param innerPanel 内部项目面板 (如 leftProjectPanel)
     * @param toggleBtn  控制该面板的 ToggleButton (如 projectBtn)
     * @param isVisible  目标状态：显示(true) 还是 隐藏(false)
     */
    private void setPanelState(VBox mainPanel, VBox innerPanel, ToggleButton toggleBtn, boolean isVisible) {

        // 处理外层面板
        mainPanel.setVisible(isVisible);
        mainPanel.setManaged(isVisible);

        // 处理内部面板
        if (innerPanel != null) {
            innerPanel.setVisible(isVisible);
            innerPanel.setManaged(isVisible);
        }

        // 同步按钮状态 (避免点击右上角关闭后，ToggleButton 依然是按下状态)
        if (toggleBtn != null && toggleBtn.isSelected() != isVisible) {
            toggleBtn.setSelected(isVisible);
        }
    }

    @FXML
    private void addYcData() {

        if (messageService == null) return;

        TreeItem<Object> selectedItem = leftProjectTree.getSelectionModel().getSelectedItem();
        // 检查是否有选中节点
        if (selectedItem == null || !(selectedItem.getValue() instanceof RtuWrapper)) {
            showWarning("请选择一个RTU节点");
            return;
        }

        // 弹出遥测添加窗口
        windowService.showAddYcDialog(ycConf -> {
            // 获取Yc表所有行
            ObservableList<AnalogInput> tableItems = YcTable.getItems();
            boolean hasDuplicate = false;
            for (AnalogInput existingYc : tableItems) {
                // isExist 返回 true 代表两者发生冲突/重复
                if (isExist(existingYc, ycConf)) {
                    hasDuplicate = true;
                    break; // 找到重复项，直接退出循环
                }
            }

            // 如果存在重复，提示并中止添加
            if (hasDuplicate) {
                showWarning("遥测点 '" + ycConf.getName() + "' 已存在");
                return;
            }

            // 如果没有重复，在循环外进行添加操作
            tableItems.add(ycConf);
        });

        int stationId;

        // 获取当前选中节点实体
        Rtu rtu = ((RtuWrapper) selectedItem.getValue()).getRtu();

        stationId = rtu.getCOA();

        messageService.subscribeYcData(stationId, yc -> {
            // 使用 JavaFx 线程更新UI组件数据
            Platform.runLater(() -> {
                ObservableList<AnalogInput> tableItems = YcTable.getItems();

                boolean isExist = false;

                for (AnalogInput existingYc : tableItems) {
                    // 判断点号是否存在
                    if (existingYc.pointProperty().get() == yc.pointProperty().get()) {
                        existingYc.valueProperty().set(yc.valueProperty().get());
                        existingYc.qualityProperty().set(yc.qualityProperty().get());
                        existingYc.timeProperty().set(yc.timeProperty().get());

                        isExist = true;
                        break;
                    }
                }

                if (!isExist) {
                    tableItems.add(yc);
                }
            });
        });
    }

    @FXML
    private void addDevice() {

        if (windowService == null) return;

        windowService.showAddDeviceDialog(device -> {
            String name = device.getName().trim();
            // 获取根节点
            TreeItem<Object> root = leftProjectTree.getRoot();

            // 统一查找范围：如果没选中，则查根节点；如果选中了，则查选中节点的子集
            TreeItem<Object> parentItem = leftProjectTree.getSelectionModel().getSelectedItem();
            if (parentItem == null) parentItem = root;

            boolean isDuplicate = parentItem.getChildren().stream()
                    .anyMatch(child -> child.getValue() instanceof DeviceWrapper dw
                            && dw.getDisplayName().equalsIgnoreCase(name));

            // 名称已存在
            if (isDuplicate) {
                showWarning("设备名 '" + name + "' 已存在");
                return;
            }

            // 创建 设备 项目树节点
            TreeItem<Object> deviceItem = new TreeItem<>(new DeviceWrapper(name, device));

            parentItem.getChildren().add(deviceItem);

            // 只有当前选中项正是我们要添加设备的父节点时，才直接更新 UI 表格
            if (leftProjectTree.getSelectionModel().getSelectedItem() == parentItem) {
                deviceTable.getItems().add(device);
            }

            // 展开父节点，显示新添加的 设备
            parentItem.setExpanded(true);
        });
    }

    @FXML
    private void deleteDevice() {
        // 获取当前选中的项目树节点
        TreeItem<Object> selectedItem = leftProjectTree.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.getValue() instanceof DeviceWrapper wrapper) {
            // 获取根节点的子节点列表
            ObservableList<TreeItem<Object>> children = leftProjectTree.getRoot().getChildren();
            children.remove(selectedItem);
            // 从设备表中移除选中的设备
            deviceTable.getItems().remove(wrapper.getDevice());

            if (children.isEmpty()) {
                leftProjectTree.getSelectionModel().select(leftProjectTree.getRoot());
            } else {
                // 清空编辑器中的所有标签页
                editorTabPane.getTabs().clear();
            }

        }
    }

    @FXML
    private void addRTU() {

        if (windowService == null) return;

        windowService.showAddRtuDialog(rtu -> {
            String name = rtu.getName().trim();
            // 获取根节点
            TreeItem<Object> root = leftProjectTree.getRoot();

            // 统一查找范围：如果没选中，则查根节点；如果选中了，则查选中节点的子集
            TreeItem<Object> parentItem = leftProjectTree.getSelectionModel().getSelectedItem();
            if (parentItem == null) parentItem = root;

            boolean isDuplicate = parentItem.getChildren().stream()
                    .anyMatch(child -> child.getValue() instanceof RtuWrapper r
                            && r.getDisplayName().equalsIgnoreCase(name));

            // 名称已存在
            if (isDuplicate) {
                showWarning("RTU名 '" + name + "' 已存在");
                return;
            }

            // 创建 RTU 项目树节点
            TreeItem<Object> rtuItem = new TreeItem<>(new RtuWrapper(name, rtu));

            parentItem.getChildren().add(rtuItem);

            // 只有当前选中项正是我们要添加 RTU 的设备节点时，才直接更新 UI 表格
            if (leftProjectTree.getSelectionModel().getSelectedItem() == parentItem) {
                RTUTable.getItems().add(rtu);
            }

            // 展开父节点，显示新添加的 RTU
            parentItem.setExpanded(true);
        });
    }

    @FXML
    private void startDevice() {
        // 获取当前选中的项目树节点
        TreeItem<Object> selectedItem = leftProjectTree.getSelectionModel().getSelectedItem();
        // 如果当前选中节点为空或不是设备节点
        if (selectedItem == null || !(selectedItem.getValue() instanceof DeviceWrapper wrapper)) {
            if (statusLabel != null) statusLabel.setText("当前选中节点为空或无效");
            return;
        }

        // 如果当前选中节点是主节点，不允许启动
        if ("master".equals(wrapper.getDisplayName())) return;

        Device device = wrapper.getDevice();
        String deviceName = wrapper.getDisplayName();

        if (statusLabel != null) statusLabel.setText("设备 " + deviceName + " 连接中...");

        // 定义设备连接线程
        currentClientThread = new Thread(() -> {

            IEC104_Client client = new IEC104_Client(device.getIp(), device.getPort());
            activeClients.put(deviceName, client);

            // 设备状态设置为运行中
            device.setDeviceStatusColumn(true);
            // 提交一个任务
            Platform.runLater(() -> {
                updateButtonStatus(wrapper);
                // 刷新设备表格
                deviceTable.refresh();
            });

            try {
                Platform.runLater(() -> {
                    statusLabel.setText("设备 " + deviceName + " 运行中");
                });

                client.run();

                Platform.runLater(() -> {
                    log.info("设备 {} 已断开连接", deviceName);
                    statusLabel.setText("设备 " + deviceName + " 已断开");
                });

            } catch (InterruptedException e) {
                // 捕获中断异常，这是正常的停止操作，不需要报错
                log.info("设备 {} 已被用户手动停止", deviceName);
                Platform.runLater(() -> {
                    statusLabel.setText("设备 " + deviceName + " 已停止");
                });
            } catch (Exception e) {
                log.error("设备 {} 启动或运行异常: ", deviceName, e);
                Platform.runLater(() -> {
                    showWarning("设备 '" + deviceName + "' 启动失败：\n" + e.getMessage());
                    statusLabel.setText("设备 " + deviceName + " 启动失败");
                });
            } finally {
                // 从 Map 中移除设备的客户端实例
                activeClients.remove(deviceName);
                // 把 UI 状态恢复为初始状态
                device.setDeviceStatusColumn(false);
                Platform.runLater(() -> {
                    log.info("设备 {} 线程已完全退出", deviceName);
                    deviceTable.refresh();
                });
            }
        });

        // 设置为守护线程，确保在 JVM 退出时自动关闭
        currentClientThread.setDaemon(true);
        // 设置线程名称，方便调试
        currentClientThread.setName("IEC104-ClientThread-" + wrapper.getDisplayName());
        // 启动线程
        currentClientThread.start();

    }

    @FXML
    private void stopDevice() {
        TreeItem<Object> selectedItem = leftProjectTree.getSelectionModel().getSelectedItem();

        if (selectedItem != null && selectedItem.getValue() instanceof DeviceWrapper wrapper) {
            String deviceName = wrapper.getDisplayName();

            // 从 Map 中获取对应设备的客户端实例
            IEC104_Client client = activeClients.get(deviceName);

            if (client != null) {
                log.info("正在手动停止设备: {}", deviceName);
                stopBtn.setDisable(true);
                // 调用 Netty 底层关闭方法
                client.stop();

                // 此时 startDevice 里的 client.run() 会解除阻塞并走到 finally 块更新 UI
                log.info("设备 {} 已发出停止指令", deviceName);
            } else {
                wrapper.getDevice().setDeviceStatusColumn(false);
                deviceTable.refresh();
                updateButtonStatus(wrapper);
                showWarning("该设备目前未在运行状态");
            }
        }
    }

    /**
     * @param wrapper 选中的节点
     */
    private void updateButtonStatus(Object wrapper) {
        // 选中的节点类型为设备Wrapper 且不是 master 节点
        if (wrapper instanceof DeviceWrapper dw && !"master".equals(dw.getDisplayName())) {
            Device device = dw.getDevice();
            if (device != null) {
                boolean isRunning = device.isDeviceStatusColumn();// 获取运行状态
                // 如果设备运行中，禁用启动按钮，启用终止按钮
                startBtn.setDisable(isRunning);//
                stopBtn.setDisable(!isRunning);
            }
        } else {
            // 如果不是设备Wrapper 类型或为 master 节点，禁用所有按钮
            startBtn.setDisable(true);
            stopBtn.setDisable(true);
        }
    }

    private void openDeviceInEditor(Object deviceWrapper) {
        if (deviceWrapper instanceof DeviceWrapper wrapper) {

            // 如果已经打开则直接切换
            for (Tab t : editorTabPane.getTabs()) {
                if (t.getText().equals(wrapper.getDisplayName())) {
                    editorTabPane.getSelectionModel().select(t);
                    return;
                }
            }

//            editorTabPane.getTabs().clear();

            if ("master".equals(wrapper.getDisplayName())) {
                // 设置 tab 页可关闭
                deviceListTab.setClosable(true);
                // 获取选项卡列表并将 tab 页添加进去
                editorTabPane.getTabs().add(deviceListTab);
                // 选择并设置标签页
                editorTabPane.getSelectionModel().select(deviceListTab);
            } else {
                RTUListTab.setClosable(true);
                editorTabPane.getTabs().add(RTUListTab);
                editorTabPane.getSelectionModel().select(RTUListTab);
            }
            editStatusLabel.setText("已保存");
        } else if (deviceWrapper instanceof RtuWrapper wrapper) {
            // 如果已经打开则直接切换
            for (Tab t : editorTabPane.getTabs()) {
                if (t.getText().equals(wrapper.getDisplayName())) {
                    editorTabPane.getSelectionModel().select(t);
                    return;
                }
            }

            boolean found = false;
            for (Tab e : editorTabs) {
                if (!editorTabPane.getTabs().contains(e)) {
                    e.setClosable(true);
                    editorTabPane.getTabs().add(e);
                    found = true;
                }
            }

            // 只有在添加了新标签页后才进行选择操作
            if (found && !editorTabPane.getTabs().isEmpty()) {
                // 选择最后一个标签页
                int lastIndex = editorTabPane.getTabs().size() - 1;
                if (lastIndex >= 0) {  // 再次确认索引有效
                    editorTabPane.getSelectionModel().select(editorTabPane.getTabs().get(lastIndex));
                }
            } else if (!editorTabPane.getTabs().isEmpty()) {
                // 如果没有添加新标签页但存在标签页，则选择当前的最后一个
                int lastIndex = editorTabPane.getTabs().size() - 1;
                if (lastIndex >= 0) {
                    editorTabPane.getSelectionModel().select(editorTabPane.getTabs().get(lastIndex));
                }
            }
        }
    }

    /**
     * 根据当前选中的树节点，动态刷新右侧表格的数据
     */
    private void refreshTablesData(TreeItem<Object> selectedItem) {
        if (selectedItem == null || selectedItem.getValue() == null) return;

        Object wrapper = selectedItem.getValue();

        if (wrapper instanceof DeviceWrapper dw) {
            if ("master".equals(dw.getDisplayName())) {
                // 如果选中了主站根节点，刷新设备列表 (deviceTable)
                deviceTable.getItems().clear(); // 先清空旧数据
                // 遍历子节点，把具体的 Device 加回来
                for (TreeItem<Object> child : selectedItem.getChildren()) {
                    if (child.getValue() instanceof DeviceWrapper childDw && childDw.getDevice() != null) {
                        deviceTable.getItems().add(childDw.getDevice());
                    }
                }
            } else {
                // 如果选中了具体的设备节点，刷新它的 RTU 列表 (RTUTable)
                RTUTable.getItems().clear(); // 先清空旧数据
                // 遍历该设备下的子节点，把对应的 RTU 加回来
                for (TreeItem<Object> child : selectedItem.getChildren()) {
                    if (child.getValue() instanceof RtuWrapper rw && rw.getRtu() != null) {
                        RTUTable.getItems().add(rw.getRtu());
                    }
                }
            }
        } else if (wrapper instanceof RtuWrapper rw) {
            // 如果选中了某个具体的 RTU，这里未来需要刷新遥测(Yc)、遥信(Yx)等表格
            if (rw.getRtu() != null) {
                YcTable.setItems(rw.getRtu().getYcList());
            }
        }
    }
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