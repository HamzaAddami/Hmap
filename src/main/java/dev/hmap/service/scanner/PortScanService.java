package dev.hmap.service.scanner;

import dev.hmap.enums.ScanType;
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

public class PortScanService  {

    private final ThreadPoolManager threadPoolManager;

    public PortScanService(){
        this.threadPoolManager = ThreadPoolManager.getInstance();
    };



    public InetAddress resolveHost(String host) throws UnknownHostException {
        return InetAddress.getByName(host);
    }


    public Future<ScanResult> scanAsync(Host host, List<Integer> ports, ScanType scanType) {
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
        threadPoolManager.shutdown();
    }

}
