package org.example.nettyclientdemo.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.example.nettyclientdemo.netty.handle.NettyReadBo;
import org.example.nettyclientdemo.netty.handle.main_device_status.MainDeviceStatusC3InBo;

import java.io.IOException;

/**
 * Created by Administrator on 2017/5/17.
 * 用于对网络事件进行读写操作
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    /**
     * 当客户端和服务端 TCP 链路建立成功之后，Netty 的 NIO 线程会调用 channelActive 方法
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //解析channel
        String key = IpPort.nettyRemoteAddress(ctx.channel()).ipPort();
        //加入缓存数据
        NettyGuide.variable(PtEnum.MB).mapChannelPut(key, ctx);
        //释放锁
        //NettyGuide.variable(PtEnum.MB).releaseChannelLock(key);
        log.info(key + "-客户端连接成功");
    }

    /**
     * 当服务端返回应答消息时，channelRead 方法被调用，从 Netty 的 ByteBuf 中读取并打印应答消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ByteBuf buf = (ByteBuf) msg;
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req, 0, buf.readableBytes());  // 使用重载的 readBytes 方法
            String body = HexByte.byte2HexStr(req);
            log.info(ctx.channel().remoteAddress() + ",Server return Message：" + body);
            //指令分发
            instructionDistribution(ctx, body);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * 指令分发
     *
     * @param ctx  信道信息
     * @param body 消息体
     */
    private void instructionDistribution(ChannelHandlerContext ctx, String body) throws Exception {
        try {
            //0.指令校验
            if (body.length() < 14) {
                return;
            }
            //1.拆分标志位
            String tag = body.substring(10, 12);
            //2.指令分发
            NettyReadBo readBo = null;
            switch (tag) {
                case "C3":
                    //主控箱状态采集
                    readBo = new MainDeviceStatusC3InBo(body);
                    break;
                default:
                    log.warn("未找到匹配指令:{}", body);
            }
            if (null != readBo) {
                //3.调用链路处理
                super.channelRead(ctx, readBo);
            } else {
                log.warn("{}-未找到readBo:{}", ctx.channel().remoteAddress(), body);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 当发生异常时，打印异常 日志，释放客户端资源
     * 断开连接
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        /**释放资源*/
        for (String key : NettyGuide.variable(PtEnum.MB).MAP_CHANNEL.keySet()) {
            if (NettyGuide.variable(PtEnum.MB).MAP_CHANNEL.get(key) == ctx) {
                log.warn("连接断开({}) : {}", key, cause.getMessage());
                //移除缓存
                NettyGuide.variable(PtEnum.MB).mapChannelRemove(key);
                //关闭通道
                ctx.close();
            }
        }
    }

    /**
     * 客户端与服务端 断连时 执行
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception, IOException {
        /**释放资源*/
        for (String key : NettyGuide.variable(PtEnum.MB).MAP_CHANNEL.keySet()) {
            if (NettyGuide.variable(PtEnum.MB).MAP_CHANNEL.get(key) == ctx) {
                log.info("客户端与服务端断开连接:" + key);
                NettyGuide.variable(PtEnum.MB).mapChannelRemove(key);
                ctx.close();
            }
        }
    }


}