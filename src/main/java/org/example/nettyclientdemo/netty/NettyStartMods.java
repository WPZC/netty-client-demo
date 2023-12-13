package org.example.nettyclientdemo.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * netty启动组
 *
 * @author WQY
 * @version 1.0
 * @date 2023/12/12 09:49
 */
@Data
@AllArgsConstructor
public class NettyStartMods {

    private Bootstrap bootstrap;

    private EventLoopGroup group;

}
