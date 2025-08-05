package config;

import lombok.Data;
import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;

@Data
public class piec104Config {
    private String t0 = "30";
    private String t1 = "15";
    private String t2 = "10";
    private String t3 = "15";

    public piec104Config() {
        Ini ini;
        try {
            ini = new Ini(new File("src/main/resources/piec104.ini"));
            this.t0 = ini.get("0", "t0");
            this.t1 = ini.get("0", "t1");
            this.t2 = ini.get("0", "t2");
            this.t3 = ini.get("0", "t3");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
