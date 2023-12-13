package org.example.nettyclientdemo.netty.cache;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 对接平台缓存
 * @Author: wqy
 * @Date: 2019/9/18 14:31
 */
@Slf4j
public class Variable {

    /**
     * 通道缓存
     */
    public final HashMap<String, ChannelHandlerContext> MAP_CHANNEL = new HashMap<>();

//    /**
//     * 设置缓存
//     * key：设备地址(/ip:port)
//     */
//    public final HashMap<String, Map> DEVICE_MAP = new HashMap<>();

    /**
     * ip:port锁
     */
    public final Map<String, ReentrantLock> IP_PORT_LOCK = new ConcurrentHashMap<>();

    /**
     * ip:port通道锁
     * 用于识别出通道正在连接
     * 结合IP_PORT_LOCK实现双重锁
     */
    public final Map<String, ReentrantLock> IP_PORT_CHANNEL_LOCK = new ConcurrentHashMap<>();

    /**
     * mapChannel
     * map中remove方法
     *
     * @param key
     */
    public void mapChannelRemove(String key) {
        //移除通道数据
        this.MAP_CHANNEL.remove(key);
        //移除缓存的时候尝试释放锁
        //this.releaseChannelLock(key);
    }

    /**
     * mapChannel
     * map中put方法
     *
     * @param key
     */
    public void mapChannelPut(String key, ChannelHandlerContext ctx) {
        //加入通道
        this.MAP_CHANNEL.put(key,ctx);
    }

    /**
     * 根据ip:port进行加锁
     *  key:为ip:port
     */
    public void acquireLock(String key) throws InterruptedException {
        Lock lock = this.IP_PORT_LOCK.computeIfAbsent(key, k -> new ReentrantLock());
        lock.tryLock(5, TimeUnit.SECONDS);
    }

    /**
     * 根据ip:port进行解锁
     */
    public void releaseLock(String key) {
        ReentrantLock lock = this.IP_PORT_LOCK.get(key);
        if (lock != null) {
            //判断是否该线程持有锁
            if (lock.isHeldByCurrentThread()){
                log.debug("{}-IP_PORT_LOCK释放锁",key);
                //移除站位锁
                this.IP_PORT_LOCK.remove(key);
                lock.unlock();
            }
        }
    }

}
