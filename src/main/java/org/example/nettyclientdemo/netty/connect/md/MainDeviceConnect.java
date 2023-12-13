package org.example.nettyclientdemo.netty.connect.md;

import lombok.extern.slf4j.Slf4j;
import org.example.nettyclientdemo.netty.IpPort;
import org.example.nettyclientdemo.netty.NettyGuide;
import org.example.nettyclientdemo.netty.PtEnum;
import org.example.nettyclientdemo.netty.connect.AbstractDeviceConnect;

/**
 * 主控设备连接
 *
 * @author WQY
 * @version 1.0
 * @date 2023/12/1 09:54
 */
@Slf4j
public class MainDeviceConnect extends AbstractDeviceConnect {

    @Override
    public void connect(String ip, Integer port) {
        //0.address
        String key = ip + ":" + port;
        try {
            //1.针对ip:port加锁
            NettyGuide.variable(PtEnum.MB).acquireLock(key);
            //2.判断是否在缓存中
            if (!NettyGuide.variable(PtEnum.MB).MAP_CHANNEL.containsKey(key)) {
                //创建连接
                NettyGuide.connect(new IpPort(ip, port),PtEnum.MB);
            }else {
                log.info("通道已连接:{}",key);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            //释放锁
            NettyGuide.variable(PtEnum.MB).releaseLock(key);
        }
    }
}
