package cloud.yunyat.view;

import java.net.URL;

public class ViewRes {
    public static URL get(String path) {
        return ViewRes.class.getResource(path); // 这里的资源搜索范围限定在 view 模块内
    }
}
