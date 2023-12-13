package org.example.nettyclientdemo.netty.connect;

/**
 * 设备连接
 * @author WQY
 * @version 1.0
 * @date 2023/12/1 09:57
 */
public interface DeviceConnect {

    /**
     * 连接
     * @param ip
     * @param port
     */
    void connect(String ip, Integer port);

}
