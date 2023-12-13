package org.example.nettyclientdemo.netty.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程执行器
 * 统一执行器，方便统一扩展
 */
@Slf4j
public class ThreadActuator {

    private final ThreadPoolExecutor threadPoolExecutor;

    public ThreadActuator(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    /**
     * 像普通线程池中提交任务
     * @param runnable
     */
    public void threadPoolExecutorTask(Runnable runnable){
        log.info("{}任务执行:{}",runnable.toString(),runnable.toString());
        try {
            Future future = threadPoolExecutor.submit(runnable);
            //future.get(500, TimeUnit.MILLISECONDS);
        }catch (Exception e){
            log.error("{}任务执行异常",runnable.toString());
            log.error("{}:"+e.toString(),runnable.toString());
            log.error("{}:"+e.getStackTrace()[0].toString(),runnable.toString());
            e.printStackTrace();
        }

    }

    /**
     * 像普通线程池中提交任务
     * @param callable
     * @param <T>
     * @return
     */
    public <T> Future<T> threadPoolExecutorTask(Callable<T> callable){

        Future future = threadPoolExecutor.submit(callable);

        return future;

    }

    public String toThreadString() {
            return "ThreadActuator:{"+threadPoolExecutor.toString()+"}";
    }

}
