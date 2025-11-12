package dev.hmap;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class HmapMain  {

//    @Override
//    public void start(Stage stage) {
//        Circle circle = new Circle(20, 20, 20);
//        Group root = new Group(circle);
//        Scene scene = new Scene(root, 400, 300);
//        stage.setScene(scene);
//        stage.setTitle("Hmap");
//        stage.show();
//    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger index = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "ThreadFac" +  index.getAndIncrement());
                thread.setDaemon(false);
                return thread;
            }
        };

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10,
                10,
                2000,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                threadFactory
                );

       Future<?> future = executor.submit(new Task());

        future.get();
        executor.shutdown();
        System.out.println(Runtime.getRuntime().availableProcessors());

        InetAddress ip = InetAddress.getLocalHost();
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(ip);

        byte[] mac = networkInterface.getHardwareAddress();

        System.out.println(mac);

        InetAddress ip = new InetAddress();


    }

    public static class Task implements Runnable{
        @Override
        public void run(){
            System.out.println(Thread.currentThread().getName());
        }
    }



}

