package cloud.yunyat.controller;

import org.junit.Test;

import static cloud.yunyat.controller.tools.HierarchyIdGenerator.generateReadableId;
import static cloud.yunyat.controller.tools.HierarchyIdGenerator.generateShortHashId;

public class controllerTest {
    @Test
    public void demo1(){
        String specificPrefix = "IOT_SYS";
        String deviceName = "SensorHub_01";
        String channelName = "TempSensor";
        String siteName = "Zone_A";

        System.out.println("明文标识: " + generateReadableId(specificPrefix, deviceName, channelName, siteName));
        System.out.println("短哈希标识: " + generateShortHashId(specificPrefix, deviceName, channelName, siteName));
    }
}
