package com.hustunique.simpleimageloader.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yifan on 2/24/17.
 */

public class LoaderThreadPoolExecutor extends ThreadPoolExecutor {
    private static LoaderThreadPoolExecutor executor;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, CPU_COUNT - 1);
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2;
    private static final LinkedBlockingDeque<Runnable> queue = new LinkedBlockingDeque<>();
    private static final long KEEP_ALIVE = 10L;


    public static LoaderThreadPoolExecutor getInstance() {
        if (executor == null) {
            synchronized (LoaderThreadPoolExecutor.class) {
                if (executor == null) {
                    executor = new LoaderThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                            KEEP_ALIVE, TimeUnit.SECONDS, queue, threadFactory);
                }
            }
        }
        return executor;
    }

    private LoaderThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
                                    long keepAliveTime, TimeUnit unit,
                                    BlockingQueue<Runnable> workQueue,
                                    ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    private static final ThreadFactory threadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "MyImageLoaderTest#" + mCount.getAndIncrement());
        }
    };
}
