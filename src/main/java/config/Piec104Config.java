package config;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

@Log4j2
@Getter
/**
 * Piec104Config 是一个用于加载和管理 piec104 配置项的单例配置类。
 * 它从配置文件 "piec104.ini" 中读取特定配置项，并提供默认值以确保程序正常运行。
 * 支持的配置项包括：t0、t1、t2、t3、k 和 w。
 */
public final class Piec104Config {

    private static final String SECTION = "0";
    private static final String T0 = "t0";
    private static final String T1 = "t1";
    private static final String T2 = "t2";
    private static final String T3 = "t3";
    private static final String K = "k";
    private static final String W = "w";

    private static final String DEFAULT_T0 = "30";
    private static final String DEFAULT_T1 = "15";
    private static final String DEFAULT_T2 = "10";
    private static final String DEFAULT_T3 = "20";
    private static final String DEFAULT_K = "12";
    private static final String DEFAULT_W = "8";

    private final String t0;
    private final String t1;
    private final String t2;
    private final String t3;
    private final String k;
    private final String w;

    /**
     * 使用静态内部类实现线程安全的单例模式。
     */
    private static class Holder {
        private static final Piec104Config INSTANCE = new Piec104Config();
    }

    /**
     * 获取 Piec104Config 的单例实例。
     *
     * @return 返回唯一的 Piec104Config 实例。
     */
    public static Piec104Config getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 私有构造方法，负责初始化配置信息。
     * 从配置文件中加载属性并设置各个字段的值。
     * 若配置文件不存在或读取出错，则使用默认值。
     */
    private Piec104Config() {
        Properties props = loadFromIni("piec104.ini");
        this.t0 = getString(props, T0, DEFAULT_T0);
        this.t1 = getString(props, T1, DEFAULT_T1);
        this.t2 = getString(props, T2, DEFAULT_T2);
        this.t3 = getString(props, T3, DEFAULT_T3);
        this.k = getString(props, K, DEFAULT_K);
        this.w = getString(props, W, DEFAULT_W);
        log.info("Piec104Config loaded: t0={}, t1={}, t2={}, t3={}, k={}, w={}", t0, t1, t2, t3, k, w);
    }

    /**
     * 从指定资源路径加载 .ini 格式的配置文件。
     *
     * @param fileName 要加载的配置文件名（如："piec104.ini"）。
     * @return 加载后的 Properties 对象；如果文件未找到或出错则返回空的 Properties。
     */
    private static Properties loadFromIni(String fileName) {
        Properties prop = new Properties();
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            if (in != null) {
                prop.load(in);   // 简单 key=value 即可，section 用前缀区分
            }
        } catch (IOException e) {
            log.warn("配置文件 {} 不存在或使用默认，异常：{}", fileName, e.getMessage());
        }
        return prop;
    }

    /**
     * 从 Properties 中获取带有 section 前缀的字符串型配置值。
     * 如果找不到对应键，则返回给定的默认值。
     *
     * @param p           包含所有配置项的 Properties 对象。
     * @param key         配置项名称（不含 section 前缀）。
     * @param defaultVal  默认值，在找不到配置时使用。
     * @return 配置值（已去除首尾空白），若解析失败则返回默认值。
     */
    private static String getString(Properties p, String key, String defaultVal) {
        String v = p.getProperty(SECTION + "." + key);
        if (v == null) return defaultVal;
        try {
            return v.trim();
        } catch (NumberFormatException e) {
            log.warn("配置 {} 不是合法整数，使用默认 {}", key, defaultVal);
            return defaultVal;
        }
    }
}
