package config;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

@Log4j2
@Getter
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

    private static class Holder {
        private static final Piec104Config INSTANCE = new Piec104Config();
    }

    public static Piec104Config getInstance() {
        return Holder.INSTANCE;
    }

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

    /* ================== 单元测试注入口 ================== */
    static Piec104Config forTest(Properties p) {
        return new Piec104Config(p);
    }

    private Piec104Config(Properties p) {
        this.t0 = getString(p, T0, DEFAULT_T0);
        this.t1 = getString(p, T1, DEFAULT_T1);
        this.t2 = getString(p, T2, DEFAULT_T2);
        this.t3 = getString(p, T3, DEFAULT_T3);
        this.k = getString(p, K, DEFAULT_K);
        this.w = getString(p, W, DEFAULT_W);
    }
}