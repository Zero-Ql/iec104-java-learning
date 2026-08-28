package cloud.yunyat.app;

import cloud.yunyat.view.MainSceneFactory;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage stage) throws Exception {
        // 通过 view 模块的工厂方法创建主场景，app 不再直接依赖 controller 模块
        Parent root = MainSceneFactory.createMainScene();
        Scene scene = new Scene(root);

        // 隐藏系统默认的标题栏跟边框
        stage.initStyle(StageStyle.UNDECORATED);

        stage.setScene(scene);
        stage.setTitle("Main");
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}