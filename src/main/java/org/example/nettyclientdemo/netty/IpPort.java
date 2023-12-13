package org.example.nettyclientdemo.netty;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author WQY
 * @version 1.0
 * @date 2023/12/4 15:25
 */
@Data
@AllArgsConstructor
public class IpPort {

    private String ip;

    private Integer port;

    /**
     * 解析netty地址
     * @param channel
     * @return
     */
    public static IpPort nettyRemoteAddress(Channel channel) {
        //获取远程地址
        String remoteAddress = channel.remoteAddress().toString();
        //解析，netty，remoteAddress格式是/ip:port
        String[] ipPort = remoteAddress.substring(1).split(":");
        //返回
        return new IpPort(ipPort[0], Integer.parseInt(ipPort[1]));
    }

    public String ipPort(){
        return this.ip+":"+this.port;
    }

}
