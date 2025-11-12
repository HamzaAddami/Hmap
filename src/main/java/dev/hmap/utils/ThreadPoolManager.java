package dev.hmap.utils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolManager {
    // Singleton
    private static ThreadPoolManager instance;

    ExecutorService networkPool;
    ExecutorService scanPool;
    ExecutorService backgroundPool;

    public final AtomicInteger activeTasks = new AtomicInteger(0);

    private ThreadPoolManager(){

        int cores = Runtime.getRuntime().availableProcessors();

        networkPool = new ThreadPoolExecutor(
                cores,
                cores * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                new NamedThreadFactory("Network"),
                new ThreadPoolExecutor.CallerRunsPolicy() // if full execute in calling Thread
        );

        scanPool = new ThreadPoolExecutor(
                cores,
                cores * 2,
                30L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new NamedThreadFactory("Scanner"),
                new ThreadPoolExecutor.AbortPolicy() // Reject if there is a lot of Tasks
        );

        backgroundPool = new ThreadPoolExecutor(
                2,
                10,
                120L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new NamedThreadFactory("Background"),
                new ThreadPoolExecutor.DiscardOldestPolicy() // Reject the oldest Task
        );
    }

    // Methods -----------------------------------

    // Singleton
    public static synchronized ThreadPoolManager getInstance() {
        if(instance == null){
            instance = new ThreadPoolManager();
        }
        return instance;
    }

    public Future<?> executeNetworkTasks(Runnable task) {
        return networkPool.submit(() -> {
            activeTasks.incrementAndGet();
            try{
                task.run();
            } finally {
                activeTasks.decrementAndGet();
            }
        });
    }

    public Future<?> executeScanTasks(Runnable task){
        return scanPool.submit(() -> {
            activeTasks.incrementAndGet();
            try{
                task.run();
            }finally {
                activeTasks.decrementAndGet();
            }
        });
    }

    public Future<?> executeBackgroundTasks(Runnable task){
        return backgroundPool.submit(task);
    }

    public void shutdown() {

        networkPool.shutdown();
        scanPool.shutdown();
        backgroundPool.shutdown();

        try {
            if (!networkPool.awaitTermination(5, TimeUnit.SECONDS)){
                networkPool.shutdownNow();
            }
            if (!scanPool.awaitTermination(5, TimeUnit.SECONDS)){
                scanPool.shutdownNow();
            }
            if (!backgroundPool.awaitTermination(5, TimeUnit.SECONDS)){
                backgroundPool.shutdownNow();
            }

        } catch(InterruptedException e){
            networkPool.shutdownNow();
            scanPool.shutdownNow();
            backgroundPool.shutdownNow();
            Thread.currentThread().interrupt();
            System.out.println("Threads stopped");
        }
    }

    public void shutdownNow() {
        networkPool.shutdownNow();
        scanPool.shutdownNow();
        backgroundPool.shutdownNow();
        Thread.currentThread().interrupt();
    }

    // Statistics
    public int getActiveTasksCount() {
        return activeTasks.get();
    }

    public String getSummary() {
        return String.format(
                "Active Threads: %d | Network: %d/%d | Scanner: %d/%d | Background: %d/%d",
                activeTasks.get(),
                ((ThreadPoolExecutor) networkPool).getActiveCount(),
                ((ThreadPoolExecutor) networkPool).getPoolSize(),
                ((ThreadPoolExecutor) scanPool).getActiveCount(),
                ((ThreadPoolExecutor) scanPool).getPoolSize(),
                ((ThreadPoolExecutor) backgroundPool).getActiveCount(),
                ((ThreadPoolExecutor) backgroundPool).getPoolSize()
        );
    }

    // Implementing NamedThreadFactory for creating named Threads
    public static class NamedThreadFactory implements ThreadFactory {
        public String name;
        public AtomicInteger counter = new AtomicInteger(1);

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