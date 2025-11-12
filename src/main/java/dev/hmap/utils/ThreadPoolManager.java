package dev.hmap.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager {

    public static ThreadPoolManager threadPoolManager;

    public ExecutorService networkThreadPool = Executors.newFixedThreadPool(4);
}
