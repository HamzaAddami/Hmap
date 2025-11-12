package dev.hmap.utils;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolManager {

    public static ThreadPoolManager threadPoolManager;

    private ExecutorService networkPool;
    private ExecutorService scannerPool;
    private ExecutorService backgroundPool;

    private final AtomicInteger activeTasks = new AtomicInteger(0);

    private ThreadPoolManager(){
        //  int cores = Runtime.getRuntime().availableProcessors();

        networkPool = new ThreadPoolExecutor(
                2,
                10,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                new NamedThreadFactory("Network"),
                new ThreadPoolExecutor.CallerRunsPolicy() // if full execute in calling Thread
        );

        scannerPool = new ThreadPoolExecutor(
                4,
                4,
                30L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new NamedThreadFactory("Scanner"),
                new ThreadPoolExecutor.AbortPolicy() // Reject if there is a lot of Tasks
        );

        backgroundPool = new ThreadPoolExecutor(
                1,
                3,
                120L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                new NamedThreadFactory("Background"),
                new ThreadPoolExecutor.DiscardOldestPolicy() // Reject the oldest Task
        );
    }

    public static synchronized ThreadPoolManager getThreadPoolManager(){
        if(threadPoolManager == null){
            threadPoolManager = new ThreadPoolManager();
        }
        return threadPoolManager;
    }

    public Future<?> executeNetworkTask(Runnable task){
        activeTasks.incrementAndGet();
        return networkPool.submit(() -> {
            try {
                task.run();
            } finally {
                activeTasks.decrementAndGet();
            }
        });
    }

    public Future<?> executeScanTask(Runnable task){
        activeTasks.incrementAndGet();
        return scannerPool.submit(() -> {
            try {
                task.run();
            } finally {
                activeTasks.decrementAndGet();
            }
        });
    }

    public Future<?> executeBackgroundTask(Runnable task){
        return backgroundPool.submit(task);
    }

    public void shutdown(){
        networkPool.shutdown();
        scannerPool.shutdown();
        backgroundPool.shutdown();

        try {
            if(!networkPool.awaitTermination(5, TimeUnit.SECONDS)){
                networkPool.shutdownNow();
            }
            if(!scannerPool.awaitTermination(5, TimeUnit.SECONDS)){
                scannerPool.shutdownNow();
            }
            if(!backgroundPool.awaitTermination(5, TimeUnit.SECONDS)){
                backgroundPool.shutdownNow();
            }
        }catch (InterruptedException e){
            networkPool.shutdownNow();
            scannerPool.shutdownNow();
            backgroundPool.shutdownNow();
            Thread.currentThread().interrupt();
            System.out.println("ThreadPoolManager stopped");
        }
    }

    public void shutdownNow(){
        networkPool.shutdownNow();
        scannerPool.shutdownNow();
        backgroundPool.shutdownNow();
        Thread.currentThread().interrupt();
        System.out.println("ThreadPoolManager stopped");
    }

    // Statistics
    public int getActiveTaskCount(){
        return activeTasks.get();
    }

    public String getStatus(){
        return String.format(
                "Active Threads: %d | Network: %d/%d | Scanner: %d/%d | Background: %d/%d",
                activeTasks.get(),
                ((ThreadPoolExecutor) networkPool).getActiveCount(),
                ((ThreadPoolExecutor) networkPool).getPoolSize(),
                ((ThreadPoolExecutor) scannerPool).getActiveCount(),
                ((ThreadPoolExecutor) scannerPool).getPoolSize(),
                ((ThreadPoolExecutor) backgroundPool).getActiveCount(),
                ((ThreadPoolExecutor) backgroundPool).getPoolSize()
        );
    }

    private static class NamedThreadFactory implements ThreadFactory{
        private final String name;
        private final AtomicInteger counter = new AtomicInteger(1);

        public NamedThreadFactory(String name){
            this.name = name;
        }

        @Override
        public Thread newThread(Runnable runnable){
            Thread thread = new Thread(runnable, name + "-" + counter.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        }
    }


}
