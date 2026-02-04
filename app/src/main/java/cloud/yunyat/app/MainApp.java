package cloud.yunyat.app;


import cloud.yunyat.controller.MainController;
import cloud.yunyat.view.Iec104MasterCommunicationParameters;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.lang.reflect.InvocationTargetException;

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
        Iec104MasterCommunicationParameters iec104MasterCommunicationParameters = new Iec104MasterCommunicationParameters();
        FXMLLoader loader = new FXMLLoader(cloud.yunyat.view.ViewRes.get("/cloud/yunyat/fxml/mainView.fxml"));

        loader.setControllerFactory(type -> {
            if (type == MainController.class){
                MainController controller = new MainController();
                controller.setWindowService(iec104MasterCommunicationParameters);
                return controller;
            }
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Main");
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
