package cloud.yunyat.controller.tools;

import javafx.scene.control.TreeItem;

import java.util.stream.Stream;

public class tools {
    public Stream<TreeItem<String>> flatten(TreeItem<String> item) {
        return Stream.concat(
                Stream.of(item),
                item.getChildren().stream().flatMap(this::flatten)
        );
    }
}
