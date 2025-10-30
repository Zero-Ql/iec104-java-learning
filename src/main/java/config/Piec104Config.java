package config;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.ini4j.Ini;

import java.io.IOException;
import java.io.InputStream;
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

    private static final String DEFAULT_T0 = "1";
    private static final String DEFAULT_T1 = "2";
    private static final String DEFAULT_T2 = "3";
    private static final String DEFAULT_T3 = "4";
    private static final String DEFAULT_K = "5";
    private static final String DEFAULT_W = "6";

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

    private Properties loadFromIni(String fileName) {
        Properties prop = new Properties();
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            if (in == null) {
                log.warn("配置文件 {} 未找到，全部使用默认值", fileName);
                return prop;
            }
            Ini ini = new Ini(in);
            Ini.Section sec = ini.get("0");   // [0]
            if (sec == null) {
                log.warn("ini 中未找到 section [{}]，全部使用默认值", SECTION);
                return prop;
            }
            // 把 section 下的 key=value 拷进 Properties，并带上前缀 "0."
            sec.forEach((k, v) -> {
                log.debug("{} = {}", k, v);
                prop.setProperty(k, v);
            });
        } catch (IOException e) {
            log.warn("读取 ini 失败，使用默认值。异常：{}", e.getMessage());
        }
        return prop;
    }

    public static String getString(Properties p, String key, String defaultVal) {
        String v = p.getProperty(key);
        return v == null ? defaultVal : v.trim();
    }
}