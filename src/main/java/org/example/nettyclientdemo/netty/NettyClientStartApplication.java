package org.example.nettyclientdemo.netty;


import org.example.nettyclientdemo.netty.connect.md.MainDeviceConnect;
import java.util.HashSet;
import java.util.Set;

/**
 * netty启动程序
 *
 * @author WQY
 * @version 1.0
 * @date 2023/12/2 09:18
 */
public class NettyClientStartApplication {

    public void start() {
        //1.查询设备参数
        //2.过滤ip和端口
        Set<String> ipPorts = new HashSet<>();
        ipPorts.add("127.0.0.1:7788");
        ipPorts.add("127.0.0.1:7787");
        ipPorts.add("127.0.0.1:7786");
        //3.启动连接程序
        for (String ipPort : ipPorts) {
            String[] strings = ipPort.split(":");
            new MainDeviceConnect().connect(strings[0],Integer.parseInt(strings[1]));
        }
    }

}
