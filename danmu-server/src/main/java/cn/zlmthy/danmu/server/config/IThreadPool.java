package cn.zlmthy.danmu.server.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zengliming
 * @date 2019/5/26
 * @since 1.0.0
 */
public class IThreadPool {

    private static final int CORE = 1;
    private static final int MAX = 5;
    private static final int ALIVE_TIME = 60;

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE, MAX, ALIVE_TIME, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));

    public static void execute(Runnable runnable){
        threadPoolExecutor.execute(runnable);
    }
}
