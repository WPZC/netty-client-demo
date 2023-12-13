package org.example.nettyclientdemo.netty.handle.main_device_status;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.example.nettyclientdemo.netty.IpPort;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 读取设备装备处理器
 *
 * @author WQY
 * @version 1.0
 * @date 2023/11/30 14:16
 */
@Slf4j
public class ReadC3Handler extends SimpleChannelInboundHandler<MainDeviceStatusC3InBo> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MainDeviceStatusC3InBo deviceStatusInBo) throws Exception {
        //解析ctx远程地址
        IpPort ipPort = IpPort.nettyRemoteAddress(ctx.channel());
        log.info("{}----{}", ctx.channel().remoteAddress().toString(), deviceStatusInBo.getMsg());
        //TODO:业务处理
    }

}
