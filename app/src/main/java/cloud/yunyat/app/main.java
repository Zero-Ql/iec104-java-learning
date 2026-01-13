package cloud.yunyat.app;/*
 * IEC 60870-5-104 Protocol Implementation
 * Copyright (C) 2025 QSky
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
import java.util.HashMap;
import java.util.Map;

import static cloud.yunyat.model.master.IEC104_Client.runMultipleClients;

public class main {
    public static void main(String[] args) throws Exception {
        // 单个客户端连接示例
        // String host = "127.0.0.1";
        // int port = 2555;
        // new IEC104_Client(host, port).run();

        // 多个客户端连接示例
        Map<String, Integer> servers = new HashMap<>();
        servers.put("127.0.0.1", 2555);
//        servers.put("127.0.0.1", 2556);
//        servers.put("127.0.0.1", 2557);

        runMultipleClients(servers);
    }
}
