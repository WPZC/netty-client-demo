package org.example.nettyclientdemo.netty.thread;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;

import java.util.Properties;
import java.util.concurrent.*;

/**
 * 线程工厂
 * @author wqy
 * @version 1.0
 * @date 2020/8/13 16:12
 */
public abstract class ThreadFactory {

    //普通线程池
    public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR;
    //定时线程池
    public static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE;

    /**
     * 构建线程池
     * {@link ThreadPoolExecutor}
     */
    static {
        System.out.println("ThreadFactory初始化");
        //1:加载配置文件
        Resource app = new ClassPathResource("thread-configuration.yml");
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        // 2:将加载的配置文件交给 YamlPropertiesFactoryBean
        yamlPropertiesFactoryBean.setResources(app);
        // 3：将yml转换成 key：val
        System.out.println(yamlPropertiesFactoryBean.getObject());

        Properties properties = yamlPropertiesFactoryBean.getObject();

        //取出参数
        String param = properties.getProperty("thread.threadPoolExecutor.corePoolSize");
        //核心线程数
        Integer corePoolSize = param==null?null:Integer.valueOf(param);
        //最大线程数
        param = properties.getProperty("thread.threadPoolExecutor.maximumPoolSize");
        Integer maximumPoolSize = param==null?null:Integer.valueOf(param);
        //无任务销毁时间
        param = properties.getProperty("thread.threadPoolExecutor.keepAliveTime");
        Integer keepAliveTime = param==null?null:Integer.valueOf(param);

        if(corePoolSize==null||corePoolSize<1){
            corePoolSize = 1;
        }
        if(maximumPoolSize==null||maximumPoolSize<1){
            maximumPoolSize = 1;
        }
        if(keepAliveTime==null||keepAliveTime<0){
            keepAliveTime = 0;
        }
        if(maximumPoolSize<corePoolSize){
            System.out.println("最大线程数小于核心线程数，最大线程数初始化为核心线程数"+corePoolSize);
            maximumPoolSize = corePoolSize;
        }

        THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
                //核心线程（常驻线程）
                corePoolSize,
                //线程池最大容量
                maximumPoolSize,
                //无任务时存在时间
                keepAliveTime,
                //时间类型
                TimeUnit.SECONDS,
                //任务队列
                new LinkedBlockingQueue<>(),
                //线程命名
                r-> new Thread(r,"普通线程池-"+r.hashCode()),
                //拒绝策略
                new ThreadPoolExecutor.AbortPolicy());
        System.out.println("THREAD_POOL_EXECUTOR初始化完成,corePoolSize-"+corePoolSize+",maximumPoolSize-"+maximumPoolSize+",keepAliveTime-"+keepAliveTime);

        //初始化定时线程池
        param = properties.getProperty("thread.scheduledThreadPoolExecutor.corePoolSize");
        corePoolSize = param==null?null:Integer.valueOf(param);
        if(corePoolSize==null||corePoolSize<1){
            corePoolSize = 1;
        }
        SCHEDULED_EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(
                //核心线程数
                corePoolSize,
                //线程命名
                r-> new Thread(r,"定时线程池-"+r.hashCode()),
                //拒绝策略
                new ThreadPoolExecutor.AbortPolicy());
        System.out.println("SCHEDULED_EXECUTOR_SERVICE初始化完成,corePoolSize-"+corePoolSize);
    }


    /**
     * 创建线程池
     * @param corePoolSize
     * @param maximumPoolSize
     * @param poolName
     * @return
     */
    public static ThreadActuator createThreadPool(Integer corePoolSize,Integer maximumPoolSize,String poolName){

         return new ThreadActuator(
                 new ThreadPoolExecutor(
                         //核心线程（常驻线程）
                         corePoolSize,
                         //线程池最大容量
                         maximumPoolSize,
                         //无任务时存在时间
                         10,
                         //时间类型
                         TimeUnit.SECONDS,
                         //任务队列
                         new ArrayBlockingQueue<>(100),
                         //线程命名
                         r-> new Thread(r,poolName+"-"+r.hashCode()),
                         //拒绝策略
                         new ThreadPoolExecutor.DiscardPolicy())
         );

    }

    /**
     * 像普通线程池中提交任务
     * @param runnable
     */
    public static void threadPoolExecutorTask(Runnable runnable){

        THREAD_POOL_EXECUTOR.execute(runnable);

    }

    /**
     * 像普通线程池中提交任务
     * @param callable
     * @param <T>
     * @return
     */
    public static <T> Future<T> threadPoolExecutorTask(Callable<T> callable){

        Future future = THREAD_POOL_EXECUTOR.submit(callable);

        return future;

    }

    /**
     * 单词定时线程池
     * @param runnable
     * @param delay
     * @param unit
     */
    @NonNull
    public static void schedule(Runnable runnable,long delay,TimeUnit unit){
        SCHEDULED_EXECUTOR_SERVICE.schedule(runnable,delay,unit);
    }
    /**
     * 延时initialDelay以后执行
     * 是以上一个任务开始的时间计时，period时间过去后，检测上一个任务是否执行完毕，如果上一个任务执行完毕，则当前任务立即执行，如果上一个任务没有执行完毕，则需要等上一个任务执行完毕后立即执行。
     * @param runnable 任务
     * @param initialDelay 延时时间
     * @param period 多久执行一次
     * @param unit
     */
    @NonNull
    public static void scheduleAtFixedRate(Runnable runnable,long initialDelay,long period,TimeUnit unit){
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(runnable,initialDelay,period,unit);
    }

    /**
     * 是以上一个任务结束时开始计时，delay时间过去后，立即执行。
     * @param runnable
     * @param initialDelay 延时时间
     * @param delay 间隔
     * @param unit
     */
    @NonNull
    public static void scheduleWithFixedDelay(Runnable runnable,long initialDelay,long delay,TimeUnit unit){
        SCHEDULED_EXECUTOR_SERVICE.scheduleWithFixedDelay(runnable,initialDelay,delay,unit);
    }

    public static String toThreadString() {
        return "THREAD_POOL_EXECUTOR:{"+THREAD_POOL_EXECUTOR.toString()+"}"+"/"+"SCHEDULED_EXECUTOR_SERVICE:{"+ SCHEDULED_EXECUTOR_SERVICE.toString()+"}";
    }

//    String td = "核心线程数:"+THREAD_POOL_EXECUTOR.getCorePoolSize()+",最大线程数:"+THREAD_POOL_EXECUTOR.getMaximumPoolSize()+",活动线程数:"+THREAD_POOL_EXECUTOR.getActiveCount();
//        return "THREAD_POOL_EXECUTOR:{"+td+"}";
}
