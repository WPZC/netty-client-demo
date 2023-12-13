package org.example.nettyclientdemo.netty;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.example.nettyclientdemo.netty.cache.Variable;
import org.example.nettyclientdemo.netty.unpack.Unpacking;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 构建netty-client环境
 *
 * @author WQY
 * @version 1.0
 * @date 2023/12/3 09:41
 */
@Slf4j
public class NettySupport {

    /**
     * 缓存信息值
     */
    private Variable variable;

    private NettyStartMods startMods;

    public NettySupport() {
        //初始化缓存信息
        this.variable = new Variable();
    }

    /**
     * 初始化NettyStartMods
     *
     * @param mods
     */
    public NettySupport initStartMods(NettyStartMods mods) {
        this.startMods = mods;
        return this;
    }

    /**
     * 连接
     *
     * @param ipPort
     */
    public void connect(IpPort ipPort) {
        ChannelFuture future = this.startMods.getBootstrap().connect(ipPort.getIp(), ipPort.getPort());
        future.addListener(f -> {
            if (f.isSuccess()) {
                log.info("{}-连接成功", ipPort.ipPort());
            } else {
                //释放锁
                //variable.releaseChannelLock(ipPort.ipPort());
                log.info("{}-连接失败", ipPort.ipPort());
            }
        });
    }

    /**
     * 等待退出
     */
    public void awaitSync() {
        try {
            /**等待所有客户端链路关闭*/
            this.startMods.getBootstrap().config().group().terminationFuture().sync();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            /**优雅退出，释放NIO线程组*/
            this.startMods.getGroup().shutdownGracefully();
        }
    }

    public Builder builder() {
        return new Builder();
    }

    /**
     * 缓存信息
     *
     * @return
     */
    public Variable variable() {
        return variable;
    }

    /**
     * 发送数据
     *
     * @param cmd
     * @param key
     */
    public void writeAndFlush(String cmd, String key) {
        byte[] sendByte = Convert.hexToBytes(cmd.replaceAll(" ", ""));
        ByteBuf reqByteBuf = Unpooled.buffer(sendByte.length);
        reqByteBuf.writeBytes(sendByte);
        this.variable.MAP_CHANNEL.get(key).writeAndFlush(reqByteBuf);
    }

    class Builder {

        /**
         * options
         */
        private LinkedHashMap<ChannelOption<?>, Object> options = new LinkedHashMap<>();

        /**
         * 业务处理管道
         */
        private List<Class<? extends ChannelHandler>> pipelines = new ArrayList<>();

        /**
         * 粘包拆包信息
         */
        private Unpacking unpacking = null;

        /**
         * 日志级别
         * 如果为null则不打印日志
         */
        private LogLevel logLevel = null;

        /**
         * 添加option
         *
         * @param option
         * @param value
         * @param <T>
         * @return
         */
        public <T> Builder addOption(ChannelOption<T> option, T value) {
            this.options.put(option, value);
            return this;
        }

        /**
         * 添加拆包粘包器
         *
         * @param unpacking
         * @return
         */
        public Builder addStickyPackageUnpacking(Unpacking unpacking) {
            this.unpacking = unpacking;
            return this;
        }

        /**
         * 添加日志处理器
         *
         * @param logLevel
         * @return
         */
        public Builder addLogLevel(LogLevel logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        /**
         * @param handler
         * @return
         */
        public Builder addPipeline(Class<? extends ChannelHandler> handler) {
            this.pipelines.add(handler);
            return this;
        }

        /**
         * 阻塞方法-启动netty客户端
         *
         * @return
         */
        public NettyStartMods start() {
            /**配置客户端 NIO 线程组/池*/
            EventLoopGroup group = new NioEventLoopGroup();
            /**Bootstrap 与 ServerBootstrap 都继承(extends)于 AbstractBootstrap
             * 创建客户端辅助启动类,并对其配置,与服务器稍微不同，这里的 Channel 设置为 NioSocketChannel
             * 然后为其添加 Handler，这里直接使用匿名内部类，实现 initChannel 方法
             * 作用是当创建 NioSocketChannel 成功后，在进行初始化时,将它的ChannelHandler设置到ChannelPipeline中，用于处理网络I/O事件*/
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class);
            //添加netty-option
            for (ChannelOption option : this.options.keySet()) {
                b.option(option, this.options.get(option));
            }
            //添加pipeline
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) {
                    //日志添加到首位
                    if (ObjectUtil.isNotNull(logLevel)) {
                        channel.pipeline().addLast(new LoggingHandler(logLevel));
                    }
                    //拆包次之
                    if (ObjectUtil.isNotNull(unpacking)) {
                        channel.pipeline().addLast(unpacking.buildHandler());
                    }
                    //如果为null则创建默认的
                    if (CollectionUtil.isEmpty(pipelines)) {
                        channel.pipeline().addLast(new NettyClientHandler());
                        return;
                    }
                    //按顺序添加处理器
                    for (Class<? extends ChannelHandler> handler : pipelines) {
                        try {
                            //创建管道处理实例
                            channel.pipeline().addLast(handler.newInstance());
                        } catch (InstantiationException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
            return new NettyStartMods(b, group);
        }
    }

}
