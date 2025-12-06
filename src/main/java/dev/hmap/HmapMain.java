package dev.hmap;

import dev.hmap.models.Host;
import dev.hmap.models.Port;
import dev.hmap.models.ScanResult;
import dev.hmap.services.scanner.PortScanService;
import dev.hmap.services.task.PortScanTask;
import dev.hmap.utils.ThreadPoolManager;
import java.net.*;

import java.util.concurrent.*;


public class HmapMain {

//    @Override
//    public void start(Stage stage) throws IOException {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dev/hmap/fxml/MainView.fxml"));
//        Parent root = loader.load();
//        Scene scene = new Scene(root, 1200, 800);
//        stage.setScene(scene);
//        stage.setTitle("Hmap");
//        stage.show();
//
//    }

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        // launch(args);

        long d1 = System.currentTimeMillis();
        String ip = InetAddress.getLocalHost().getHostName();
        Host host = new Host("192.168.1.2");

        ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();

        PortScanService portScanService = new PortScanService();

        Future<ScanResult>  result = portScanService.scanAsync(host, PortScanService.COMMON_PORTS, PortScanTask.ScanType.TCP_CONNECT);
        try {
            ScanResult finalResult = result.get();
            System.out.println(finalResult.getSummary());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

    }


    public static class Task implements Runnable{
        @Override
        public void run(){
            System.out.println(Thread.currentThread().getName());
        }
    }

    public static boolean isOpen(String ip, int port, int timeout){
        try{
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), timeout);
            socket.close();
            return true;

        }catch (Exception e){
            return false;
        }
    }



}

