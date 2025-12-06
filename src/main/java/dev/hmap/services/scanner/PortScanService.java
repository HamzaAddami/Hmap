package dev.hmap.services.scanner;

import dev.hmap.models.Host;
import dev.hmap.models.Port;
import dev.hmap.models.ScanResult;
import dev.hmap.services.task.PortScanTask;
import dev.hmap.utils.ThreadPoolManager;
import org.apache.commons.net.telnet.TelnetClient;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class PortScanService {

    public static final List<Integer> COMMON_PORTS = List.of
            (20, 21, 22, 23, 25, 53, 67, 68, 69,
                    80, 110, 123, 135, 137, 138, 139,
                    143, 161, 389, 443, 445, 500, 512,
                    514, 515, 520, 587, 631, 636, 873,
                    902, 903, 993, 995);

    private static final int[] WELL_KNOWN_PORTS = generateRangePorts(1, 1024);
    private static final int SCAN_TIMEOUT_MS = 2000;



    private static int[] generateRangePorts(int start, int end){
        int[] result = new int[end - start + 1];
        int len = result.length;
        for (int i=0; i<len; i++){
            result[i] = start + 1;
            start++;
        }
        return result;
    }
    private byte[] dataPayload;

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

    public boolean isOpenPort(InetAddress address, int port) {

        TelnetClient telnetClient = new TelnetClient();
        telnetClient.setDefaultTimeout(SCAN_TIMEOUT_MS);
        try{
            telnetClient.connect(address, port);
            telnetClient.disconnect();
            return true;
        }catch (IOException e){
            return false;
        }
    }

    public ScanResult scan(InetAddress address, int port){


        return null;
    }


    public Future<ScanResult> scanAsync(Host host, List<Integer> ports, PortScanTask.ScanType scanType) {
        Callable<ScanResult> overallScan = () -> {
            ScanResult scanResult = new ScanResult(host);
            scanResult.setTotalPorts(ports.size());
            List<Future<Port>> portFutures = new ArrayList<>();

            for(int portNumber: ports){
                Port port = new Port(portNumber);
                scanResult.addScannedPort(port);
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
