package dev.hmap.service.scanner;

import dev.hmap.model.Host;
import dev.hmap.model.Port;
import dev.hmap.model.ScanResult;
import dev.hmap.service.task.PortScanTask;
import dev.hmap.config.ThreadPoolManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class PortScanService {

    public static final List<Integer> COMMONS_UDP_PORTS = List.of
            (
            53, 67, 68, 69, 123, 137, 138, 139,161,
                    162, 445, 500, 514, 520, 1194, 1900, 3478,
                    4500, 5353, 5060, 6881, 10000, 17185, 27015,
                    3702, 4500, 5353
            );

    public static final List<Integer> COMMON_PORTS = List.of
            (20, 21, 22, 23, 25, 80, 110,
                    111, 123, 135, 137, 138, 139, 143,
                    161, 389, 443, 445, 500, 512, 514,
                    515, 520, 587, 631, 636, 873, 902,
                    903, 993, 995, 1900, 5357, 8081, 49152,
                    62078, 65001, 3000, 3306
            );

    public static final List<Integer> WELL_KNOWN_PORTS = generateRangePorts(1, 100);
    private static final int SCAN_TIMEOUT_MS = 2000;



    private static List<Integer> generateRangePorts(int start, int end){
        List<Integer> result = new ArrayList<>();
        for (int i=start; i<end; i++){
            result.add(i);
            start++;
        }
        return result;
    }
//    private byte[] dataPayload;

    private final ThreadPoolManager threadPoolManager;

    private Consumer<String> onSuccess;
    private Consumer<String> onErrors;
    private Future<?> portScanner;

    public PortScanService(){
        this.threadPoolManager = ThreadPoolManager.getInstance();
    };



    public InetAddress resolveHost(String host) throws UnknownHostException {
        return InetAddress.getByName(host);
    }

    public Future<ScanResult> scanAsync(Host host, List<Integer> ports, PortScanTask.ScanType scanType) {
        Callable<ScanResult> overallScan = () -> {
            ScanResult scanResult = new ScanResult(host);
            List<Future<Port>> portFutures = new ArrayList<>();

            for(int portNumber: ports){
                Port port = new Port(portNumber);
                PortScanTask scanTask = new PortScanTask(host, port, scanType);
                Future<Port> future = threadPoolManager.executeScanTasks(scanTask);
                portFutures.add(future);
            }

            for(Future<Port> future: portFutures){
                try {
                    Port scannedPort = future.get();
                    scanResult.addScannedPort(scannedPort);
                }catch (InterruptedException | ExecutionException e){
                    System.err.println("Getting scan task ERROR : " + e.getMessage());
                }
            }
            scanResult.finalizeScan();
            return scanResult;
        };
        return threadPoolManager.executeScanTasks(overallScan);
    }

    public void shutdown() {
    }

}
