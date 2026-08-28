package cloud.yunyat.view;

import cloud.yunyat.controller.MainControl;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * 主场景工厂类，封装 FXML 加载和 Controller 工厂逻辑
 * 让 app 模块无需直接依赖 controller 模块
 */
public class MainSceneFactory {

    /**
     * 创建主场景的根节点
     * @return 加载完成的 Parent 节点
     * @throws IOException FXML 加载失败时抛出
     */
    public static Parent createMainScene() throws IOException {
        // 创建统一窗口服务
        UnifiedWindowService unifiedWindowService = UnifiedWindowService.create();

        // 加载主界面 FXML
        FXMLLoader loader = new FXMLLoader(ViewRes.get("/cloud/yunyat/fxml/mainView.fxml"));

        // 设置控制器工厂，注入 WindowService
        loader.setControllerFactory(type -> {
            if (type == MainControl.class) {
                MainControl control = new MainControl();
                control.setWindowService(unifiedWindowService);
                return control;
            }
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | NoSuchMethodException
                     | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException("创建控制器实例失败: " + type.getName(), e);
            }
        });

        return loader.load();
    }
}