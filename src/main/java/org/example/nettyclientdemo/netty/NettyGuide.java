package org.example.nettyclientdemo.netty;

import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import org.example.nettyclientdemo.netty.cache.Variable;
import org.example.nettyclientdemo.netty.thread.ThreadFactory;
import org.example.nettyclientdemo.netty.unpack.Unpacking;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Netty引导任务
 *
 * @author WQY
 * @version 1.0
 * @date 2023/12/3 10:21
 */
public class NettyGuide {

    private static Map<PtEnum, NettySupport> NETTY_GUIDE = new HashMap<>();


    static {
        //此处增加协议
        //1.主控板协议处理集合
        NettySupport supportBoard = new NettySupport();
        //缓存引导数据
        NETTY_GUIDE.put(PtEnum.MB, supportBoard);
        //初始化链接
        startMainBoardClient(ThreadFactory.THREAD_POOL_EXECUTOR);


        //2.PLC处理集合
    }

    /**
     * 获取缓存值
     *
     * @param ptEnum
     * @return
     */
    public static Variable variable(PtEnum ptEnum) {
        return NettyGuide.NETTY_GUIDE.get(ptEnum).variable();
    }

    /**
     * 获取support
     *
     * @param ptEnum
     * @return
     */
    public static NettySupport support(PtEnum ptEnum) {
        return NettyGuide.NETTY_GUIDE.get(ptEnum);
    }

    /**
     * 连接
     * @param ipPort    地址
     * @param ptEnum    类型
     */
    public static void connect(IpPort ipPort, PtEnum ptEnum) {
        //获取netty_support
        NettySupport support = NettyGuide.NETTY_GUIDE.get(ptEnum);
        //连接
        support.connect(ipPort);
    }

    /**
     * 连接主控板netty_client
     */
    private static void startMainBoardClient(ThreadPoolExecutor executor) {
        NettySupport support = NettyGuide.NETTY_GUIDE.get(PtEnum.MB);
        NettyStartMods mods = support.builder()
                //添加option
                .addOption(ChannelOption.TCP_NODELAY, true)
                .addOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000 * 5)
                //添加处理器
                .addLogLevel(LogLevel.INFO)
                .addStickyPackageUnpacking(new Unpacking(Integer.MAX_VALUE,3,2))
                .addPipeline(NettyClientHandler.class)
//                .addPipeline(ReadC3Handler.class)
//                .addPipeline(ReadA3Handler.class)
//                .addPipeline(ReadA4Handler.class)
//                .addPipeline(ReadA5handler.class)
//                .addPipeline(WriteA5Handler.class)
                //启动netty_client
                .start();
        //异步等待
        executor.execute(()->{
            //等待关闭
            support.initStartMods(mods).awaitSync();
        });
    }

    /**
     * 启动PLC控制板
     *
     * @param ip
     * @param port
     */
    private void connectNettyClientPlc(String ip, Integer port) {

    }

}
